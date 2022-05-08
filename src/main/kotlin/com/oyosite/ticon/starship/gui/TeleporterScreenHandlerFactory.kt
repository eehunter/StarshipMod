package com.oyosite.ticon.starship.gui

import com.oyosite.ticon.starship.component.ComponentEntrypoint
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class TeleporterScreenHandlerFactory(val pos:BlockPos,val world: World): NamedScreenHandlerFactory {
    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler = TeleporterGuiDescription(syncId,inv, ScreenHandlerContext.create(world, pos))

    override fun getDisplayName(): Text = TranslatableText(ComponentEntrypoint.STARSHIP_WORLD_COMPONENT[world].getTeleporters { it.first==pos }.getOrNull(0)?.second?:"<unnamed>")
}