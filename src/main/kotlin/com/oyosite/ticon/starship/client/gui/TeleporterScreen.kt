package com.oyosite.ticon.starship.client.gui

import com.oyosite.ticon.starship.gui.TeleporterGuiDescription
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

class TeleporterScreen(gui:TeleporterGuiDescription,player:PlayerEntity,title:Text): CottonInventoryScreen<TeleporterGuiDescription>(gui,player,title) {
}