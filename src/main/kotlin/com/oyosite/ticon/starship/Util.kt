package com.oyosite.ticon.starship

import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry

object Util {
    fun <T> Registry<T>.register(id: Identifier, entry: T) = Registry.register(this, id, entry)
    fun <T> Registry<T>.register(id: String, entry: T) = register(identifier(id), entry)
    fun NbtCompound.getList(key: String, type: Byte) = getList(key, type.toInt())
    fun identifier(id: String) = Identifier(if(id.contains(":"))id else "${StarshipMod.MODID}:$id")
    fun BlockPos(nbt:NbtCompound) = BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"))
    fun NbtCompound.getBlockPos(key: String) = BlockPos(getCompound(key))
    fun NbtCompound.putBlockPos(key: String, pos: BlockPos) = put(key, NbtCompound().also { putInt("x",pos.x);putInt("y",pos.y);putInt("z",pos.z) })
}