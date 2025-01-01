package dev.kyriji.missilewars;

import dev.kyriji.missilewars.command.TestCommand;
import dev.kyriji.missilewars.minecraft.Minecraft;
import dev.kyriji.tritonstom.TritonStom;
import dev.kyriji.tritonstom.worlds.TritonWorld;
import dev.kyriji.tritonstom.worlds.WorldManager;
import dev.kyriji.tritonstom.worlds.spawn.SpawnLocation;
import dev.kyriji.tritonstom.worlds.spawn.SpawnManager;
import dev.kyriji.tritonstom.worlds.time.TimeManager;
import dev.kyriji.tritonstom.worlds.time.TimeStrategy;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.instance.block.Block;

public class MissileWars {
	public static TritonWorld world;
	public static TritonWorld world2;

	public static void main(String[] args) {
		// System.setProperty("minestom.tps", "5");
		MinecraftServer server = MinecraftServer.init();

		world = WorldManager.get().buildWorld("world")
				.timeKeeper(TimeManager.get().buildTimeKeeper().strategy(TimeStrategy.ALWAYS_NOON))
				.build();

		// world2 = WorldManager.get().buildWorld("world2")
		// 		.timeKeeper(TimeManager.get().buildTimeKeeper().strategy(TimeStrategy.ALWAYS_NOON))
		// 		.build();

		TritonStom.builder(server)
				.defaultGameMode(GameMode.CREATIVE)
				.playerSpawner(SpawnManager.get().buildPlayerSpawner()
						.fixed(new SpawnLocation(world, new Pos(-100.5, 70, 0.5, 90, 0))))
				.start();

		MinecraftServer.getCommandManager().register(new TestCommand());

		// MojangAuth.init();
		server.start("0.0.0.0", 25565);

		Minecraft.init();

		System.out.println(Block.MOVING_PISTON.properties());
		System.out.println(Block.PISTON_HEAD.properties());
	}
}
