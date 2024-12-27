package dev.kyriji.missilewars;

import dev.kyriji.missilewars.commands.TestCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;

import java.time.Duration;

public class MissileWars {

	public static void main(String[] args) {
		MinecraftServer minecraftServer = MinecraftServer.init();

		InstanceManager instanceManager = MinecraftServer.getInstanceManager();
		InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

		instanceContainer.setChunkLoader(new AnvilLoader("data/worlds/world"));
		instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 1, Block.GRASS_BLOCK));
		instanceContainer.setChunkSupplier(LightingChunk::new);

		GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
		globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
			final Player player = event.getPlayer();
			event.setSpawningInstance(instanceContainer);
			player.setRespawnPoint(new Pos(-100.5, 70, 0.5));
			MinecraftServer.getSchedulerManager().buildTask(() -> {
						player.setFlying(true);
						player.setGameMode(GameMode.CREATIVE);
					})
					.delay(Duration.ofSeconds(1))
					.schedule();
		});

		MinecraftServer.getCommandManager().register(new TestCommand());

		MojangAuth.init();
		minecraftServer.start("0.0.0.0", 25565);
	}
}
