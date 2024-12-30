package dev.kyriji.missilewars.minecraft;

import dev.kyriji.missilewars.minecraft.block.placement.BlockPlacementManager;
import dev.kyriji.missilewars.minecraft.block.state.BlockStateManager;

public class Minecraft {
	private static Minecraft INSTANCE;

	private Minecraft() {
		BlockUpdateHandler.init();
		BlockUpdateDetector.init();
		BlockStateManager.init();
		BlockPlacementManager.init();
	}

	public static void init() {
		if (INSTANCE != null) throw new IllegalStateException();
		INSTANCE = new Minecraft();
	}

	public static Minecraft get() {
		return INSTANCE;
	}
}
