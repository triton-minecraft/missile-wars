package dev.kyriji.missilewars.minecraft.util;

import net.minestom.server.coordinate.BlockVec;

import java.util.List;

public class LocationUtils {
	public static List<BlockVec> getNeighborBlocks(BlockVec block) {
		return List.of(
			block.add(1, 0, 0),
			block.add(-1, 0, 0),
			block.add(0, 1, 0),
			block.add(0, -1, 0),
			block.add(0, 0, 1),
			block.add(0, 0, -1)
		);
	}
}
