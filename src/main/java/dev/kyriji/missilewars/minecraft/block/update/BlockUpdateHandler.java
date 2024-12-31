package dev.kyriji.missilewars.minecraft.block.update;

import dev.kyriji.missilewars.minecraft.block.slimestone.PistonManager;
import dev.kyriji.missilewars.minecraft.block.util.BlockUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.*;

public class BlockUpdateHandler {
	private static BlockUpdateHandler INSTANCE;

	private static final Map<Instance, Set<BlockVec>> instanceBlockUpdateMap = new HashMap<>();

	private BlockUpdateHandler() {
		MinecraftServer.getGlobalEventHandler().addListener(InstanceTickEvent.class, event -> {
			Instance instance = event.getInstance();
			if (instanceBlockUpdateMap.containsKey(instance)) {
				PistonManager.get().handleScheduledMoves(instance);

				Set<BlockVec> blocksToUpdate = instanceBlockUpdateMap.get(instance);
				Set<BlockVec> blocksToUpdateCopy = new HashSet<>(blocksToUpdate);
				blocksToUpdate.clear();

				for (BlockVec blockVec : blocksToUpdateCopy) handleUpdate(instance, blockVec);
			}
		});
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
