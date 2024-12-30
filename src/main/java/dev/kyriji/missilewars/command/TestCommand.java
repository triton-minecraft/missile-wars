package dev.kyriji.missilewars.command;

import dev.kyriji.missilewars.MissileWars;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

public class TestCommand extends Command {
	public TestCommand() {
		super("test");
		System.out.println("running");

		setDefaultExecutor((sender, context) -> {
			if (!(sender instanceof Player player)) return;
			player.sendMessage("test " + MissileWars.world.getInstance().getTimeRate() + " " +
					MissileWars.world.getInstance().getTime());

			player.sendMessage("world1: " + MissileWars.world.getInstance().getUniqueId());
			player.sendMessage("world2: " + MissileWars.world2.getInstance().getUniqueId());
			player.sendMessage("player: " + player.getInstance().getUniqueId());

			player.setInstance(MissileWars.world2.getInstance(), new Pos(0, 2, 0));

			// MinecraftServer.getInstanceManager().getInstances().forEach(instance -> {
			// 	player.sendMessage("saving the world: " + instance.getUniqueId());
			// 	instance.saveChunksToStorage();
			// });
		});

		ArgumentInteger numberArgument = ArgumentType.Integer("number");

		addSyntax((sender, context) -> {
			int number = context.get(numberArgument);
			MissileWars.world.getInstance().setTimeRate(number);
		}, numberArgument);
	}
}
