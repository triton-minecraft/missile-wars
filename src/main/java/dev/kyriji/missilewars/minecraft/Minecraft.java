package dev.kyriji.missilewars.minecraft;

import dev.kyriji.missilewars.minecraft.block.placement.BlockPlacementManager;
import dev.kyriji.missilewars.minecraft.block.state.BlockStateManager;
import dev.kyriji.missilewars.minecraft.block.update.BlockUpdateDetector;
import dev.kyriji.missilewars.minecraft.block.update.BlockUpdateHandler;
import dev.kyriji.missilewars.minecraft.block.update.BlockUpdateManager;

public class Minecraft {
	private static Minecraft INSTANCE;

	private Minecraft() {
		BlockUpdateHandler.init();
		BlockUpdateDetector.init();
		BlockUpdateManager.init();
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
