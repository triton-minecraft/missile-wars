package dev.kyriji.missilewars.minecraft.block.state.properties;

import dev.kyriji.missilewars.minecraft.block.state.Property;
import net.minestom.server.instance.block.Block;

public enum PistonExtended implements Property<PistonExtended> {
	EXTENDED,
	RETRACTED,
	;

	@Override
	public String getKey() {
		return "type";
	}

	@Override
	public String getValue() {
		return this == EXTENDED ? "true" : "false";
	}

	public static PistonExtended fromBlock(Block block) {
		return block.getProperty("extended").equals("true") ? EXTENDED : RETRACTED;
	}
}
