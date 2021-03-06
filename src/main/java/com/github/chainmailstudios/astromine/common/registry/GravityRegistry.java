package com.github.chainmailstudios.astromine.common.registry;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

import com.github.chainmailstudios.astromine.common.registry.base.BiDirectionalRegistry;

import java.util.Optional;

public class GravityRegistry extends BiDirectionalRegistry<RegistryKey<DimensionType>, Double> {
	public static final GravityRegistry INSTANCE = new GravityRegistry();

	private GravityRegistry() {
		// Locked.
	}

	@Override
	public Double get(RegistryKey<DimensionType> dimensionTypeRegistryKey) {
		return Optional.ofNullable(super.get(dimensionTypeRegistryKey)).orElse(0.08D);
	}
}
