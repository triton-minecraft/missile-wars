package dev.kyriji.missilewars.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class TestCommand extends Command {
	public TestCommand() {
		super("test");

		setDefaultExecutor((sender, context) -> {
			if (!(sender instanceof Player player)) return;
			player.sendMessage("test");

			// MinecraftServer.getInstanceManager().getInstances().forEach(instance -> {
			// 	player.sendMessage("saving the world: " + instance.getUniqueId());
			// 	instance.saveChunksToStorage();
			// });
		});
	}
}
