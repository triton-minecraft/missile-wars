package dev.kyriji.missilewars.minecraft.block.slimestone;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;

public class BlockMoveTask {
	private final Instance instance;
	private final BlockVec pistonVec;
	private final boolean isExtending;
	private final Runnable updatePistonRunnable;
	private final long creationTick;

	private Runnable moveBlocksRunnable;
	private boolean runImmediately = false;

	public BlockMoveTask(Instance instance, BlockVec pistonVec, boolean isExtending, Runnable updatePistonRunnable) {
		this.instance = instance;
		this.pistonVec = pistonVec;
		this.isExtending = isExtending;
		this.updatePistonRunnable = updatePistonRunnable;
		this.creationTick = instance.getWorldAge();
	}

	public Instance getInstance() {
		return instance;
	}

	public BlockVec getPistonVec() {
		return pistonVec;
	}

	public boolean isExtending() {
		return isExtending;
	}

	public Runnable getUpdatePistonRunnable() {
		return updatePistonRunnable;
	}

	public long getCreationTick() {
		return creationTick;
	}

	public boolean hasMoveBlocksRunnable() {
		return moveBlocksRunnable != null;
	}

	public Runnable getMoveBlocksRunnable() {
		return moveBlocksRunnable;
	}

	public void setMoveBlocksRunnable(Runnable moveBlocksRunnable) {
		this.moveBlocksRunnable = moveBlocksRunnable;
	}

	public boolean shouldRunImmediately() {
		return runImmediately;
	}

	public void runImmediately() {
		runImmediately = true;
	}
}
