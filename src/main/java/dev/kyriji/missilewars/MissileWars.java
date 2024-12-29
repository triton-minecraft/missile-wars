package dev.kyriji.missilewars;

import dev.kyriji.missilewars.commands.TestCommand;
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
import net.minestom.server.extras.MojangAuth;

public class MissileWars {
	public static TritonWorld world;
	public static TritonWorld world2;

	public static void main(String[] args) {
		MinecraftServer server = MinecraftServer.init();

		world = WorldManager.get().buildWorld("world")
				.timeKeeper(TimeManager.get().buildTimeKeeper().strategy(TimeStrategy.ALWAYS_NOON))
				.build();

		world2 = WorldManager.get().buildWorld("world2")
				.timeKeeper(TimeManager.get().buildTimeKeeper().strategy(TimeStrategy.ALWAYS_NOON))
				.build();

		TritonStom.builder(server)
				.defaultGameMode(GameMode.CREATIVE)
				.playerSpawner(SpawnManager.get().buildPlayerSpawner()
						.fixed(new SpawnLocation(world, new Pos(-100.5, 70, 0.5, 90, 0))))
				.start();

		MinecraftServer.getCommandManager().register(new TestCommand());

		MojangAuth.init();
		server.start("0.0.0.0", 25565);
	}
}
