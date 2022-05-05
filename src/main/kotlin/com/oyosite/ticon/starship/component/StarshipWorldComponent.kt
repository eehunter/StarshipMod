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

    fun getOrigin(index: Int) = BlockPos((index%16)*1024+512, 256, index.floorDiv(16)*1024+512)

    fun addStarship(starship: Starship): Int

    fun addTeleporter(pos: BlockPos, name: String? = null): Boolean
    fun removeTeleporter(pos: BlockPos): Boolean
    fun getTeleporters(): List<Pair<BlockPos, String?>>
    fun getTeleporters(predicate: (Pair<BlockPos, String?>)->Boolean): List<Pair<BlockPos, String?>>

    class Impl(val provider: World) : StarshipWorldComponent, AutoSyncedComponent{
        /**
         * Starships should only exist in the starship world
         * */
        private val starships = mutableListOf<Starship?>()
        /**
         * Teleporters can exist in any world
         * */
        private val teleporters = mutableMapOf<BlockPos, String?>()

        override fun get(index: Int): Starship? = starships.getOrNull(index)

        override fun getIndex(x: Int, y: Int): Int = y*16+x

        override fun readFromNbt(tag: NbtCompound) {
            tag.getList("starships", NbtElement.COMPOUND_TYPE).map { it as NbtCompound }.forEachIndexed { i, it -> starships += StarshipType.REGISTRY.get(identifier(it.getString("type")))?.makeStarship(it.getCompound("data"), getOrigin(i)) }
            tag.getList("teleporters", NbtElement.COMPOUND_TYPE).map { it as NbtCompound }.forEach { teleporters[BlockPos(it.getInt("x"),it.getInt("y"),it.getInt("z"))] = if(it.contains("name"))it.getString("name")else null }
        }

        override fun writeToNbt(tag: NbtCompound) {
            tag.put("starships", NbtList().apply{addAll(starships.map{NbtCompound().apply{putString("type", it?.type?.id?.toString() ?: "");put("data",it?.writeToNbt()?:NbtCompound())}})})
            tag.put("teleporters", NbtList().apply{addAll(teleporters.map{NbtCompound().apply{putInt("x",it.key.x);putInt("y",it.key.y);putInt("z",it.key.z);if(it.value!=null)putString("name", it.value)}})})
        }

        override fun addStarship(starship: Starship): Int {
            if(!starships.contains(starship))starships.add(starship)
            teleporters.forEach{(pos,_)-> if(this[pos]==starship)starship.teleporters.add(pos) }
            return starships.indexOf(starship)
        }

        override fun getTeleporters(): List<Pair<BlockPos, String?>> = teleporters.toList()
        override fun getTeleporters(predicate: (Pair<BlockPos, String?>) -> Boolean): List<Pair<BlockPos, String?>> = teleporters.entries.map{Pair(it.key,it.value)}.filter(predicate)
        override fun addTeleporter(pos: BlockPos, name: String?): Boolean = !teleporters.containsKey(pos).also { if(!it||name!=null)teleporters[pos] = name }
        override fun removeTeleporter(pos: BlockPos): Boolean = teleporters.containsKey(pos).also{if(it)teleporters.remove(pos)}
    }
}