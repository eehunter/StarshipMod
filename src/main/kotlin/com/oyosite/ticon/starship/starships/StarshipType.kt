package com.oyosite.ticon.starship.starships

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.oyosite.ticon.starship.Util.identifier
import com.oyosite.ticon.starship.Util.register
import io.github.apace100.calio.data.SerializableDataType
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos


open class StarshipType(val id: Identifier, val pos: BlockPos, val structure: Identifier, val defaultTeleporterPos: BlockPos?, val factory: StarshipType.(NbtCompound?, BlockPos)->Starship) {


    fun makeStarship(nbt: NbtCompound? = null, pos: BlockPos): Starship = factory(nbt, pos)

    companion object {
        //for debugging
        val GENERIC_STARSHIP_TYPE = StarshipType(identifier("generic"), BlockPos(512, 256, 512), identifier("none"), BlockPos(0,0,0)){ nbt, pos ->
            val starship = Starship(this, pos)
            if(nbt != null) starship.readFromNbt(nbt)
            starship
        }


        val REGISTRY = FabricRegistryBuilder.createSimple(StarshipType::class.java, identifier("starship_type")).buildAndRegister().apply{register(GENERIC_STARSHIP_TYPE.id, GENERIC_STARSHIP_TYPE)}



        private fun send(buf: PacketByteBuf, type: StarshipType): Unit = buf.run{
            writeIdentifier(type.id)
            writeBlockPos(type.pos)
            writeIdentifier(type.structure)
            (type.defaultTeleporterPos!=null).also { writeBoolean(it); if(it)writeBlockPos(type.defaultTeleporterPos) }
            Unit
        }
        private fun receive(buf: PacketByteBuf): StarshipType = buf.run { StarshipType(
            readIdentifier(),
            readBlockPos(),
            readIdentifier(),
            if(readBoolean())readBlockPos()else null
        ) { nbt, pos ->
            val starship = Starship(this, pos)
            if(nbt != null) starship.readFromNbt(nbt)
            else {

            }
            starship
        } }
        private fun read(json: JsonElement): StarshipType = read(json.asJsonObject)
        private fun read(json: JsonObject): StarshipType = StarshipType(
            Identifier(json["id"].asString),
            json.getAsJsonArray("structure_pos").map(JsonElement::getAsInt).let{BlockPos(it[0],it[1],it[2])},
            Identifier(json["structure_id"].asString),
            if(json.has("teleporter_pos"))json.getAsJsonArray("teleporter_pos").map(JsonElement::getAsInt).let{BlockPos(it[0],it[1],it[2])} else null,
        ) { nbt, pos ->
            val starship = Starship(this, pos)
            if(nbt != null) starship.readFromNbt(nbt)
            else {

            }
            starship
        }
        val DATA_TYPE: SerializableDataType<StarshipType> = SerializableDataType(StarshipType::class.java, Companion::send, Companion::receive, Companion::read)
    }
}