package com.oyosite.ticon.starship.starships

import com.oyosite.ticon.starship.Util.identifier
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier

open class StarshipType(val id: Identifier, val factory: (NbtCompound?)->Starship) {
    
    fun makeStarship(nbt: NbtCompound? = null): Starship = factory(nbt)

    companion object {
        val REGISTRY = FabricRegistryBuilder.createSimple(StarshipType::class.java, identifier("starship_type")).buildAndRegister()
    }
}