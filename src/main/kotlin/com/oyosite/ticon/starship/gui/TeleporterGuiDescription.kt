package com.oyosite.ticon.starship.gui

import com.oyosite.ticon.starship.StarshipMod
import com.oyosite.ticon.starship.StarshipMod.TELEPORTER_SCREEN_HANDLER
import com.oyosite.ticon.starship.TeleporterVisibility
import com.oyosite.ticon.starship.Util.colorInt
import com.oyosite.ticon.starship.Util.identifier
import com.oyosite.ticon.starship.component.ComponentEntrypoint
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.client.BackgroundPainter
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WLabel
import io.github.cottonmc.cotton.gui.widget.WListPanel
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.WSprite
import io.github.cottonmc.cotton.gui.widget.WTextField
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.InputResult
import io.github.cottonmc.cotton.gui.widget.data.Insets.ROOT_PANEL
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon
import me.shedaniel.clothconfig2.api.TickableWidget
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.impl.screenhandler.client.ClientNetworking
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import kotlin.jvm.optionals.getOrNull

@OptIn(ExperimentalStdlibApi::class)
class TeleporterGuiDescription(syncId: Int, val playerInventory: PlayerInventory, screenHandlerContext: ScreenHandlerContext = ScreenHandlerContext.EMPTY): SyncedGuiDescription(TELEPORTER_SCREEN_HANDLER, syncId, playerInventory) {

    private val comp = ComponentEntrypoint.STARSHIP_ENTITY_COMPONENT[playerInventory.player]
    private var lastRefUpdate = comp.lastRefUpdate
    private val teleporterReferences = /*mutableListOf(TeleporterVisibility(Identifier("overworld"), screenHandlerContext.get{w,p->p}.orElseGet { BlockPos(0,255,0) }, null))*/
        //Ok, this should filter out this teleporter's reference from the list.
        comp.visibleTeleporters/*.map{it}*/.let{screenHandlerContext.get{world, pos -> world to pos}.run{if(isPresent)it.filter{get().run{it.first!=first.registryKey.value&&it.second!=second}} else it}}.toList()

    private var list = WListPanel(teleporterReferences, ::TeleporterReferenceWidget, TeleporterReferenceWidget.Companion::configurator)
    //private var testList = WListPanel(listOf("testing"), { WLabel(LiteralText("foo")) }, {d,w -> w.text = LiteralText(d)})
    private val renameButton = WButton(TextureIcon(identifier("gui/teleporter/rename_button")))
    private val renameBox = WTextField()
    private val root = object : WGridPanel(), TickableWidget{
        override fun tick() {
            super.tick()
            if(lastRefUpdate==comp.lastRefUpdate)return
            remove(list)
            list = WListPanel(teleporterReferences, ::TeleporterReferenceWidget, TeleporterReferenceWidget.Companion::configurator)
            list.setListItemHeight(30)
            add(list, 0, 1, 15, 18)
            lastRefUpdate=comp.lastRefUpdate
        }
    }
    init{
        setRootPanel(root)
        root.setSize(300,400)
        root.insets = ROOT_PANEL



        renameButton.alignment = HorizontalAlignment.RIGHT
        renameButton.onClick = Runnable{
            if(renameBox.text.isEmpty())return@Runnable
            val (worldID,pos) = screenHandlerContext.get{w,p->w.registryKey.value to p}.getOrNull()?:return@Runnable Unit.apply{println("Invalid teleporter to rename")}
            ClientPlayNetworking.send(StarshipMod.TELEPORTER_RENAME_PACKET_ID, PacketByteBufs.create().apply { writeBlockPos(pos).writeIdentifier(worldID).writeString(renameBox.text) })

        }
        // Runnable{ClientPlayNetworking.send(StarshipMod.TELEPORTER_RENAME_PACKET_ID, PacketByteBufs.create().apply { screenHandlerContext.get{_,p->p}.apply { writeBoolean(isPresent);if(isPresent)writeBlockPos(get()) } })}
        root.add(renameButton, 0, 0, 2, 2)
        //println(teleporterReferences)

        list.setListItemHeight(30)
        root.add(list,0, 1, 15, 18)

        root.validate(this)
    }

    class TeleporterReferenceWidget: WPlainPanel(){
        var reference: TeleporterVisibility? = null
        val teleporterName = WLabel(TranslatableText("teleporter.reference.error.invalid.destination"))
        val posAndWorldName = WLabel(TranslatableText("teleporter.reference.error.invalid.destination.details"))
        //val teleporterName = WDynamicLabel { if(reference==null) "teleporter.reference.error.invalid.destination" else (reference?.third?:"<unnamed>")  }
        //val posAndWorldName = WDynamicLabel { if(reference==null) "teleporter.reference.error.invalid.destination.details" else reference!!.second.run { "$x $y $z ${reference?.first}" } }
        private val unfocusedBackgroundPainter = BackgroundPainter.createColorful(0x888888.colorInt)
        private val focusedBackgroundPainter = BackgroundPainter.createColorful(0x555555.colorInt)
        init{
            add(teleporterName, 2,2, 12*18, 12)
            add(posAndWorldName, 2,16, 12*18, 8)
            posAndWorldName.setColor(0xAAAAAA, 0x999999)
            backgroundPainter = unfocusedBackgroundPainter
            setSize(12*18+6, 30)
        }

        override fun canResize(): Boolean = true

        override fun canFocus(): Boolean = true

        override fun onFocusGained() { backgroundPainter = focusedBackgroundPainter }
        override fun onFocusLost() { backgroundPainter = unfocusedBackgroundPainter }

        override fun onClick(x: Int, y: Int, button: Int): InputResult {
            reference?:return InputResult.IGNORED
            if(!isFocused)return requestFocus().run{InputResult.PROCESSED}
            val buf = with(reference!!){
                PacketByteBufs.create().apply {
                    writeIdentifier(first)
                    writeBlockPos(second)
                }
            }
            ClientPlayNetworking.send(StarshipMod.TELEPORT_PACKET_ID, buf)
            return InputResult.PROCESSED
        }

        companion object{
            fun configurator(reference:TeleporterVisibility, tr: TeleporterReferenceWidget){
                try {
                    //println(reference.second)
                    tr.reference = reference
                    tr.teleporterName.text = TranslatableText(reference.third ?: "<unnamed>")
                    tr.posAndWorldName.text = LiteralText(reference.second.run { "$x $y $z " }).append(TranslatableText(reference.first.dimensionTranslationKey))
                }catch (e:Exception){e.printStackTrace()}
            }
            val Identifier.dimensionTranslationKey get() = "world.$namespace.${path.replace("/",".")}"
        }
    }
}