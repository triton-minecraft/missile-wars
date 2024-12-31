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

	private PistonManager() {}

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

	public void UNSAFE_push(Instance instance, BlockVec pistonVec) {
		Block pistonBlock = instance.getBlock(pistonVec);
		Direction direction = Facing.fromBlock(pistonBlock).toDirection();
		List<BlockVec> pushBlocks = getPushBlocks(instance, pistonVec, direction);

		System.out.println("pushing " + pushBlocks.size() + " blocks");

		pushBlocks = pushBlocks.reversed();
		for (BlockVec pushVec : pushBlocks) {
			BlockVec nextBlockVec = pushVec.add(new BlockVec(direction.vec()));
			instance.setBlock(nextBlockVec, instance.getBlock(pushVec));
			instance.setBlock(pushVec, Block.AIR);
			BlockUpdateHandler.get().scheduleUpdate(instance, pushVec);
			BlockUpdateHandler.get().scheduleUpdate(instance, nextBlockVec);
		}
	}

	public List<BlockVec> getPushBlocks(Instance instance, BlockVec sourceVec, Direction direction) {
		List<BlockVec> pushBlocks = new ArrayList<>();
		BlockVec currentVec = sourceVec.add(new BlockVec(direction.vec()));
		return getPushBlockHelper(pushBlocks, currentVec, PushType.PUSH, instance, sourceVec, direction);
	}

	public List<BlockVec> getPushBlockHelper(List<BlockVec> pushBlocks, BlockVec currentVec, PushType pushType,
											 Instance instance, BlockVec sourceVec, Direction direction) {
		if (pushBlocks.contains(currentVec)) return pushBlocks;
		if (currentVec.equals(sourceVec) && pushType == PushType.STICKY) return pushBlocks;
		Block defaultBlock = instance.getBlock(currentVec).defaultState();

		if (!SpecialBlockManager.get().isFluff(defaultBlock)) pushBlocks.add(currentVec);

		BlockVec nextBlockVec = currentVec.add(new BlockVec(direction.vec()));
		Block nextBlock = instance.getBlock(nextBlockVec);
		if (!SpecialBlockManager.get().isFluff(nextBlock))
			getPushBlockHelper(pushBlocks, nextBlockVec, PushType.PUSH, instance, sourceVec, direction);

		if (SpecialBlockManager.get().isSticky(defaultBlock)) {
			List<BlockVec> neighborBlockVectors = BlockUtils.getNeighborBlocks(currentVec);
			// TODO: switch out for the order which minecraft moves blocks
			for (BlockVec neighborVec : neighborBlockVectors)
				getPushBlockHelper(pushBlocks, neighborVec, PushType.STICKY, instance, sourceVec, direction);
			return pushBlocks;
		}

		return pushBlocks;
	}

	public static void init() {
		if (INSTANCE != null) throw new IllegalStateException();
		INSTANCE = new PistonManager();
	}

	public static PistonManager get() {
		return INSTANCE;
	}
}
