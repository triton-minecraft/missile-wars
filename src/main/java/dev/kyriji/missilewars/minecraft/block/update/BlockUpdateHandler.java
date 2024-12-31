package dev.kyriji.missilewars.minecraft.block.update;

import dev.kyriji.missilewars.minecraft.block.slimestone.PistonManager;
import dev.kyriji.missilewars.minecraft.block.util.BlockUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.*;

public class BlockUpdateHandler {
	private static BlockUpdateHandler INSTANCE;

	private static final Map<Instance, LinkedHashSet<BlockVec>> instanceBlockUpdateMap = new HashMap<>();

	public static long lastStoneTick = Integer.MAX_VALUE;

	private BlockUpdateHandler() {
		MinecraftServer.getGlobalEventHandler().addListener(InstanceTickEvent.class, event -> {
			Instance instance = event.getInstance();
			if (lastStoneTick < 15) {
				System.out.println("------------------------------");
				System.out.println("ticking: " + lastStoneTick);
			}
			tick(instance);
			lastStoneTick++;
		});

		MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockPlaceEvent.class, event -> {
			if (event.getBlock().defaultState() == Block.STONE) lastStoneTick = 1;
		});
	}

	public void tick(Instance instance) {
		PistonManager.get().handleScheduledMoves(instance);

		instanceBlockUpdateMap.putIfAbsent(instance, new LinkedHashSet<>());
		Set<BlockVec> blocksToUpdate = instanceBlockUpdateMap.get(instance);
		Set<BlockVec> blocksToUpdateThisTick = new LinkedHashSet<>(blocksToUpdate);
		blocksToUpdate.clear();

		for (BlockVec blockVec : blocksToUpdateThisTick) handleUpdate(instance, blockVec);
	}

	public void handleUpdate(Instance instance, BlockVec blockVec) {
		Block block = instance.getBlock(blockVec);
		if (block == Block.AIR) return;
		for (BlockUpdateListener listener : BlockUpdateManager.get().getListeners())
			listener.onBlockUpdate(block, instance, blockVec);
	}

	public void scheduleUpdate(Instance instance, BlockVec blockVec) {
		instanceBlockUpdateMap.putIfAbsent(instance, new LinkedHashSet<>());
		Set<BlockVec> blocksToUpdate = instanceBlockUpdateMap.get(instance);
		blocksToUpdate.add(blockVec);
		blocksToUpdate.addAll(BlockUtils.getNeighborBlocks(blockVec));
		System.out.println(blocksToUpdate);
	}

	public void scheduleUpdateImmediately(Instance instance, BlockVec blockVec) {
		Set<BlockVec> blocksToUpdate = instanceBlockUpdateMap.get(instance);
		blocksToUpdate.add(blockVec);
		blocksToUpdate.addAll(BlockUtils.getNeighborBlocks(blockVec));
		for (BlockVec updateVec : new ArrayList<>(blocksToUpdate)) handleUpdate(instance, updateVec);
	}

	public static void init() {
		if (INSTANCE != null) throw new IllegalStateException();
		INSTANCE = new BlockUpdateHandler();
	}

	public static BlockUpdateHandler get() {
		return INSTANCE;
	}
}
