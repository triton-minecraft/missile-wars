package dev.kyriji.missilewars.minecraft.block.util;

import net.minestom.server.MinecraftServer;

public class MessageUtil {

	public static void broadcast(String message) {
		MinecraftServer.getInstanceManager().getInstances().forEach(instance -> {
			instance.getPlayers().forEach(player -> {
				player.sendMessage(message);
			});
		});
	}
}
