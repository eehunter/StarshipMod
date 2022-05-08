package com.oyosite.ticon.starship

import com.oyosite.ticon.starship.StarshipMod.TELEPORTER_SCREEN_HANDLER
import com.oyosite.ticon.starship.client.gui.TeleporterScreen
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.gui.screen.ingame.HandledScreens

object StarshipModClient : ClientModInitializer {
    override fun onInitializeClient() {
        HandledScreens.register(TELEPORTER_SCREEN_HANDLER,::TeleporterScreen)
    }
}