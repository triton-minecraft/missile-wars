package dev.kyriji.missilewars.minecraft.block.state.properties;

import dev.kyriji.missilewars.minecraft.block.state.Property;
import net.minestom.server.instance.block.Block;

public enum PistonHeadType implements Property<PistonHeadType> {
	NORMAL,
	STICKY,
	;

	@Override
	public String getKey() {
		return "type";
	}

	@Override
	public String getValue() {
		return name().toLowerCase();
	}

	public static PistonHeadType fromBlock(Block block) {
		return valueOf(block.getProperty("type").toUpperCase());
	}
}
