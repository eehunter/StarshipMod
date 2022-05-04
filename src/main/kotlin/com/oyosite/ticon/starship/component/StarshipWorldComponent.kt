package com.oyosite.ticon.starship.component

import com.oyosite.ticon.starship.starships.Starship
import com.oyosite.ticon.starship.Util.getList
import com.oyosite.ticon.starship.Util.identifier
import com.oyosite.ticon.starship.starships.StarshipType
import dev.onyxstudios.cca.api.v3.component.ComponentV3
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface StarshipWorldComponent : ComponentV3 {
    operator fun get(index: Int): Starship?
    operator fun get(x: Int, y: Int): Starship? = this[getIndex(x,y)]
    operator fun get(pos: BlockPos): Starship? = this[pos.x.floorDiv(1024), pos.y.floorDiv(1024)]
    fun getIndex(x: Int, y: Int): Int
    fun getIndex(pos: BlockPos): Int = getIndex(pos.x.floorDiv(1024), pos.y.floorDiv(1024))

    fun addStarship(starship: Starship): Int

    class Impl(val provider: World) : StarshipWorldComponent, AutoSyncedComponent{
        private val starships = mutableListOf<Starship?>()

        fun getOrigin(index: Int) = BlockPos((index%16)*1024+512, 256, index.floorDiv(16)*1024+512)

        override fun get(index: Int): Starship? = starships.getOrNull(index)

        override fun getIndex(x: Int, y: Int): Int = y*16+x

        override fun readFromNbt(tag: NbtCompound) = tag.getList("starships", NbtElement.COMPOUND_TYPE).map{it as NbtCompound}.forEachIndexed{ i, it -> starships += StarshipType.REGISTRY.get(identifier(it.getString("type")))?.makeStarship(it.getCompound("data"), getOrigin(i)) }

        override fun writeToNbt(tag: NbtCompound) {tag.put("starships", NbtList().apply{addAll(starships.map{NbtCompound().apply{putString("type", it?.type?.id?.toString() ?: "");put("data",it?.writeToNbt()?:NbtCompound())}})})}

        override fun addStarship(starship: Starship): Int {if(!starships.contains(starship))starships.add(starship);return starships.indexOf(starship)}
    }
}