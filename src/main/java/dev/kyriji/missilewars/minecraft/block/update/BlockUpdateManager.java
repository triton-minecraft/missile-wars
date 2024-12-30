package dev.kyriji.missilewars.minecraft.block.update;

import dev.kyriji.missilewars.minecraft.block.update.listener.PistonListener;

import java.util.ArrayList;
import java.util.List;

public class BlockUpdateManager {
	private static BlockUpdateManager INSTANCE;

	private static final List<BlockUpdateListener> listeners = new ArrayList<>();

	private BlockUpdateManager() {
		listeners.add(new PistonListener());
	}

	public List<BlockUpdateListener> getListeners() {
		return listeners;
	}

	public static void init() {
		if (INSTANCE != null) throw new IllegalStateException();
		INSTANCE = new BlockUpdateManager();
	}

	public static BlockUpdateManager get() {
		return INSTANCE;
	}
}
