package com.oyosite.ticon.starship.block

import com.oyosite.ticon.starship.StarshipMod
import com.oyosite.ticon.starship.block.entity.TeleporterBlockEntity
import com.oyosite.ticon.starship.component.ComponentEntrypoint
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TeleporterBlock(material: Material, settings: Settings.()->Unit): BlockWithEntity(Settings.of(material).apply(settings)) {
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = TeleporterBlockEntity(pos, state)
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if(world.registryKey == StarshipMod.STARSHIP_WORLD){
            ComponentEntrypoint.STARSHIP_COMPONENT[world][pos]?.beamDown(player)?.also{return ActionResult.SUCCESS}
            player.sendMessage(TranslatableText("starship.teleporter.error.starship_not_found"), false)
            return ActionResult.FAIL
        }
        //for debug purposes only
        var starship = ComponentEntrypoint.STARSHIP_COMPONENT[world][0]
        if(starship!=null){
            starship.bringTo(player)
        }else{

        }
        return super.onUse(state, world, pos, player, hand, hit)
    }
}