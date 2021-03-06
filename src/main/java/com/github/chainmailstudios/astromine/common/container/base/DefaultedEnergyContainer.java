package com.github.chainmailstudios.astromine.common.container.base;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;

import com.github.chainmailstudios.astromine.common.block.entity.base.DefaultedEnergyBlockEntity;

public class DefaultedEnergyContainer extends DefaultedBlockEntityContainer {
	public DefaultedEnergyBlockEntity blockEntity;

	public DefaultedEnergyContainer(int synchronizationID, PlayerInventory playerInventory, BlockPos position) {
		super(synchronizationID, playerInventory, position);

		blockEntity = (DefaultedEnergyBlockEntity) world.getBlockEntity(position);
	}
}
