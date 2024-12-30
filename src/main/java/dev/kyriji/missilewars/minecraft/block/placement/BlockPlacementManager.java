package dev.kyriji.missilewars.minecraft.block.placement;

import dev.kyriji.missilewars.minecraft.block.placement.rules.FacingPlacementRule;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockManager;
import org.jetbrains.annotations.NotNull;

public class BlockPlacementManager {
	private static BlockPlacementManager INSTANCE;

	private BlockPlacementManager() {
		MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockPlaceEvent.class, event -> {
			Player player = event.getPlayer();
			Block block = event.getBlock();
			System.out.println(block.properties());
		});

		BlockManager blockManager = MinecraftServer.getBlockManager();
		for (@NotNull Block block : Block.values()) {
			// if (block.properties().containsKey("facing"))
			if (block == Block.PISTON || block == Block.STICKY_PISTON || block == Block.OBSERVER)
				blockManager.registerBlockPlacementRule(new FacingPlacementRule(block));
		}
	}

	public void handleBlockPlacement(PlayerBlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		System.out.println(block.properties());
	}

	public static void init() {
		if (INSTANCE != null) throw new IllegalStateException();
		INSTANCE = new BlockPlacementManager();
	}

	public static BlockPlacementManager get() {
		return INSTANCE;
	}
}
