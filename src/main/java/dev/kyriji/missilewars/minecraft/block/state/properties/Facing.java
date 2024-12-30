package dev.kyriji.missilewars.minecraft.block.state.properties;

import dev.kyriji.missilewars.minecraft.block.state.Property;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.block.BlockFace;

public enum Facing implements Property<Facing> {
	NORTH(0, 0, -1),
	EAST(1, 0, 0),
	SOUTH(0, 0, 1),
	WEST(-1, 0, 0),
	UP(0, 1, 0),
	DOWN(0, -1, 0)
	;

	private final int x, y, z;

	Facing(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

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

	public BlockVec offset(BlockVec blockVec) {
		return offset(blockVec, false);
	}

	public BlockVec offset(BlockVec blockVec, boolean reverse) {
		int multiplier = reverse ? -1 : 1;
		return blockVec.add(x * multiplier, y * multiplier, z * multiplier);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	@Override
	public String getKey() {
		return "facing";
	}

	@Override
	public String getValue() {
		return name().toLowerCase();
	}

	public static Facing fromString(String value) {
		return valueOf(value.toUpperCase());
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
