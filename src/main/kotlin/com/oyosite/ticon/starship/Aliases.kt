package com.oyosite.ticon.starship

import com.oyosite.ticon.starship.Util.identifier
import com.oyosite.ticon.starship.Util.putBlockPos
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

typealias TeleporterReference = Pair<RegistryKey<World>, BlockPos>
fun TeleporterReference.writeToNbt() = NbtCompound().apply { putString("world", first.value.toString());second.apply{putInt("x",x);putInt("y",y);putInt("z",z)} }
val Pair<String, BlockPos>.teleRef get() = RegistryKey.of(Registry.WORLD_KEY, identifier(first)) to second

typealias TeleporterVisibility = Triple<Identifier, BlockPos, String?>
fun PacketByteBuf.writeTeleporterVisibility(visibility: TeleporterVisibility) {
    writeIdentifier(visibility.first)
    writeBlockPos(visibility.second)
    (visibility.third!=null).also { writeBoolean(it); if(it)writeString(visibility.third) }
}
fun PacketByteBuf.readTeleporterVisibility() = TeleporterVisibility(readIdentifier(),readBlockPos(),if(readBoolean())readString()else null)