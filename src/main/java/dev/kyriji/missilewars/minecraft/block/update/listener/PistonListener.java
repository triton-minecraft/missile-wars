package dev.kyriji.missilewars.minecraft.block.update.listener;

import dev.kyriji.missilewars.minecraft.block.slimestone.PistonManager;
import dev.kyriji.missilewars.minecraft.block.slimestone.RedstoneManager;
import dev.kyriji.missilewars.minecraft.block.state.properties.Facing;
import dev.kyriji.missilewars.minecraft.block.state.properties.PistonExtended;
import dev.kyriji.missilewars.minecraft.block.state.properties.PistonHeadType;
import dev.kyriji.missilewars.minecraft.block.update.BlockUpdateHandler;
import dev.kyriji.missilewars.minecraft.block.update.BlockUpdateListener;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class PistonListener implements BlockUpdateListener {
	@Override
	public void onBlockUpdate(Block block, Instance instance, BlockVec blockVec) {
		Block defaultBlock = block.defaultState();
		if (defaultBlock == Block.PISTON || defaultBlock == Block.STICKY_PISTON) {
			checkPowered(instance, blockVec, block);

		} else if (defaultBlock == Block.PISTON_HEAD) {
			Facing facing = Facing.fromBlock(block);
			PistonHeadType type = PistonHeadType.fromBlock(block);

			BlockVec pistonVec = facing.offset(blockVec, true);
			Block pistonBlock = instance.getBlock(pistonVec);

			if (pistonBlock.defaultState() != Block.PISTON && type == PistonHeadType.NORMAL) {
				instance.setBlock(blockVec, Block.AIR);
				System.out.println("something went wrong, deleting piston head");
			} else if (pistonBlock.defaultState() != Block.STICKY_PISTON && type == PistonHeadType.STICKY) {
				instance.setBlock(blockVec, Block.AIR);
				System.out.println("something went wrong, deleting sticky piston head");
			}

			checkPowered(instance, pistonVec, pistonBlock);
		}
	}

	private void checkPowered(Instance instance, BlockVec pistonVec, Block pistonBlock) {
		PistonExtended extended = PistonExtended.fromBlock(pistonBlock);
		boolean powered = RedstoneManager.get().isPowered(instance, pistonVec) ||
				RedstoneManager.get().isPowered(instance, pistonVec.add(0, 1, 0));

		if (extended == PistonExtended.RETRACTED && powered) {
			extendPiston(instance, pistonVec);
		} else if (extended == PistonExtended.EXTENDED && !powered) {
			retractPiston(instance, pistonVec);
		}
	}

	public void extendPiston(Instance instance, BlockVec blockVec) {
		Block block = instance.getBlock(blockVec);
		Block defaultBlock = block.defaultState();
		Facing facing = Facing.fromBlock(block);

		if (!PistonManager.get().canPush(instance, blockVec)) return;
		PistonManager.get().UNSAFE_push(instance, blockVec);

		instance.setBlock(blockVec, block.withProperty("extended", PistonExtended.EXTENDED.getValue()));

		BlockVec headVec = facing.offset(blockVec);
		Block head = Block.PISTON_HEAD
				.withProperty("facing", facing.getValue())
				.withProperty("type", defaultBlock == Block.PISTON ?
						PistonHeadType.NORMAL.getValue() : PistonHeadType.STICKY.getValue());

		instance.setBlock(headVec, head);

		BlockUpdateHandler.get().scheduleUpdate(instance, blockVec);
		BlockUpdateHandler.get().scheduleUpdate(instance, headVec);
	}

	public void retractPiston(Instance instance, BlockVec blockVec) {
		Block block = instance.getBlock(blockVec);
		Block defaultBlock = block.defaultState();
		Facing facing = Facing.fromBlock(block);

		instance.setBlock(blockVec, block.withProperty("extended", PistonExtended.RETRACTED.getValue()));

		BlockVec offset = facing.offset(blockVec);
		instance.setBlock(offset, Block.AIR);

		BlockUpdateHandler.get().scheduleUpdate(instance, blockVec);
		BlockUpdateHandler.get().scheduleUpdate(instance, offset);
	}
}
