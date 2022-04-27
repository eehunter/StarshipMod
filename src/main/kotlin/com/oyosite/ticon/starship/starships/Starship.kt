package com.oyosite.ticon.starship.starships

import com.oyosite.ticon.starship.Util.getList
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtIntArray
import net.minecraft.nbt.NbtList
import net.minecraft.util.math.BlockPos

class Starship(val type: StarshipType){

    var generated = false
    val teleporters = mutableSetOf<BlockPos>()
    init{
        type.defaultTeleporterPos?.takeIf{!teleporters.contains(it)}?.let(teleporters::add)
    }

    fun readFromNbt(nbt: NbtCompound){
        if(nbt.contains("generated")) generated = nbt.getBoolean("generated")
        if(nbt.contains("teleporters")) teleporters.addAll(nbt.getList("teleporters", NbtElement.INT_ARRAY_TYPE).map { (it as NbtIntArray).intArray }.map{BlockPos(it[0],it[1],it[2])})
    }
    fun writeToNbt(): NbtCompound = NbtCompound().apply {
        putBoolean("generated", generated)
        put("teleporters", NbtList().apply { addAll(teleporters.map { with(it) { listOf(x, y, z) } }.map(::NbtIntArray)) })
    }

    fun sendTo(player: PlayerEntity){

    }
}
