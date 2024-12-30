package dev.kyriji.missilewars.minecraft.block.placement.rules;

import dev.kyriji.missilewars.minecraft.block.state.properties.Facing;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FacingPlacementRule extends BlockPlacementRule {
	public FacingPlacementRule(@NotNull Block block) {
		super(block);
	}

	@Override
	public @Nullable Block blockPlace(@NotNull PlacementState placementState) {
		Pos playerPosition = placementState.playerPosition();
		assert playerPosition != null;
		Facing facing = Facing.fromYawAndPitch(playerPosition.yaw(), playerPosition.pitch());

		if (block == Block.PISTON || block == Block.STICKY_PISTON) facing = facing.opposite();

		return getBlock().withProperty("facing", facing.getValue());
	}
}
