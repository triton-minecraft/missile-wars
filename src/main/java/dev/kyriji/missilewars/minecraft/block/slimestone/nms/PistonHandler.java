package dev.kyriji.missilewars.minecraft.block.slimestone.nms;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

import java.util.ArrayList;
import java.util.List;

public class PistonHandler {
	public static final int MAX_MOVABLE_BLOCKS = 12;
	private final Instance world;
	private final BlockVec posFrom;
	private final boolean retracted;
	private final BlockVec posTo;
	private final Direction motionDirection;
	private final List<BlockVec> movedBlocks = new ArrayList<>();
	private final List<BlockVec> brokenBlocks = new ArrayList<>();
	private final Direction pistonDirection;

	public PistonHandler(Instance world, BlockVec pos, Direction dir, boolean retracted) {
		this.world = world;
		this.posFrom = pos;
		this.pistonDirection = dir;
		this.retracted = retracted;
		if (retracted) {
			this.motionDirection = dir;
			this.posTo = offset(pos, dir);
		} else {
			this.motionDirection = dir.opposite();
			this.posTo = offset(pos, dir, 2);
		}
	}

	// new methods

	public BlockVec offset(BlockVec blockVec, Direction direction) {
		return offset(blockVec, direction, 1);
	}

	public BlockVec offset(BlockVec blockVec, Direction direction, int offset) {
		return blockVec.add(new BlockVec(direction.vec().mul(offset)));
	}

	public Axis getAxis(Direction direction) {
		return switch (direction) {
			case EAST, WEST -> Axis.X;
			case UP, DOWN -> Axis.Y;
			case NORTH, SOUTH -> Axis.Z;
		};
	}

	public enum Axis {
		X, Y, Z
	}

	// original methods

	public boolean calculatePush() {
		this.movedBlocks.clear();
		this.brokenBlocks.clear();
		Block blockState = this.world.getBlock(this.posTo);
		if (!PistonBlock.isMovable(blockState, this.world, this.posTo, this.motionDirection, false, this.pistonDirection)) {
			if (this.retracted && BlocksExtension.fromBlock(blockState).getPistonBehavior() == PistonBehavior.DESTROY) {
				this.brokenBlocks.add(this.posTo);
				return true;
			} else {
				return false;
			}
		} else if (!this.tryMove(this.posTo, this.motionDirection)) {
			return false;
		} else {
			for(int i = 0; i < this.movedBlocks.size(); ++i) {
				BlockVec blockPos = this.movedBlocks.get(i);
				if (isBlockSticky(this.world.getBlock(blockPos)) && !this.tryMoveAdjacentBlock(blockPos)) {
					return false;
				}
			}

			return true;
		}
	}

	private static boolean isBlockSticky(Block state) {
		Block defaultState = state.defaultState();
		return defaultState == Block.SLIME_BLOCK || defaultState == Block.HONEY_BLOCK;
	}

	private static boolean isAdjacentBlockStuck(Block state, Block adjacentState) {
		Block defaultState = state.defaultState();
		Block adjacentDefaultState = adjacentState.defaultState();

		if (defaultState == Block.SLIME_BLOCK && adjacentDefaultState == Block.HONEY_BLOCK) {
			return false;
		} else if (defaultState == Block.HONEY_BLOCK && adjacentDefaultState == Block.SLIME_BLOCK) {
			return false;
		} else {
			return isBlockSticky(state) || isBlockSticky(adjacentState);
		}
	}

	private boolean tryMove(BlockVec pos, Direction dir) {
		Block blockState = this.world.getBlock(pos);
		if (blockState.isAir()) {
			return true;
		} else if (!PistonBlock.isMovable(blockState, this.world, pos, this.motionDirection, false, dir)) {
			return true;
		} else if (pos.equals(this.posFrom)) {
			return true;
		} else if (this.movedBlocks.contains(pos)) {
			return true;
		} else {
			int i = 1;
			if (i + this.movedBlocks.size() > 12) {
				return false;
			} else {
				while(isBlockSticky(blockState)) {
					BlockVec blockPos = offset(pos, this.motionDirection.opposite(), i);
					Block blockState2 = blockState;
					blockState = this.world.getBlock(blockPos);
					if (blockState.isAir() || !isAdjacentBlockStuck(blockState2, blockState) || !PistonBlock.isMovable(blockState, this.world, blockPos, this.motionDirection, false, this.motionDirection.opposite()) || blockPos.equals(this.posFrom)) {
						break;
					}

					++i;
					if (i + this.movedBlocks.size() > 12) {
						return false;
					}
				}

				int j = 0;

				int k;
				for(k = i - 1; k >= 0; --k) {
					this.movedBlocks.add(offset(pos, this.motionDirection.opposite(), k));
					++j;
				}

				k = 1;

				while(true) {
					BlockVec blockPos2 = offset(pos, this.motionDirection, k);
					int l = this.movedBlocks.indexOf(blockPos2);
					if (l > -1) {
						this.setMovedBlocks(j, l);

						for(int m = 0; m <= l + j; ++m) {
							BlockVec blockPos3 = this.movedBlocks.get(m);
							if (isBlockSticky(this.world.getBlock(blockPos3)) && !this.tryMoveAdjacentBlock(blockPos3)) {
								return false;
							}
						}

						return true;
					}

					blockState = this.world.getBlock(blockPos2);
					if (blockState.isAir()) {
						return true;
					}

					if (!PistonBlock.isMovable(blockState, this.world, blockPos2, this.motionDirection, true, this.motionDirection) || blockPos2.equals(this.posFrom)) {
						return false;
					}

					if (BlocksExtension.fromBlock(blockState).getPistonBehavior() == PistonBehavior.DESTROY) {
						this.brokenBlocks.add(blockPos2);
						return true;
					}

					if (this.movedBlocks.size() >= 12) {
						return false;
					}

					this.movedBlocks.add(blockPos2);
					++j;
					++k;
				}
			}
		}
	}

	private void setMovedBlocks(int from, int to) {
		List<BlockVec> list = new ArrayList<>();
		List<BlockVec> list2 = new ArrayList<>();
		List<BlockVec> list3 = new ArrayList<>();
		list.addAll(this.movedBlocks.subList(0, to));
		list2.addAll(this.movedBlocks.subList(this.movedBlocks.size() - from, this.movedBlocks.size()));
		list3.addAll(this.movedBlocks.subList(to, this.movedBlocks.size() - from));
		this.movedBlocks.clear();
		this.movedBlocks.addAll(list);
		this.movedBlocks.addAll(list2);
		this.movedBlocks.addAll(list3);
	}

	private boolean tryMoveAdjacentBlock(BlockVec pos) {
		Block blockState = this.world.getBlock(pos);
		Direction[] var3 = Direction.values();
		int var4 = var3.length;

		for(int var5 = 0; var5 < var4; ++var5) {
			Direction direction = var3[var5];
			if (getAxis(direction) != getAxis(this.motionDirection)) {
				BlockVec blockPos = offset(pos, direction);
				Block blockState2 = this.world.getBlock(blockPos);
				if (isAdjacentBlockStuck(blockState2, blockState) && !this.tryMove(blockPos, direction)) {
					return false;
				}
			}
		}

		return true;
	}

	public Direction getMotionDirection() {
		return this.motionDirection;
	}

	public List<BlockVec> getMovedBlocks() {
		return this.movedBlocks;
	}

	public List<BlockVec> getBrokenBlocks() {
		return this.brokenBlocks;
	}
}
