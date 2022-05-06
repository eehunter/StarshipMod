package com.oyosite.ticon.starship.gui

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess

class TeleporterScreenHandlerFactory(val pos:BlockPos,val world:WorldAccess): NamedScreenHandlerFactory {
    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        TODO("Not yet implemented")
    }

    override fun getDisplayName(): Text {
        TODO("Not yet implemented")
    }
}