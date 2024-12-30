package dev.kyriji.missilewars.minecraft.block.update;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;

public class BlockUpdateDetector {
	private static BlockUpdateDetector INSTANCE;

	private BlockUpdateDetector() {
		MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockPlaceEvent.class, event -> {
			BlockUpdateHandler.get().scheduleUpdate(event.getInstance(), event.getBlockPosition());
		});
	}

	public static void init() {
		if (INSTANCE != null) throw new IllegalStateException();
		INSTANCE = new BlockUpdateDetector();
	}

	public static BlockUpdateDetector get() {
		return INSTANCE;
	}
}
