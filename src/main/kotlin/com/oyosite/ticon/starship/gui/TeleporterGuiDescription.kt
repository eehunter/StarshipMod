package com.oyosite.ticon.starship.gui

import com.oyosite.ticon.starship.StarshipMod.TELEPORTER_SCREEN_HANDLER
import com.oyosite.ticon.starship.TeleporterReference
import com.oyosite.ticon.starship.TeleporterVisibility
import com.oyosite.ticon.starship.component.ComponentEntrypoint
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WLabel
import io.github.cottonmc.cotton.gui.widget.WListPanel
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.data.Insets.ROOT_PANEL
import me.shedaniel.clothconfig2.api.TickableWidget
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText

class TeleporterGuiDescription(syncId: Int, val playerInventory: PlayerInventory, screenHandlerContext: ScreenHandlerContext = ScreenHandlerContext.EMPTY): SyncedGuiDescription(TELEPORTER_SCREEN_HANDLER, syncId,playerInventory) {

    private val comp = ComponentEntrypoint.STARSHIP_ENTITY_COMPONENT[playerInventory.player]
    private var lastRefUpdate = comp.lastRefUpdate
    private val teleporterReferences = comp.visibleTeleporters
    private var list = WListPanel(teleporterReferences, ::TeleporterReferenceWidget, TeleporterReferenceWidget.Companion::configurator)
    private val root = object : WGridPanel(), TickableWidget{
        override fun tick() {
            if(lastRefUpdate==comp.lastRefUpdate)return
            remove(list)
            list = WListPanel(teleporterReferences, ::TeleporterReferenceWidget, TeleporterReferenceWidget.Companion::configurator)
            list.setListItemHeight(30)
            add(list, 2, 20, 296, 378)
        }
    }
    init{
        setRootPanel(root)
        root.setSize(300,400)
        root.insets = ROOT_PANEL

        list.setListItemHeight(30)
        root.add(list,2, 20, 296, 378)

        root.validate(this)
    }

    class TeleporterReferenceWidget: WPlainPanel(){
        var reference: TeleporterVisibility? = null
        val teleporterName = WLabel(TranslatableText("teleporter.reference.error.invalid.destination"))
        val posAndWorldName = WLabel(TranslatableText("teleporter.reference.error.invalid.destination.details"))
        //val teleporterName = WDynamicLabel { if(reference==null) "teleporter.reference.error.invalid.destination" else (reference?.third?:"<unnamed>")  }
        //val posAndWorldName = WDynamicLabel { if(reference==null) "teleporter.reference.error.invalid.destination.details" else reference!!.second.run { "$x $y $z ${reference?.first}" } }
        init{
            add(teleporterName, 2,2, 8*18, 12)
            add(posAndWorldName, 2,16, 8*18, 8)
            posAndWorldName.setColor(0xAAAAAA, 0x999999)

        }


        companion object{
            fun configurator(reference:TeleporterVisibility, tr: TeleporterReferenceWidget){
                tr.reference = reference
                tr.teleporterName.text=TranslatableText(reference.third?:"<unnamed>")
                tr.posAndWorldName.text=LiteralText(reference.second.run { "$x $y $z" }).append(LiteralText(reference.first.toString()))
            }
        }
    }
}