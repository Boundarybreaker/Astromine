package com.github.chainmailstudios.astromine.client.registry;

import net.minecraft.block.Block;

import com.github.chainmailstudios.astromine.common.registry.DeltaRegistry;
import com.github.chainmailstudios.astromine.common.utilities.data.Range;

public class AsteroidOreRegistry extends DeltaRegistry<Integer, Block> {
	public static final AsteroidOreRegistry INSTANCE = new AsteroidOreRegistry();

	private AsteroidOreRegistry() {
		// Locked.
	}

	public void register(Range<Integer> range, Block block) {
		if (range.getMinimum() > range.getMaximum()) {
			range = Range.of(range.getMaximum(), range.getMinimum());
		}

		for (int chance = range.getMinimum(); chance < range.getMaximum(); ++chance) {
			this.register(chance, block);
		}
	}
}
