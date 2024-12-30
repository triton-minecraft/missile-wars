package dev.kyriji.missilewars.minecraft.block.update.listener;

import dev.kyriji.missilewars.minecraft.block.state.properties.Facing;
import dev.kyriji.missilewars.minecraft.block.update.BlockUpdateListener;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class PistonListener implements BlockUpdateListener {
	@Override
	public void onBlockUpdate(Block block, Instance instance, BlockVec blockVec) {
		if (block.defaultState() != Block.PISTON) return;

		Facing facing = Facing.fromString(block.getProperty("facing"));

		instance.setBlock(blockVec, block.withProperty("extended", "true"));

		BlockVec offset = facing.offset(blockVec);
		instance.setBlock(offset, Block.PISTON_HEAD.withProperty("facing", facing.getValue()));
	}
}
