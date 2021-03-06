package com.github.chainmailstudios.astromine.common.world.generation;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import com.mojang.serialization.Codec;

import com.github.chainmailstudios.astromine.registry.AstromineBiomes;

import com.google.common.collect.ImmutableList;

public class AstromineBiomeSource extends BiomeSource {
	public static Codec<AstromineBiomeSource> CODEC = Codec.LONG.fieldOf("seed").xmap(AstromineBiomeSource::new, (source) -> source.seed).stable().codec();
	private final long seed;

	public AstromineBiomeSource(long seed) {
		super(ImmutableList.of());
		this.seed = seed;
	}

	@Override
	protected Codec<? extends BiomeSource> method_28442() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed) {
		return new AstromineBiomeSource(seed);
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return AstromineBiomes.ASTEROID_BELT;
	}
}
