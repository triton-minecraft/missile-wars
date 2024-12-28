package dev.kyriji.missilewars.commands;

import dev.kyriji.missilewars.MissileWars;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.entity.Player;

public class TestCommand extends Command {
	public TestCommand() {
		super("test");
		System.out.println("running");

		setDefaultExecutor((sender, context) -> {
			if (!(sender instanceof Player player)) return;
			player.sendMessage("test " + MissileWars.world.getInstanceContainer().getTimeRate() + " " +
					MissileWars.world.getInstanceContainer().getTime());

			// MinecraftServer.getInstanceManager().getInstances().forEach(instance -> {
			// 	player.sendMessage("saving the world: " + instance.getUniqueId());
			// 	instance.saveChunksToStorage();
			// });
		});

		ArgumentInteger numberArgument = ArgumentType.Integer("number");

		addSyntax((sender, context) -> {
			int number = context.get(numberArgument);
			MissileWars.world.getInstanceContainer().setTimeRate(number);
		}, numberArgument);
	}
}
