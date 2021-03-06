package com.github.chainmailstudios.astromine.registry.client;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;

public class AstromineRenderLayers {
	public static void initialize() {
		// Unused.
	}

	/**
	 * @param block       Block instance to be registered
	 * @param renderLayer RenderLayer of block instance to be registered
	 * @return Block instance registered
	 */
	static <T extends Block> T register(T block, RenderLayer renderLayer) {
		BlockRenderLayerMap.INSTANCE.putBlock(block, renderLayer);
		return block;
	}
}
