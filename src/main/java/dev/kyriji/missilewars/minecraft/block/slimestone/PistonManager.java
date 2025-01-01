package dev.kyriji.missilewars.minecraft.block.slimestone;

import dev.kyriji.missilewars.minecraft.block.slimestone.nms.PistonHandler;
import dev.kyriji.missilewars.minecraft.block.state.properties.Facing;
import dev.kyriji.missilewars.minecraft.block.state.properties.PistonExtended;
import dev.kyriji.missilewars.minecraft.block.state.properties.PistonType;
import dev.kyriji.missilewars.minecraft.block.update.BlockUpdateHandler;
import dev.kyriji.missilewars.minecraft.block.util.BlockUtil;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

import java.util.*;

public class PistonManager {
	private static PistonManager INSTANCE;

	public final Map<BlockVec, PushType> scheduledExtensions = new HashMap<>();
	public final List<BlockMoveTask> blockMoveTasks = new ArrayList<>();

	private PistonManager() {}

	public void handleScheduledMoves(Instance instance) {
		long serverTick = instance.getWorldAge();

		for (BlockMoveTask task : new ArrayList<>(blockMoveTasks)) {
			if (task.getInstance() != instance) continue;
			if (task.getCreationTick() + 3 == serverTick || task.shouldRunImmediately()) {
				System.out.println("setting blocks: " + BlockUpdateHandler.lastStoneTick + " " + task.shouldRunImmediately());
				if (scheduledExtensions.get(task.getPistonVec()) == PushType.PUSH) scheduledExtensions.remove(task.getPistonVec());
				if (task.isExtending()) {
					if (task.hasMoveBlocksRunnable()) {
						task.getMoveBlocksRunnable().run();
					} else {
						System.out.println("no move blocks runnable");
					}
					// task.getUpdatePistonRunnable().run();
				} else {
					if (task.hasMoveBlocksRunnable()) {
						task.getMoveBlocksRunnable().run();
					} else {
						System.out.println("no move blocks runnable");
					}
					// task.getUpdatePistonRunnable().run();
				}
				blockMoveTasks.remove(task);
			}
		}
	}

	public boolean canPush(Instance instance, BlockVec pistonVec) {
		Block pistonBlock = instance.getBlock(pistonVec);
		Block defaultBlock = pistonBlock.defaultState();
		if (defaultBlock != Block.PISTON && defaultBlock != Block.STICKY_PISTON) return false;

		Direction direction = Facing.fromBlock(pistonBlock).toDirection();
		List<BlockVec> pushBlocks = getPushBlocks(instance, pistonVec, direction);
		for (BlockVec pushBlock : pushBlocks) {
			if (pushBlock.equals(pistonVec)) {
				System.out.println("piston pushing itself, aborting");
				return false;
			}
			if (SpecialBlockManager.get().isImmovable(instance.getBlock(pushBlock))) return false;
		}
		return true;
	}

	public boolean canPull(Instance instance, BlockVec pistonVec) {
		Block pistonBlock = instance.getBlock(pistonVec);
		Block defaultBlock = pistonBlock.defaultState();
		if (defaultBlock != Block.STICKY_PISTON) return false;

		Direction direction = Facing.fromBlock(pistonBlock).toDirection();
		List<BlockVec> pullBlocks = getPullBlocks(instance, pistonVec, direction.opposite());
		for (BlockVec pullBlock : pullBlocks) {
			if (pullBlock.equals(pistonVec)) {
				System.out.println("piston pulling itself, aborting");
				return false;
			}
			if (SpecialBlockManager.get().isImmovable(instance.getBlock(pullBlock))) return false;
		}
		return true;
	}

