package dev.kyriji.missilewars.minecraft.block.slimestone;

import dev.kyriji.missilewars.minecraft.block.state.properties.Facing;
import dev.kyriji.missilewars.minecraft.block.util.BlockUtil;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.List;

public class RedstoneManager {
	private static RedstoneManager INSTANCE;

	private RedstoneManager() {}

	public boolean isPowered(Instance instance, BlockVec blockVec) {
		Block block = instance.getBlock(blockVec);
		Block defaultBlock = block.defaultState();

		List<BlockVec> neighbors;
		if (defaultBlock == Block.PISTON || defaultBlock == Block.STICKY_PISTON) {
			Facing facing = Facing.fromBlock(block);
			neighbors = BlockUtil.getNeighborBlocksExcluding(blockVec, facing.toDirection());
		} else {
			neighbors = BlockUtil.getNeighborBlocks(blockVec);
		}

		for (BlockVec neighbor : neighbors) {
			Block neighborBlock = instance.getBlock(neighbor);
			if (neighborBlock.defaultState() == Block.REDSTONE_BLOCK) return true;
		}
		return false;
	}

	public static void init() {
		if (INSTANCE != null) throw new IllegalStateException();
		INSTANCE = new RedstoneManager();
	}

	public static RedstoneManager get() {
		return INSTANCE;
	}
}
