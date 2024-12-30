package dev.kyriji.missilewars.minecraft.block.update.listener;

import dev.kyriji.missilewars.minecraft.block.state.properties.Facing;
import dev.kyriji.missilewars.minecraft.block.state.properties.PistonType;
import dev.kyriji.missilewars.minecraft.block.update.BlockUpdateListener;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class PistonListener implements BlockUpdateListener {
	@Override
	public void onBlockUpdate(Block block, Instance instance, BlockVec blockVec) {
		Block defaultBlock = block.defaultState();
		if (defaultBlock == Block.PISTON || defaultBlock == Block.STICKY_PISTON) {
			Facing facing = Facing.fromString(block.getProperty("facing"));

			instance.setBlock(blockVec, block.withProperty("extended", "true"));

			Block head = Block.PISTON_HEAD
					.withProperty("facing", facing.getValue())
					.withProperty("type", defaultBlock == Block.PISTON ? "normal" : "sticky");

			BlockVec offset = facing.offset(blockVec);
			instance.setBlock(offset, head);
		} else if (defaultBlock == Block.PISTON_HEAD) {
			Facing facing = Facing.fromString(block.getProperty("facing"));
			PistonType type = PistonType.fromString(block.getProperty("type"));
			Block pistonBlock = instance.getBlock(facing.offset(blockVec, true));
			System.out.println(facing + " " + type + " " + pistonBlock);
			if (pistonBlock.defaultState() != Block.PISTON && type == PistonType.NORMAL) {
				instance.setBlock(blockVec, Block.AIR);
			} else if (pistonBlock.defaultState() != Block.STICKY_PISTON && type == PistonType.STICKY) {
				instance.setBlock(blockVec, Block.AIR);
			}
		}
	}
}
