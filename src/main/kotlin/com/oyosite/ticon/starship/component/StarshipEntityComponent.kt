package com.oyosite.ticon.starship.component

import dev.onyxstudios.cca.api.v3.component.ComponentV3
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.NbtCompound

interface StarshipEntityComponent : ComponentV3 {



    class Impl(val provider: LivingEntity): StarshipEntityComponent{
        override fun readFromNbt(tag: NbtCompound) {

        }

        override fun writeToNbt(tag: NbtCompound) {

        }

    }
}