	public void UNSAFE_push(Instance instance, BlockVec pistonVec, BlockMoveTask task) {
		Block pistonBlock = instance.getBlock(pistonVec);
		Direction direction = Facing.fromBlock(pistonBlock).toDirection();

		Facing facing = Facing.fromBlock(pistonBlock);
		BlockVec headVec = facing.offset(pistonVec);
		Block head = Block.PISTON_HEAD
				.withProperty("facing", facing.getValue())
				.withProperty("type", pistonBlock.defaultState() == Block.PISTON ?
						PistonType.NORMAL.getValue() : PistonType.STICKY.getValue());

		Map<BlockVec, Block> pushBlocks = new LinkedHashMap<>();
		PistonHandler pistonHandler = new PistonHandler(instance, pistonVec, direction, true);
		boolean success = pistonHandler.calculatePush();
		if (!success) {
			System.out.println("PUSH FAILED");
			scheduledExtensions.remove(pistonVec);
			blockMoveTasks.remove(task);
			return;
		}
		for (BlockVec blockVec : pistonHandler.getMovedBlocks().reversed())
			pushBlocks.put(blockVec, instance.getBlock(blockVec));

		for (Map.Entry<BlockVec, Block> entry : pushBlocks.entrySet()) {
			instance.setBlock(entry.getKey(), Block.AIR);
			// BlockUpdateHandler.get().scheduleUpdateImmediately(instance, entry.getKey());
		}

		Runnable runnable = () -> {
			System.out.println("pushing " + pushBlocks.size() + " blocks");

			List<Runnable> updates = new ArrayList<>();
			for (Map.Entry<BlockVec, Block> entry : pushBlocks.entrySet()) {
				BlockVec nextBlockVec = entry.getKey().add(new BlockVec(direction.vec()));
				instance.setBlock(nextBlockVec, entry.getValue());
				// updates.add(() -> {
				// 	BlockUpdateHandler.get().scheduleUpdate(instance, nextBlockVec);
				// });
				BlockUpdateHandler.get().scheduleUpdateImmediately(instance, nextBlockVec);
			}

			instance.setBlock(pistonVec, pistonBlock.withProperty("extended", PistonExtended.EXTENDED.getValue()));
			instance.setBlock(headVec, head);
			BlockUpdateHandler.get().scheduleUpdateImmediately(instance, pistonVec);
			BlockUpdateHandler.get().scheduleUpdateImmediately(instance, headVec);
			// BlockUpdateHandler.get().scheduleUpdate(instance, pistonVec);
			// BlockUpdateHandler.get().scheduleUpdate(instance, headVec);
			for (Runnable update : updates) update.run();
		};

		getScheduledMove(instance, pistonVec).setMoveBlocksRunnable(runnable);
	}

	public void UNSAFE_pull(Instance instance, BlockVec pistonVec, BlockMoveTask task) {
		Block pistonBlock = instance.getBlock(pistonVec);
		Direction direction = Facing.fromBlock(pistonBlock).toDirection();
		PistonType pistonType = PistonType.fromBlock(pistonBlock);

		Facing facing = Facing.fromBlock(pistonBlock);
		BlockVec headVec = facing.offset(pistonVec);

		instance.setBlock(pistonVec, pistonBlock.withProperty("extended", PistonExtended.RETRACTED.getValue()));
		instance.setBlock(headVec, Block.AIR);
		BlockUpdateHandler.get().scheduleUpdate(instance, pistonVec);
		BlockUpdateHandler.get().scheduleUpdate(instance, headVec);

		Map<BlockVec, Block> pullBlocks = new LinkedHashMap<>();
		if (pistonType == PistonType.STICKY) {
			PistonHandler pistonHandler = new PistonHandler(instance, pistonVec, direction, false);
			boolean success = pistonHandler.calculatePush();
			if (!success) {
				System.out.println("PULL FAILED");
				scheduledExtensions.remove(pistonVec);
				blockMoveTasks.remove(task);
				return;
			}
			for (BlockVec blockVec : pistonHandler.getMovedBlocks().reversed())
				pullBlocks.put(blockVec, instance.getBlock(blockVec));

			for (Map.Entry<BlockVec, Block> entry : pullBlocks.entrySet()) {
				instance.setBlock(entry.getKey(), Block.AIR);
				BlockUpdateHandler.get().scheduleUpdateImmediately(instance, entry.getKey());
			}
		}

		Runnable runnable = () -> {
			if (pistonType == PistonType.STICKY) {
				System.out.println("pulling " + pullBlocks.size() + " blocks");

				for (Map.Entry<BlockVec, Block> entry : pullBlocks.entrySet()) {
					BlockVec nextBlockVec = entry.getKey().add(new BlockVec(direction.opposite().vec()));
					instance.setBlock(nextBlockVec, entry.getValue());
					BlockUpdateHandler.get().scheduleUpdateImmediately(instance, nextBlockVec);
				}
			}

			if (scheduledExtensions.get(task.getPistonVec()) == PushType.PULL)
				scheduledExtensions.remove(pistonVec); // TODO: this is a terrible spot to put this
			BlockUpdateHandler.get().scheduleUpdate(instance, pistonVec);
		};

		getScheduledMove(instance, pistonVec).setMoveBlocksRunnable(runnable);
	}

