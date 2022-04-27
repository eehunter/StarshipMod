package com.oyosite.ticon.starship

import com.oyosite.ticon.starship.Util.register
import com.oyosite.ticon.starship.world.VoidChunkGenerator
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World


object StarshipMod : ModInitializer {
    const val MODID = "starship"
    val STARSHIP_WORLD: RegistryKey<World> = RegistryKey.of(Registry.WORLD_KEY, Identifier(MODID, "starship"))
    override fun onInitialize() {
        Registry.CHUNK_GENERATOR.register("starship", VoidChunkGenerator.CODEC)
    }
}