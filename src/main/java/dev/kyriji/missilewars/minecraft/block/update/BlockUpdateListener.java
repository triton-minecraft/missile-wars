package dev.kyriji.missilewars.minecraft.block.update;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public interface BlockUpdateListener {
	void onBlockUpdate(Block block, Instance instance, BlockVec blockVec);
}
