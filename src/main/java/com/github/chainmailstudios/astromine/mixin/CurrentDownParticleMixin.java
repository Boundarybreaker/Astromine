package com.github.chainmailstudios.astromine.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.particle.CurrentDownParticle;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import com.github.chainmailstudios.astromine.common.registry.GravityRegistry;

@Mixin(CurrentDownParticle.class)
public abstract class CurrentDownParticleMixin extends Particle {
	public CurrentDownParticleMixin(ClientWorld world, double x, double y, double z) {
		super(world, x, y, z);
		throw new UnsupportedOperationException("Cannot instantiate Mixin class!");
	}

	@ModifyConstant(method = "tick()V", constant = @Constant(doubleValue = 0.08D))
	double getGravity(double original) {
		return GravityRegistry.INSTANCE.get(world.getDimensionRegistryKey());
	}
}
