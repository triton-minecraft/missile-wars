package dev.kyriji.missilewars.minecraft.block.state;

public class BlockStateManager {
	private static BlockStateManager INSTANCE;

	private BlockStateManager() {}

	public static void init() {
		if (INSTANCE != null) throw new IllegalStateException();
		INSTANCE = new BlockStateManager();
	}

	public static BlockStateManager get() {
		return INSTANCE;
	}
}
