package dev.kyriji.missilewars.minecraft.block.state.properties;

import dev.kyriji.missilewars.minecraft.block.state.Property;

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

	public static PistonType fromString(String value) {
		return valueOf(value.toUpperCase());
	}
}
