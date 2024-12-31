package dev.kyriji.missilewars.minecraft.block.state.properties;

import dev.kyriji.missilewars.minecraft.block.state.Property;
import net.minestom.server.instance.block.Block;

public enum PistonType implements Property<PistonType> {
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

	public static PistonType fromBlock(Block block) {
		Block defaultState = block.defaultState();
		if (defaultState == Block.PISTON) return NORMAL;
		if (defaultState == Block.STICKY_PISTON) return STICKY;
		return valueOf(block.getProperty("type").toUpperCase());
	}
}
