package com.oyosite.ticon.starship.starships

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.oyosite.ticon.starship.Util.identifier
import io.github.apace100.calio.data.SerializableDataType
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos


open class StarshipType(val id: Identifier, val pos: BlockPos, val structure: Identifier, val defaultTeleporterPos: BlockPos?, val factory: StarshipType.(NbtCompound?, BlockPos)->Starship) {


    fun makeStarship(nbt: NbtCompound? = null, pos: BlockPos): Starship = factory(nbt, pos)

    companion object {
        val REGISTRY = FabricRegistryBuilder.createSimple(StarshipType::class.java, identifier("starship_type")).buildAndRegister()

        //for debugging
        val GENERIC_STARSHIP_TYPE = StarshipType(identifier("generic"), BlockPos(512, 256, 512), identifier("none"), null){ nbt, pos ->
            val starship = Starship(this, pos)
            if(nbt != null) starship.readFromNbt(nbt)
            starship
        }

        private fun send(buf: PacketByteBuf, type: StarshipType): Unit = TODO()
        private fun receive(buf: PacketByteBuf): StarshipType = TODO()
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