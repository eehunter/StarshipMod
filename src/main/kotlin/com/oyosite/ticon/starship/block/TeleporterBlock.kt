package com.oyosite.ticon.starship.block

import com.oyosite.ticon.starship.StarshipMod
import com.oyosite.ticon.starship.component.ComponentEntrypoint
import com.oyosite.ticon.starship.gui.TeleporterScreenHandlerFactory
import com.oyosite.ticon.starship.starships.StarshipType
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

@Suppress("OVERRIDE_DEPRECATION")
class TeleporterBlock(material: Material, settings: Settings.()->Unit): Block(Settings.of(material).apply(settings)){//BlockWithEntity(Settings.of(material).apply(settings)) {
    //override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = TeleporterBlockEntity(pos, state)
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        /*if(!world.isClient){
            if(world==world.server!!.getWorld(StarshipMod.STARSHIP_WORLD)){
                ComponentEntrypoint.STARSHIP_WORLD_COMPONENT[world][pos]?.beamDown(player)?.also{return ActionResult.SUCCESS}
                player.sendMessage(TranslatableText("starship.teleporter.error.starship_not_found"), false)
                return ActionResult.FAIL
            }
            //for debug purposes only
            val component = ComponentEntrypoint.STARSHIP_WORLD_COMPONENT[world.server!!.getWorld(StarshipMod.STARSHIP_WORLD)!!]
            var starship = component[0]
            if (starship == null) {
                starship = StarshipType.GENERIC_STARSHIP_TYPE.makeStarship(pos = BlockPos(512, 256, 512))
                component.addStarship(starship)
            }
            starship.bringTo(player)
        }*/

        if(!world.isClient) ComponentEntrypoint.STARSHIP_ENTITY_COMPONENT.apply{
            get(player).teleporters.apply{ if(none{it.first.value==world.registryKey.value&&it.second==pos})add(world.registryKey to pos) }
            syncWith(player as ServerPlayerEntity, player)
        }
        if(!player.isSneaking)player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
        return ActionResult.SUCCESS
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        ComponentEntrypoint.STARSHIP_WORLD_COMPONENT[world].addTeleporter(pos)
    }

    override fun onBroken(world: WorldAccess, pos: BlockPos, state: BlockState) {
        ComponentEntrypoint.STARSHIP_WORLD_COMPONENT[world].removeTeleporter(pos)
    }

    override fun createScreenHandlerFactory(state: BlockState, world: World, pos: BlockPos): NamedScreenHandlerFactory = TeleporterScreenHandlerFactory(pos, world)
}