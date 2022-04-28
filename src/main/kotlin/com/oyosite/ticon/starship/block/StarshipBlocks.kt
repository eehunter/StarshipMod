package com.oyosite.ticon.starship.block

import com.oyosite.ticon.starship.Util.register
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.util.registry.Registry

object StarshipBlocks {
    private val BLOCKS = mutableListOf<Pair<String, Block>>()

    val TELEPORTER = "teleporter" block TeleporterBlock(Material.METAL){hardness(1f)}






    infix fun <T: Block> String.block(block: T) = block.also{ BLOCKS.add(this to it) }
    fun register() = BLOCKS.forEach { Registry.BLOCK.register(it.first, it.second) }


}