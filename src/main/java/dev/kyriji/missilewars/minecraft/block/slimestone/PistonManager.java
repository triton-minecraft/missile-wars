package dev.kyriji.missilewars.minecraft.block.slimestone;

import dev.kyriji.missilewars.minecraft.block.state.properties.Facing;
import dev.kyriji.missilewars.minecraft.block.update.BlockUpdateHandler;
import dev.kyriji.missilewars.minecraft.block.util.BlockUtils;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

import java.util.ArrayList;
import java.util.List;

public class PistonManager {
	private static PistonManager INSTANCE;

	private final List<BlockMoveTask> blockMoveTasks = new ArrayList<>();

	private PistonManager() {}

	public void handleScheduledMoves(Instance instance) {
		long serverTick = instance.getWorldAge();

		for (BlockMoveTask task : new ArrayList<>(blockMoveTasks)) {
			if (task.getInstance() != instance) continue;
			if (task.getCreationTick() + 2 == serverTick) {
				if (task.isExtending()) {
					if (task.hasMoveBlocksRunnable()) task.getMoveBlocksRunnable().run();
					task.getUpdatePistonRunnable().run();
				} else {
					task.getUpdatePistonRunnable().run();
					if (task.hasMoveBlocksRunnable()) task.getMoveBlocksRunnable().run();
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
				System.out.println("pushing piston, aborting");
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
				System.out.println("pulling piston, aborting");
				return false;
			}
			if (SpecialBlockManager.get().isImmovable(instance.getBlock(pullBlock))) return false;
		}
		return true;
	}

	public void UNSAFE_push(Instance instance, BlockVec pistonVec) {
		Block pistonBlock = instance.getBlock(pistonVec);
		Direction direction = Facing.fromBlock(pistonBlock).toDirection();
		List<BlockVec> pushBlocks = getPushBlocks(instance, pistonVec, direction);

		System.out.println("pushing " + pushBlocks.size() + " blocks");

		pushBlocks = pushBlocks.reversed();
		List<BlockVec> finalPushBlocks = pushBlocks;
		Runnable runnable = () -> {
			for (BlockVec pushVec : finalPushBlocks) {
				BlockVec nextBlockVec = pushVec.add(new BlockVec(direction.vec()));
				instance.setBlock(nextBlockVec, instance.getBlock(pushVec));
				instance.setBlock(pushVec, Block.AIR);
				BlockUpdateHandler.get().scheduleUpdate(instance, pushVec);
				BlockUpdateHandler.get().scheduleUpdate(instance, nextBlockVec);
			}
		};

		getScheduledMove(instance, pistonVec).setMoveBlocksRunnable(runnable);
	}

	public void UNSAFE_pull(Instance instance, BlockVec pistonVec) {
		Block pistonBlock = instance.getBlock(pistonVec);
		Direction direction = Facing.fromBlock(pistonBlock).toDirection().opposite();
		List<BlockVec> pullBlocks = getPullBlocks(instance, pistonVec, direction);

		System.out.println("pulling " + pullBlocks.size() + " blocks");

		Runnable runnable = () -> {
			for (BlockVec pullVec : pullBlocks) {
				BlockVec nextBlockVec = pullVec.add(new BlockVec(direction.vec()));
				instance.setBlock(nextBlockVec, instance.getBlock(pullVec));
				instance.setBlock(pullVec, Block.AIR);
				BlockUpdateHandler.get().scheduleUpdate(instance, pullVec);
				BlockUpdateHandler.get().scheduleUpdate(instance, nextBlockVec);
			}
		};

		getScheduledMove(instance, pistonVec).setMoveBlocksRunnable(runnable);
	}

	public List<BlockVec> getPushBlocks(Instance instance, BlockVec pistonVec, Direction direction) {
		List<BlockVec> pushBlocks = new ArrayList<>();
		BlockVec currentVec = pistonVec.add(new BlockVec(direction.vec()));
		List<BlockVec> sourceVecs = List.of(pistonVec);
		return getConnectedBlockHelper(pushBlocks, currentVec, PushType.PUSH, instance, sourceVecs, direction);
	}

	public List<BlockVec> getPullBlocks(Instance instance, BlockVec pistonVec, Direction direction) {
		List<BlockVec> pushBlocks = new ArrayList<>();
		BlockVec currentVec = pistonVec.add(new BlockVec(direction.vec().mul(-2)));
		List<BlockVec> sourceVecs = List.of(pistonVec, pistonVec.add(new BlockVec(direction.vec().mul(-1))));
		return getConnectedBlockHelper(pushBlocks, currentVec, PushType.PUSH, instance, sourceVecs, direction);
	}

	public List<BlockVec> getConnectedBlockHelper(List<BlockVec> pushBlocks, BlockVec currentVec, PushType pushType,
												  Instance instance, List<BlockVec> sourceVecs, Direction direction) {
		if (pushBlocks.contains(currentVec)) return pushBlocks;
		for (BlockVec sourceVec : sourceVecs)
			if (sourceVec.equals(currentVec)) return pushBlocks;
		Block defaultBlock = instance.getBlock(currentVec).defaultState();

		if (!SpecialBlockManager.get().isFluff(defaultBlock)) {
			// if the block isn't fluffy, check if it's movable and we're pushing
			if (!SpecialBlockManager.get().isImmovable(defaultBlock) || pushType == PushType.PUSH) pushBlocks.add(currentVec);
		} else {
			// if the block is fluffy don't add it to the list
			return pushBlocks;
		}

		BlockVec nextBlockVec = currentVec.add(new BlockVec(direction.vec()));
		Block nextBlock = instance.getBlock(nextBlockVec);
		getConnectedBlockHelper(pushBlocks, nextBlockVec, PushType.PUSH, instance, sourceVecs, direction);

		if (SpecialBlockManager.get().isSticky(defaultBlock)) {
			List<BlockVec> neighborBlockVectors = BlockUtils.getNeighborBlocks(currentVec);
			// TODO: switch out for the order which minecraft moves blocks
			for (BlockVec neighborVec : neighborBlockVectors)
				getConnectedBlockHelper(pushBlocks, neighborVec, PushType.STICKY, instance, sourceVecs, direction);
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
