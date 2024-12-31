package dev.kyriji.missilewars.minecraft.block.update;

import dev.kyriji.missilewars.minecraft.block.slimestone.PistonManager;
import dev.kyriji.missilewars.minecraft.block.util.BlockUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockUpdateHandler {
	private static BlockUpdateHandler INSTANCE;

	private static final Map<Instance, Set<BlockVec>> instanceBlockUpdateMap = new HashMap<>();

	private BlockUpdateHandler() {
		MinecraftServer.getGlobalEventHandler().addListener(InstanceTickEvent.class, event -> {
			Instance instance = event.getInstance();
			tick(instance);
		});
	}

	public void tick(Instance instance) {
		instanceBlockUpdateMap.putIfAbsent(instance, new HashSet<>());
		Set<BlockVec> blocksToUpdate = instanceBlockUpdateMap.get(instance);
		Set<BlockVec> blocksToUpdateThisTick = new HashSet<>(blocksToUpdate);
		blocksToUpdate.clear();

		for (BlockVec blockVec : blocksToUpdateThisTick) handleUpdate(instance, blockVec);

		PistonManager.get().handleScheduledMoves(instance);
	}

	public void handleUpdate(Instance instance, BlockVec blockVec) {
		Block block = instance.getBlock(blockVec);
		if (block == Block.AIR) return;
		for (BlockUpdateListener listener : BlockUpdateManager.get().getListeners())
			listener.onBlockUpdate(block, instance, blockVec);
	}

	public void scheduleUpdate(Instance instance, BlockVec blockVec) {
		instanceBlockUpdateMap.putIfAbsent(instance, new HashSet<>());
		Set<BlockVec> blocksToUpdate = instanceBlockUpdateMap.get(instance);
		blocksToUpdate.add(blockVec);
		blocksToUpdate.addAll(BlockUtils.getNeighborBlocks(blockVec));
	}

	public static void init() {
		if (INSTANCE != null) throw new IllegalStateException();
		INSTANCE = new BlockUpdateHandler();
	}

	public static BlockUpdateHandler get() {
		return INSTANCE;
	}
}
