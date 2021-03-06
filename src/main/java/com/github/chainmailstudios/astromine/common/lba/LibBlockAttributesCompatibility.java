package com.github.chainmailstudios.astromine.common.lba;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import alexiil.mc.lib.attributes.Attribute;
import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.attributes.CombinableAttribute;
import alexiil.mc.lib.attributes.ListenerRemovalToken;
import alexiil.mc.lib.attributes.ListenerToken;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.FixedFluidInvView;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.FluidInvTankChangeListener;
import alexiil.mc.lib.attributes.fluid.GroupedFluidInv;
import alexiil.mc.lib.attributes.fluid.GroupedFluidInvView;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.GroupedFluidInvFixedWrapper;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import com.github.chainmailstudios.astromine.common.component.ComponentProvider;
import com.github.chainmailstudios.astromine.common.component.inventory.FluidInventoryComponent;
import com.github.chainmailstudios.astromine.common.fraction.Fraction;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class LibBlockAttributesCompatibility {
	public static void initialize() {
		// LBA 0.7.1: replace these 6 method calls with "FluidAttributes.forEachInv()"
		appendAdder(FluidAttributes.FIXED_INV_VIEW);
		appendAdder(FluidAttributes.FIXED_INV);
		appendAdder(FluidAttributes.GROUPED_INV_VIEW);
		appendAdder(FluidAttributes.GROUPED_INV);
		appendAdder(FluidAttributes.INSERTABLE);
		appendAdder(FluidAttributes.EXTRACTABLE);
	}

	private static <T> void appendAdder(Attribute<T> attribute) {
		attribute.appendBlockAdder(LibBlockAttributesCompatibility::append);
	}

	private static <T> void append(World world, BlockPos blockPos, BlockState state, AttributeList<T> list) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);

		if (blockEntity != null) {
			ComponentProvider componentProvider = ComponentProvider.fromBlockEntity(blockEntity);

			// LBA 0.7.1: replace this get&opposite call with "list.getTargetSide()"
			@Nullable Direction direction = list.getSearchDirection();
			if (direction != null) {
				direction = direction.getOpposite();
			}

			FluidInventoryComponent component = componentProvider.getSidedComponent(direction, AstromineComponentTypes.FLUID_INVENTORY_COMPONENT);

			if (component != null) {
				list.offer(new LibBlockAttributesWrapper(component));
			}
		}
	}

	private static alexiil.mc.lib.attributes.fluid.volume.FluidVolume wrapLibBlockAttributes(FluidVolume volume) {
		return FluidKeys.get(volume.getFluid()).withAmount(wrapLibBlockAttributes(volume.getFraction().copy()));
	}

	private static Optional<FluidVolume> wrapVolumeToAstromine(alexiil.mc.lib.attributes.fluid.volume.FluidVolume volume) {
		if (volume.getRawFluid() == null) return Optional.empty();

		return Optional.of(new FluidVolume(volume.getRawFluid(), wrapVolumeToAstromine(volume.amount())));
	}

	private static FluidAmount wrapLibBlockAttributes(Fraction fraction) {
		return FluidAmount.of(fraction.getNumerator(), fraction.getDenominator());
	}

	private static Fraction wrapVolumeToAstromine(FluidAmount amount) {
		return Fraction.of(amount.whole, amount.numerator, amount.denominator);
	}

	private static class LibBlockAttributesWrapper implements FixedFluidInv {
		private final FluidInventoryComponent component;

		public LibBlockAttributesWrapper(FluidInventoryComponent component) {
			this.component = component;
		}

		private void validateTankIndex(int tank) {
			if (tank < 0 || tank >= getTankCount()) {
				throw new IndexOutOfBoundsException("Tank (" + tank + ") was out of bounds [0, " + getTankCount() + ")");
			}
		}

		@Override
		public int getTankCount() {
			return component.getSize();
		}

		@Override
		public alexiil.mc.lib.attributes.fluid.volume.FluidVolume getInvFluid(int tank) {
			validateTankIndex(tank);
			return wrapLibBlockAttributes(component.getVolume(tank));
		}

		@Override
		public boolean setInvFluid(int tank, alexiil.mc.lib.attributes.fluid.volume.FluidVolume fluidVolume, Simulation simulation) {
			if (!isFluidValidForTank(tank, fluidVolume.getFluidKey())) return false;

			Optional<FluidVolume> optionalFluidVolume = wrapVolumeToAstromine(fluidVolume);

			if (!optionalFluidVolume.isPresent()) return false;

			FluidVolume incoming = optionalFluidVolume.get();
			FluidVolume current = component.getVolume(tank);

			if (incoming.getFraction().isBiggerThan(current.getSize())) {
				return false;
			}

			boolean allowed = false;

			if (incoming.isEmpty()) {
				if (current.isEmpty()) {
					return true;
				}
				allowed = component.canExtract(current, tank);
			} else if (current.isEmpty()) {
				allowed = component.canInsert(incoming, tank);
			} else if (incoming.getFluid() == current.getFluid()) {

				if (incoming.getFraction().equals(current.getFraction())) {
					return true;
				}

				if (incoming.isSmallerThan(current)) {
					allowed = component.canExtract(current, tank);
				} else {
					allowed = component.canInsert(incoming, tank);
				}
			} else {
				allowed = component.canExtract(current, tank) && component.canInsert(incoming, tank);
			}

			if (allowed && simulation.isAction()) {

				current.setFluid(incoming.getFluid());
				current.setFraction(incoming.getFraction());

				component.setVolume(tank, current);
			}

			return allowed;
		}

		@Override
		public boolean isFluidValidForTank(int tank, FluidKey fluidKey) {
			validateTankIndex(tank);
			Fluid fluid = fluidKey.getRawFluid();
			return fluid != null && component.canInsert(new FluidVolume(fluid, Fraction.BUCKET.copy()), tank);
		}

		@Override
		public FluidAmount getMaxAmount_F(int tank) {
			validateTankIndex(tank);
			return wrapLibBlockAttributes(component.getVolume(tank).getSize());
		}

		@Override
		public ListenerToken addListener(FluidInvTankChangeListener fluidInvTankChangeListener, ListenerRemovalToken listenerRemovalToken) {
			// We don't support listeners
			return null;
		}
	}
}
