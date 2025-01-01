package dev.kyriji.missilewars.minecraft.block.update.listener;

import dev.kyriji.missilewars.minecraft.block.slimestone.PistonManager;
import dev.kyriji.missilewars.minecraft.block.slimestone.RedstoneManager;
import dev.kyriji.missilewars.minecraft.block.state.properties.Facing;
import dev.kyriji.missilewars.minecraft.block.state.properties.PistonExtended;
import dev.kyriji.missilewars.minecraft.block.state.properties.PistonType;
import dev.kyriji.missilewars.minecraft.block.update.BlockUpdateHandler;
import dev.kyriji.missilewars.minecraft.block.update.BlockUpdateListener;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class PistonListener implements BlockUpdateListener {
	@Override
	public void onBlockUpdate(Block block, Instance instance, BlockVec blockVec) {
		Block defaultBlock = block.defaultState();
		// System.out.println("updating block: " + block + " at " + BlockUpdateHandler.lastStoneTick);
		if (defaultBlock == Block.PISTON || defaultBlock == Block.STICKY_PISTON) {
			checkPowered(instance, blockVec, block);

		} else if (defaultBlock == Block.PISTON_HEAD) {
			Facing facing = Facing.fromBlock(block);
			PistonType type = PistonType.fromBlock(block);

			BlockVec pistonVec = facing.offset(blockVec, true);
			Block pistonBlock = instance.getBlock(pistonVec);

			if (pistonBlock.defaultState() != Block.PISTON && type == PistonType.NORMAL) {
				instance.setBlock(blockVec, Block.AIR);
				System.out.println("something went wrong, deleting piston head");
			} else if (pistonBlock.defaultState() != Block.STICKY_PISTON && type == PistonType.STICKY) {
				instance.setBlock(blockVec, Block.AIR);
				System.out.println("something went wrong, deleting sticky piston head");
			}

			// BlockUpdateHandler.get().scheduleUpdate(instance, pistonVec);
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
		System.out.println("extending piston: " + BlockUpdateHandler.lastStoneTick);
		Block block = instance.getBlock(blockVec);
		Block defaultBlock = block.defaultState();
		Facing facing = Facing.fromBlock(block);

		// if (!PistonManager.get().canPush(instance, blockVec)) return;

		BlockVec headVec = facing.offset(blockVec);
		Block head = Block.PISTON_HEAD
				.withProperty("facing", facing.getValue())
				.withProperty("type", defaultBlock == Block.PISTON ?
						PistonType.NORMAL.getValue() : PistonType.STICKY.getValue());

		Runnable runnable = () -> {
			// instance.setBlock(blockVec, block.withProperty("extended", PistonExtended.EXTENDED.getValue()));
			// instance.setBlock(headVec, head);
			// BlockUpdateHandler.get().scheduleUpdate(instance, blockVec);
			// BlockUpdateHandler.get().scheduleUpdate(instance, headVec);
		};

		PistonManager.get().scheduleMove(instance, blockVec, true, runnable);
		PistonManager.get().UNSAFE_push(instance, blockVec);
	}

	public void retractPiston(Instance instance, BlockVec blockVec) {
		System.out.println("retracting piston: " + BlockUpdateHandler.lastStoneTick);
		Block block = instance.getBlock(blockVec);
		Block defaultBlock = block.defaultState();
		Facing facing = Facing.fromBlock(block);

		// if (defaultBlock == Block.STICKY_PISTON && !PistonManager.get().canPull(instance, blockVec)) return;

		BlockVec offset = facing.offset(blockVec);

		Runnable runnable = () -> {
			// instance.setBlock(blockVec, block.withProperty("extended", PistonExtended.RETRACTED.getValue()));
			// instance.setBlock(offset, Block.AIR);
			// BlockUpdateHandler.get().scheduleUpdate(instance, blockVec);
			// BlockUpdateHandler.get().scheduleUpdate(instance, offset);
		};

		// code to drop blocks
		// BlockMoveTask task = null;
		// if (defaultBlock == Block.STICKY_PISTON) {
		// 	task = PistonManager.get().getScheduledMove(instance, blockVec);
		// 	if (task != null) {
		// 		task.setMoveBlocksRunnable(null);
		// 		task.runImmediately();
		// 	}
		// }

		PistonManager.get().scheduleMove(instance, blockVec, false, runnable);
		PistonManager.get().UNSAFE_pull(instance, blockVec);
	}
}
