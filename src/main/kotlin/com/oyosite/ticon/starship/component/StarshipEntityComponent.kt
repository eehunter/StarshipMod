package com.oyosite.ticon.starship.component

import com.oyosite.ticon.starship.*
import com.oyosite.ticon.starship.Util.getList
import com.oyosite.ticon.starship.Util.getBlockPos
import dev.onyxstudios.cca.api.v3.component.ComponentV3
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement.COMPOUND_TYPE
import net.minecraft.nbt.NbtList
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

interface StarshipEntityComponent : ComponentV3, AutoSyncedComponent {

    val teleporters: MutableList<TeleporterReference>

    var visibleTeleporters: MutableSet<TeleporterVisibility>

    val lastRefUpdate: Long

    class Impl(val provider: LivingEntity): StarshipEntityComponent{

        override val teleporters: MutableList<TeleporterReference> = mutableListOf(("starship" to BlockPos(512,256,512)).teleRef)

        override var visibleTeleporters = mutableSetOf<TeleporterVisibility>()

        private var _lastRefUpdate = 0L
        override val lastRefUpdate: Long get() = _lastRefUpdate

        private fun findVisibleTeleporters(): MutableList<TeleporterVisibility> {
            if(provider.world.isClient) throw AssertionError("StarshipEntityComponent.findVisibleTeleporters can only be run on the server side.")
            val worldsToCheck = setOf(*teleporters.map(TeleporterReference::first).toTypedArray())
            val otpt = mutableListOf<TeleporterVisibility>()
            worldsToCheck.forEach{worldKey->
                val world = provider.world.server!!.getWorld(worldKey)?:return@forEach
                val worldComp = ComponentEntrypoint.STARSHIP_WORLD_COMPONENT[world]
                val poses = teleporters.filter{it.first==worldKey}.map(TeleporterReference::second).toSet()
                //println(worldKey.value)
                //println(poses)
                worldComp.getTeleporters { poses.contains(it.first) }.map{ Triple(worldKey.value, it.first, it.second) }.forEach { if(!otpt.contains(it))otpt.add(it) }
            }
            return otpt
        }

        override fun writeSyncPacket(buf: PacketByteBuf, recipient: ServerPlayerEntity) {
            super.writeSyncPacket(buf, recipient)
            visibleTeleporters = findVisibleTeleporters().toMutableSet().apply {
                buf.writeInt(size)
                forEach { buf.writeTeleporterVisibility(it) }
            }
            recipient.world.time.let{_lastRefUpdate = it; buf.writeLong(it)}
        }

        override fun applySyncPacket(buf: PacketByteBuf) {
            super.applySyncPacket(buf)
            visibleTeleporters=HashSet(List(buf.readInt()){buf.readTeleporterVisibility()})
            _lastRefUpdate = buf.readLong()
        }

        override fun readFromNbt(tag: NbtCompound) {
            tag.getList("teleporters", COMPOUND_TYPE).forEach { teleporters += it as NbtCompound }
            println("Teleporters known by ${provider.entityName}: $teleporters")

        }

        override fun writeToNbt(tag: NbtCompound) {
            tag.put("teleporters", NbtList().apply{teleporters.forEach{add(it.writeToNbt())}})
        }
        companion object{
            operator fun MutableList<TeleporterReference>.plusAssign(nbt: NbtCompound) {
                val key = RegistryKey.of(Registry.WORLD_KEY, Identifier(nbt.getString("world")))
                val pos = BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"))//nbt.getBlockPos("pos")
                TeleporterReference(key, pos).also{if(!contains(it))this += it}
            }
        }
    }
}