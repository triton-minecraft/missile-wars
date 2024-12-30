package dev.kyriji.missilewars.minecraft.block.placement;

import dev.kyriji.missilewars.minecraft.block.placement.rules.FacingPlacementRule;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockManager;
import org.jetbrains.annotations.NotNull;

public class BlockPlacementManager {
	private static BlockPlacementManager INSTANCE;

	private BlockPlacementManager() {
		BlockManager blockManager = MinecraftServer.getBlockManager();
		for (@NotNull Block block : Block.values()) {
			// if (block.properties().containsKey("facing"))
			if (block == Block.PISTON || block == Block.STICKY_PISTON || block == Block.OBSERVER)
				blockManager.registerBlockPlacementRule(new FacingPlacementRule(block));
		}
	}

	public static void init() {
		if (INSTANCE != null) throw new IllegalStateException();
		INSTANCE = new BlockPlacementManager();
	}

	public static BlockPlacementManager get() {
		return INSTANCE;
	}
}
