package com.oyosite.ticon.starship.gui

import com.oyosite.ticon.starship.StarshipMod.TELEPORTER_SCREEN_HANDLER
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.data.Insets.ROOT_PANEL
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext

class TeleporterGuiDescription(syncId: Int, playerInventory: PlayerInventory, screenHandlerContext: ScreenHandlerContext = ScreenHandlerContext.EMPTY): SyncedGuiDescription(TELEPORTER_SCREEN_HANDLER, syncId,playerInventory) {
    private val root = WGridPanel()
    init{
        setRootPanel(root)
        root.setSize(300,400)
        root.insets = ROOT_PANEL

        root.validate(this)
    }
}