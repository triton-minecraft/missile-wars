package dev.kyriji.missilewars.minecraft.block.util;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.utils.Direction;

import java.util.ArrayList;
import java.util.List;

public class BlockUtils {
	public static List<BlockVec> getNeighborBlocks(BlockVec block) {
		return getNeighborBlocksExcluding(block);
	}

	public static List<BlockVec> getNeighborBlocksExcluding(BlockVec blockVec, Direction... directions) {
		List<Direction> directionsList = List.of(Direction.values()).stream().filter(direction -> {
			for (Direction testDirection : directions) if (direction == testDirection) return false;
			return true;
		}).toList();
		List<BlockVec> neighbors = new ArrayList<>();
		for (Direction direction : directionsList) neighbors.add(blockVec.add(new BlockVec(direction.vec())));
		return neighbors;
	}
}
