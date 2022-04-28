package com.oyosite.ticon.starship.starships

import com.oyosite.ticon.starship.StarshipMod
import com.oyosite.ticon.starship.Util.getList
import com.oyosite.ticon.starship.block.StarshipBlocks
import com.oyosite.ticon.starship.block.entity.TeleporterBlockEntity
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtIntArray
import net.minecraft.nbt.NbtList
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.TeleportTarget
import net.minecraft.world.World

class Starship(val type: StarshipType, val origin: BlockPos){

    var generated = false
    val teleporters = mutableSetOf<BlockPos>()
    var orbitId: Identifier? = null
    val orbitKey: RegistryKey<World> get() = RegistryKey.of(Registry.WORLD_KEY, orbitId) ?: World.OVERWORLD

    init{
        type.defaultTeleporterPos?.add(origin)?.let(teleporters::add)
    }

    fun readFromNbt(nbt: NbtCompound){
        if(nbt.contains("generated")) generated = nbt.getBoolean("generated")
        if(nbt.contains("teleporters")) teleporters.addAll(nbt.getList("teleporters", NbtElement.INT_ARRAY_TYPE).map { (it as NbtIntArray).intArray }.map{BlockPos(it[0],it[1],it[2])})
        if(nbt.contains("orbit")) orbitId = Identifier(nbt.getString("orbit"))
    }
    fun writeToNbt(): NbtCompound = NbtCompound().apply {
        putBoolean("generated", generated)
        put("teleporters", NbtList().apply { addAll(teleporters.map { with(it) { listOf(x, y, z) } }.map(::NbtIntArray)) })
        orbitId?.toString()?.let { putString("orbit", it) }
    }

    private fun generate(world: World){
        (-2..2).forEach{x -> (-2..2).forEach{z-> world.setBlockState(origin.add(x, 0, z), Blocks.IRON_BLOCK.defaultState) }}
        world.setBlockState(origin, StarshipBlocks.TELEPORTER.defaultState)
    }

    fun bringTo(player: PlayerEntity, teleporterName: String? = null){
        val oldWorld = player.entityWorld
        if(oldWorld.isClient)return
        val newWorld = oldWorld.server?.getWorld(StarshipMod.STARSHIP_WORLD)?:return
        if(!generated)generate(newWorld)
        for(pos in teleporters){
            val teleporter = newWorld.getBlockEntity(pos)
            if(teleporter is TeleporterBlockEntity){
                if(teleporterName == null || teleporterName==teleporter.name){
                    FabricDimensions.teleport(player, newWorld, TeleportTarget(teleporter.pos.up().run{ Vec3d(x.toDouble(), y.toDouble(), z.toDouble()) }, Vec3d.ZERO,player.yaw, player.pitch))
                    break
                }
            }
        }
    }

    fun beamDown(player: PlayerEntity){
        val oldWorld = player.entityWorld
        if(oldWorld.isClient)return
        val newWorld = oldWorld.server?.getWorld(orbitKey)?:return
        val x = newWorld.random.nextInt(32)-16
        val z = newWorld.random.nextInt(32)-16
        val chunk = newWorld.getChunk(x, z)
        var pos = BlockPos(x, chunk.topY, z)
        while(pos.y>chunk.bottomY && chunk.getBlockState(pos).isAir) pos = pos.down()
        if(!chunk.getBlockState(pos).isAir) FabricDimensions.teleport(player, newWorld, TeleportTarget(pos.up().run{ Vec3d(x.toDouble(), y.toDouble(), z.toDouble()) }, Vec3d.ZERO,player.yaw, player.pitch))
    }


}
