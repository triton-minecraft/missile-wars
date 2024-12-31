package dev.kyriji.missilewars.minecraft.block.slimestone.nms;

import dev.kyriji.missilewars.minecraft.block.slimestone.SpecialBlockManager;
import dev.kyriji.missilewars.minecraft.block.state.properties.PistonExtended;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

public class PistonBlock {
	private static final int WORLD_BOTTOM = -64;
	private static final int WORLD_TOP = 319;

	private final boolean sticky;

	public PistonBlock(boolean sticky) {
		this.sticky = sticky;
	}

	// new methods

	public BlockVec offset(BlockVec blockVec, Direction direction) {
		return offset(blockVec, direction, 1);
	}

	public BlockVec offset(BlockVec blockVec, Direction direction, int offset) {
		return blockVec.add(new BlockVec(direction.vec().mul(offset)));
	}

	// original methods

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isMovable(Block state, Instance world, BlockVec pos, Direction direction, boolean canBreak, Direction pistonDir) {
		Block defaultState = state.defaultState();
		// if (pos.y() >= WORLD_BOTTOM && pos.y() <= WORLD_TOP && world.getWorldBorder().contains(pos)) {
		if (pos.y() >= WORLD_BOTTOM && pos.y() <= WORLD_TOP) {
			if (state.isAir()) {
				return true;
			} else if (defaultState != Block.OBSIDIAN && defaultState != Block.CRYING_OBSIDIAN && defaultState != Block.RESPAWN_ANCHOR && defaultState != Block.REINFORCED_DEEPSLATE) {
				if (direction == Direction.DOWN && pos.y() == WORLD_BOTTOM) {
					return false;
				} else if (direction == Direction.UP && pos.y() == WORLD_TOP) {
					return false;
				} else {
					if (defaultState != Block.PISTON && defaultState != Block.STICKY_PISTON) {
						// if (state.getHardness(world, pos) == -1.0F) {
						// 	return false;
						// }
						if (SpecialBlockManager.get().isImmovable(state)) return false;

						switch (BlocksExtension.fromBlock(state).getPistonBehavior()) {
							case BLOCK:
								return false;
							case DESTROY:
								return canBreak;
							case PUSH_ONLY:
								return direction == pistonDir;
						}
					} else if (PistonExtended.fromBlock(state) == PistonExtended.EXTENDED) {
						return false;
					}
					// return !state.hasBlockEntity();
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	// public boolean move(Instance world, BlockVec pos, Direction dir, boolean extend) {
	// 	BlockVec blockPos = offset(pos, dir);
	// 	if (!extend && world.getBlock(blockPos).defaultState() == Block.PISTON_HEAD) {
	// 		// world.setBlock(blockPos, Block.AIR, Block.NO_REDRAW | Block.FORCE_STATE);
	// 		world.setBlock(blockPos, Block.AIR);
	// 	}
	//
	// 	PistonHandler pistonHandler = new PistonHandler(world, pos, dir, extend);
	// 	if (!pistonHandler.calculatePush()) {
	// 		return false;
	// 	} else {
	// 		Map<BlockVec, Block> map = new HashMap<>();
	// 		List<BlockVec> list = pistonHandler.getMovedBlocks();
	// 		List<Block> list2 = new ArrayList<>();
	// 		Iterator var10 = list.iterator();
	//
	// 		while(var10.hasNext()) {
	// 			BlockVec blockPos2 = (BlockVec)var10.next();
	// 			Block blockState = world.getBlock(blockPos2);
	// 			list2.add(blockState);
	// 			map.put(blockPos2, blockState);
	// 		}
	//
	// 		List<BlockVec> list3 = pistonHandler.getBrokenBlocks();
	// 		Block[] blockStates = new Block[list.size() + list3.size()];
	// 		Direction direction = extend ? dir : dir.opposite();
	// 		int i = 0;
	//
	// 		int j;
	// 		BlockVec blockPos3;
	// 		Block blockState2;
	// 		for(j = list3.size() - 1; j >= 0; --j) {
	// 			blockPos3 = list3.get(j);
	// 			blockState2 = world.getBlock(blockPos3);
	// 			// BlockEntity blockEntity = blockState2.hasBlockEntity() ? world.getBlock(blockPos3) : null;
	// 			// dropStacks(blockState2, world, blockPos3, blockEntity);
	// 			// world.setBlock(blockPos3, Block.AIR, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
	// 			world.setBlock(blockPos3, Block.AIR);
	// 			// world.emitGameEvent(GameEvent.BLOCK_DESTROY, blockPos3, GameEvent.Emitter.of(blockState2));
	// 			// if (!blockState2.isIn(BlockTags.FIRE)) {
	// 			// 	world.addBlockBreakParticles(blockPos3, blockState2);
	// 			// }
	//
	// 			blockStates[i++] = blockState2;
	// 		}
	//
	// 		Block blockState3;
	// 		for(j = list.size() - 1; j >= 0; --j) {
	// 			blockPos3 = list.get(j);
	// 			blockState2 = world.getBlock(blockPos3);
	// 			blockPos3 = offset(blockPos3, direction);
	// 			map.remove(blockPos3);
	// 			blockState3 = Block.MOVING_PISTON.withProperty("facing", Facing.fromDirection(dir).getValue());
	// 			// world.setBlock(blockPos3, blockState3, Block.NO_REDRAW | Block.MOVED);
	// 			world.setBlock(blockPos3, blockState3);
	// 			// world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston(blockPos3, blockState3, (BlockState)list2.get(j), dir, extend, false));
	// 			blockStates[i++] = blockState2;
	// 		}
	//
	// 		if (extend) {
	// 			PistonType pistonType = this.sticky ? PistonType.STICKY : PistonType.NORMAL;
	// 			Block blockState4 = Block.PISTON_HEAD.withProperty("facing", Facing.fromDirection(dir).getValue()).withProperty("type", pistonType.getValue());
	// 			blockState2 = Block.MOVING_PISTON.withProperty("facing", Facing.fromDirection(dir).getValue()).withProperty("type", pistonType.getValue());
	// 			map.remove(blockPos);
	// 			// world.setBlock(blockPos, blockState2, Block.NO_REDRAW | Block.MOVED);
	// 			world.setBlock(blockPos, blockState2);
	// 			// world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston(blockPos, blockState2, blockState4, dir, true, true));
	// 		}
	//
	// 		Block blockState5 = Block.AIR;
	// 		Iterator var25 = map.keySet().iterator();
	//
	// 		while(var25.hasNext()) {
	// 			BlockVec blockPos4 = (BlockVec) var25.next();
	// 			// world.setBlock(blockPos4, blockState5, Block.NOTIFY_LISTENERS | Block.FORCE_STATE | Block.MOVED);
	// 			world.setBlock(blockPos4, blockState5);
	// 		}
	//
	// 		var25 = map.entrySet().iterator();
	//
	// 		while(var25.hasNext()) {
	// 			Map.Entry<BlockVec, Block> entry = (Map.Entry)var25.next();
	// 			BlockVec blockPos5 = entry.getKey();
	// 			Block blockState6 = entry.getValue();
	// 			// blockState6.prepare(world, blockPos5, 2);
	// 			// blockState5.updateNeighbors(world, blockPos5, Block.NOTIFY_LISTENERS);
	// 			// blockState5.prepare(world, blockPos5, 2);
	// 		}
	//
	// 		// WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation(world, pistonHandler.getMotionDirection(), (Direction)null);
	// 		i = 0;
	//
	// 		int k;
	// 		for(k = list3.size() - 1; k >= 0; --k) {
	// 			blockState3 = blockStates[i++];
	// 			BlockVec blockPos6 = list3.get(k);
	// 			// blockState3.prepare(world, blockPos6, 2);
	// 			// world.updateNeighborsAlways(blockPos6, blockState3.getBlock(), wireOrientation);
	// 		}
	//
	// 		for(k = list.size() - 1; k >= 0; --k) {
	// 			// world.updateNeighborsAlways((BlockPos)list.get(k), blockStates[i++].getBlock(), wireOrientation);
	// 		}
	//
	// 		if (extend) {
	// 			// world.updateNeighborsAlways(blockPos, Block.PISTON_HEAD, wireOrientation);
	// 		}
	//
	// 		return true;
	// 	}
	// }
}
