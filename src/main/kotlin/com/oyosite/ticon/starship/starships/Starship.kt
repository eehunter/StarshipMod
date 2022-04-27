package com.oyosite.ticon.starship.starships

import net.minecraft.nbt.NbtCompound

abstract class Starship(val type: StarshipType){
    abstract fun readFromNbt(nbt: NbtCompound)
    abstract fun writeToNbt(): NbtCompound
}
