package com.oyosite.ticon.starship

import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry

object Util {
    fun <T, U:T> Registry<T>.register(id: Identifier, entry: U) = Registry.register(this, id, entry) as U
    fun <T, U:T> Registry<T>.register(id: String, entry: U) = register(identifier(id), entry)
    fun NbtCompound.getList(key: String, type: Byte) = getList(key, type.toInt())
    fun identifier(id: String) = Identifier(if(id.contains(":"))id else "${StarshipMod.MODID}:$id")
    fun BlockPos(nbt:NbtCompound) = BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"))
    fun NbtCompound.getBlockPos(key: String) = BlockPos(getCompound(key))
    fun NbtCompound.putBlockPos(key: String, pos: BlockPos) = put(key, NbtCompound().also { putInt("x",pos.x);putInt("y",pos.y);putInt("z",pos.z) })
    val Long.colorInt get() = (if(this and 0xff000000 == 0L)this or 0xff000000 else this).toUInt().toInt()
    val Int.colorInt get() = this.toLong().colorInt
}