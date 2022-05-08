package com.oyosite.ticon.starship

import com.oyosite.ticon.starship.Util.register
import com.oyosite.ticon.starship.block.StarshipBlocks
import com.oyosite.ticon.starship.gui.TeleporterGuiDescription
import com.oyosite.ticon.starship.starships.StarshipType
import com.oyosite.ticon.starship.world.VoidChunkGenerator
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World


object StarshipMod : ModInitializer {
    const val MODID = "starship"
    val STARSHIP_WORLD: RegistryKey<World> = RegistryKey.of(Registry.WORLD_KEY, Identifier(MODID, "starship"))
    val TELEPORTER_SCREEN_HANDLER: ScreenHandlerType<TeleporterGuiDescription> by lazy {Registry.SCREEN_HANDLER.register("teleporter", ScreenHandlerType(::TeleporterGuiDescription))}
    override fun onInitialize() {
        Registry.CHUNK_GENERATOR.register("starship", VoidChunkGenerator.CODEC)
        StarshipBlocks.register()
        StarshipType
        TELEPORTER_SCREEN_HANDLER
    }
}