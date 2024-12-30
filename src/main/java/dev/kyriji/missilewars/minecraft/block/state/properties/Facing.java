package dev.kyriji.missilewars.minecraft.block.state.properties;

import dev.kyriji.missilewars.minecraft.block.state.Property;
import net.minestom.server.instance.block.BlockFace;

public enum Facing implements Property<Facing> {
	NORTH,
	EAST,
	SOUTH,
	WEST,
	UP,
	DOWN
	;

	public Facing opposite() {
		return switch (this) {
			case NORTH -> SOUTH;
			case EAST -> WEST;
			case SOUTH -> NORTH;
			case WEST -> EAST;
			case UP -> DOWN;
			case DOWN -> UP;
		};
	}

	@Override
	public String getKey() {
		return "facing";
	}

	@Override
	public String getValue() {
		return name().toLowerCase();
	}

	public static Facing fromYawAndPitch(float yaw, float pitch) {
		float pitchRad = pitch * (float) Math.PI / 180.0F;
		float yawRad = -yaw * (float) Math.PI / 180.0F;

		float sinPitch = (float) Math.sin(pitchRad);
		float cosPitch = (float) Math.cos(pitchRad);
		float sinYaw = (float) Math.sin(yawRad);
		float cosYaw = (float) Math.cos(yawRad);

		boolean facingEast = sinYaw > 0.0F;
		boolean facingUp = sinPitch < 0.0F;
		boolean facingSouth = cosYaw > 0.0F;

		float eastWestComponent = facingEast ? sinYaw : -sinYaw;
		float upDownComponent = facingUp ? -sinPitch : sinPitch;
		float northSouthComponent = facingSouth ? cosYaw : -cosYaw;

		float eastWestWeight = eastWestComponent * cosPitch;
		float northSouthWeight = northSouthComponent * cosPitch;

		if (eastWestComponent > northSouthComponent) {
			if (upDownComponent > eastWestWeight) {
				return facingUp ? Facing.UP : Facing.DOWN;
			} else {
				return facingEast ? Facing.EAST : Facing.WEST;
			}
		} else {
			if (upDownComponent > northSouthWeight) {
				return facingUp ? Facing.UP : Facing.DOWN;
			} else {
				return facingSouth ? Facing.SOUTH : Facing.NORTH;
			}
		}
	}

	public static Facing fromBlockFace(BlockFace blockFace) {
		return switch (blockFace) {
			case NORTH -> NORTH;
			case EAST -> EAST;
			case SOUTH -> SOUTH;
			case WEST -> WEST;
			case TOP -> UP;
			case BOTTOM -> DOWN;
		};
	}
}
