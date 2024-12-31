package dev.kyriji.missilewars.minecraft.block.slimestone;

import dev.kyriji.missilewars.minecraft.block.state.properties.PistonExtended;
import net.minestom.server.instance.block.Block;

import java.util.HashSet;
import java.util.Set;

public class SpecialBlockManager {
	private static SpecialBlockManager INSTANCE;

	private static final Set<Block> immovableBlocks = new HashSet<>();
	private static final Set<Block> fluffBlocks = new HashSet<>();

	private SpecialBlockManager() {
		// blocks
		immovableBlocks.add(Block.BARRIER);
		immovableBlocks.add(Block.BEACON);
		immovableBlocks.add(Block.BEDROCK);
		immovableBlocks.add(Block.COMMAND_BLOCK);
		immovableBlocks.add(Block.CONDUIT);
		immovableBlocks.add(Block.CRYING_OBSIDIAN);
		immovableBlocks.add(Block.ENCHANTING_TABLE);
		immovableBlocks.add(Block.END_GATEWAY);
		immovableBlocks.add(Block.END_PORTAL);
		immovableBlocks.add(Block.END_PORTAL_FRAME);
		immovableBlocks.add(Block.ENDER_CHEST);
		immovableBlocks.add(Block.GRINDSTONE);
		immovableBlocks.add(Block.JIGSAW);
		immovableBlocks.add(Block.JUKEBOX);
		immovableBlocks.add(Block.LIGHT);
		immovableBlocks.add(Block.LODESTONE);
		immovableBlocks.add(Block.SPAWNER);
		immovableBlocks.add(Block.MOVING_PISTON);
		immovableBlocks.add(Block.NETHER_PORTAL);
		immovableBlocks.add(Block.OBSIDIAN);
		immovableBlocks.add(Block.PISTON_HEAD);
		immovableBlocks.add(Block.REINFORCED_DEEPSLATE);
		immovableBlocks.add(Block.RESPAWN_ANCHOR);
		immovableBlocks.add(Block.SCULK_CATALYST);
		immovableBlocks.add(Block.SCULK_SENSOR);
		immovableBlocks.add(Block.SCULK_SHRIEKER);
		immovableBlocks.add(Block.STRUCTURE_BLOCK);
		immovableBlocks.add(Block.STRUCTURE_VOID);

	// 	block entities
		immovableBlocks.add(Block.BARREL);
		immovableBlocks.add(Block.BEEHIVE);
		immovableBlocks.add(Block.BEE_NEST);
		immovableBlocks.add(Block.BLAST_FURNACE);
		immovableBlocks.add(Block.BREWING_STAND);
		immovableBlocks.add(Block.CHEST);
		immovableBlocks.add(Block.CHISELED_BOOKSHELF);
		immovableBlocks.add(Block.DAYLIGHT_DETECTOR);
		immovableBlocks.add(Block.DISPENSER);
		immovableBlocks.add(Block.DROPPER);
		immovableBlocks.add(Block.FURNACE);
		immovableBlocks.add(Block.HOPPER);
		immovableBlocks.add(Block.LECTERN);
		immovableBlocks.add(Block.SMOKER);
		immovableBlocks.add(Block.TRAPPED_CHEST);

		immovableBlocks.add(Block.CAMPFIRE);
		immovableBlocks.add(Block.SOUL_CAMPFIRE);

	// 	fluff blocks
		fluffBlocks.add(Block.AIR);
	}

	public static void init() {
		if (INSTANCE != null) throw new IllegalStateException();
		INSTANCE = new SpecialBlockManager();
	}

	public boolean isImmovable(Block block) {
		Block defaultBlock = block.defaultState();
		if (defaultBlock == Block.PISTON) {
			PistonExtended extended = PistonExtended.fromBlock(block);
			if (extended == PistonExtended.EXTENDED) return true;
		} else if (defaultBlock.name().contains("banner") ||
				defaultBlock.name().contains("sign")) {
			return true;
		}
		return immovableBlocks.contains(defaultBlock);
	}

	/**
	 * "Fluff" blocks are blocks that ignored by piston pushing, e.g. air, water, grass, etc.
	 */
	public boolean isFluff(Block block) {
		Block defaultBlock = block.defaultState();
		return fluffBlocks.contains(defaultBlock);
	}

	public boolean isSticky(Block block) {
		Block defaultBlock = block.defaultState();
		return defaultBlock == Block.SLIME_BLOCK || defaultBlock == Block.HONEY_BLOCK;
	}

	public static SpecialBlockManager get() {
		return INSTANCE;
	}
}
