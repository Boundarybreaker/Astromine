package com.github.chainmailstudios.astromine.common.block;

import com.github.chainmailstudios.astromine.access.DyeColorAccess;
import com.github.chainmailstudios.astromine.common.block.base.DefaultedHorizontalFacingBlockWithEntity;
import com.github.chainmailstudios.astromine.common.block.entity.HolographicBridgeProjectorBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import spinnery.widget.api.Color;

public class HolographicBridgeProjectorBlock extends DefaultedHorizontalFacingBlockWithEntity {
	public HolographicBridgeProjectorBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos position, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack stack = player.getStackInHand(hand);

		if (stack.getItem() instanceof DyeItem) {
			DyeItem dye = (DyeItem) stack.getItem();

			HolographicBridgeProjectorBlockEntity entity = (HolographicBridgeProjectorBlockEntity) world.getBlockEntity(position);

			if (!world.isClient() && entity != null) {
				entity.color = Color.of(0x7e000000 | ((DyeColorAccess) (Object) dye.getColor()).getColor());
				entity.sync();
				
				if (entity.hasChild()) {
					entity.getChild().color = Color.of(0x7e000000 | ((DyeColorAccess) (Object) dye.getColor()).getColor());
					entity.getChild().sync();
				}
				if (entity.hasParent()) {
					entity.getParent().color = Color.of(0x7e000000 | ((DyeColorAccess) (Object) dye.getColor()).getColor());
					entity.getParent().sync();
				}

				if (!player.isCreative()) {
					stack.decrement(1);
				}
				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.PASS;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new HolographicBridgeProjectorBlockEntity();
	}
}