	public List<BlockVec> getPushBlocks(Instance instance, BlockVec pistonVec, Direction direction) {
		List<BlockVec> pushBlocks = new ArrayList<>();
		BlockVec currentVec = pistonVec.add(new BlockVec(direction.vec()));
		List<BlockVec> sourceVecs = List.of(pistonVec);
		return getConnectedBlockHelper(pushBlocks, currentVec, BlockMoveType.PUSH, instance, sourceVecs, direction);
	}

	public List<BlockVec> getPullBlocks(Instance instance, BlockVec pistonVec, Direction direction) {
		List<BlockVec> pushBlocks = new ArrayList<>();
		BlockVec currentVec = pistonVec.add(new BlockVec(direction.vec().mul(-2)));
		List<BlockVec> sourceVecs = List.of(pistonVec, pistonVec.add(new BlockVec(direction.vec().mul(-1))));
		return getConnectedBlockHelper(pushBlocks, currentVec, BlockMoveType.PUSH, instance, sourceVecs, direction);
	}

	public List<BlockVec> getConnectedBlockHelper(List<BlockVec> pushBlocks, BlockVec currentVec, BlockMoveType blockMoveType,
												  Instance instance, List<BlockVec> sourceVecs, Direction direction) {
		if (pushBlocks.contains(currentVec)) return pushBlocks;
		for (BlockVec sourceVec : sourceVecs)
			if (sourceVec.equals(currentVec)) return pushBlocks;
		Block defaultBlock = instance.getBlock(currentVec).defaultState();

		if (!SpecialBlockManager.get().isFluff(defaultBlock)) {
			// if the block isn't fluffy, check if it's movable and we're pushing
			if (!SpecialBlockManager.get().isImmovable(defaultBlock) || blockMoveType == BlockMoveType.PUSH) pushBlocks.add(currentVec);
		} else {
			// if the block is fluffy don't add it to the list
			return pushBlocks;
		}

		BlockVec nextBlockVec = currentVec.add(new BlockVec(direction.vec()));
		Block nextBlock = instance.getBlock(nextBlockVec);
		getConnectedBlockHelper(pushBlocks, nextBlockVec, BlockMoveType.PUSH, instance, sourceVecs, direction);

		if (SpecialBlockManager.get().isSticky(defaultBlock)) {
			List<BlockVec> neighborBlockVectors = BlockUtil.getNeighborBlocks(currentVec);
			// TODO: switch out for the order which minecraft moves blocks
			for (BlockVec neighborVec : neighborBlockVectors)
				getConnectedBlockHelper(pushBlocks, neighborVec, BlockMoveType.STICKY, instance, sourceVecs, direction);
			return pushBlocks;
		}

		return pushBlocks;
	}

	public BlockMoveTask getScheduledMove(Instance instance, BlockVec pistonVec) {
		for (BlockMoveTask blockMoveTask : blockMoveTasks)
			if (blockMoveTask.getInstance() == instance && blockMoveTask.getPistonVec().equals(pistonVec)) return blockMoveTask;
		return null;
	}

	public BlockMoveTask scheduleMove(Instance instance, BlockVec pistonVec, boolean isExtending, Runnable runnable) {
		BlockMoveTask task = new BlockMoveTask(instance, pistonVec, isExtending, runnable);
		blockMoveTasks.add(task);
		return task;
	}

	public static void init() {
		if (INSTANCE != null) throw new IllegalStateException();
		INSTANCE = new PistonManager();
	}

	public static PistonManager get() {
		return INSTANCE;
	}
}
