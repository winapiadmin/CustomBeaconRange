package com.winapiadmin.custombeaconrange.mixin;
import com.winapiadmin.custombeaconrange.Config;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(BeaconBlockEntity.class)
public class BeaconMixin {

    @Inject(method = "applyPlayerEffects", at = @At("HEAD"))
    private static void onApplyPlayerEffects(
            World world,
            BlockPos pos,
            int beaconLevel,
            RegistryEntry<StatusEffect> primaryEffect,
            RegistryEntry<StatusEffect> secondaryEffect,
            CallbackInfo ci
    ) {
        if (world.isClient() || primaryEffect == null) {
            return;
        }
        double d = switch (beaconLevel) {
            case 1 -> Config.Level1Range;
            case 2 -> Config.Level2Range;
            case 3 -> Config.Level3Range;
            case 4 -> Config.Level4Range;
            default -> beaconLevel * 10 + 10;
        };

        int i = 0;
        if (beaconLevel >= 4 && Objects.equals(primaryEffect, secondaryEffect)) {
            i = 1;
        }
        int j = (9 + beaconLevel * 2) * 20;
        Box box = new Box(pos).expand(d).stretch(0.0, world.getHeight(), 0.0);
        List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);
        for (PlayerEntity playerEntity : list) {
            playerEntity.addStatusEffect(new StatusEffectInstance(primaryEffect, j, i, true, true));
        }
        if (beaconLevel >= 4 && !Objects.equals(primaryEffect, secondaryEffect) && secondaryEffect != null) {
            for (PlayerEntity playerEntity : list) {
                playerEntity.addStatusEffect(new StatusEffectInstance(secondaryEffect, j, 0, true, true));
            }
        }
    }
}
