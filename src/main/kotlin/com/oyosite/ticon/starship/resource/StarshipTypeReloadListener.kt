package com.oyosite.ticon.starship.resource

import com.google.gson.JsonObject
import com.oyosite.ticon.starship.Util.identifier
import com.oyosite.ticon.starship.Util.register
import com.oyosite.ticon.starship.starships.StarshipType
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object StarshipTypeReloadListener: SimpleSynchronousResourceReloadListener {
    override fun getFabricId(): Identifier = identifier("starship_types")
    override fun reload(manager: ResourceManager) = manager.findResources("starship_types"){it.endsWith(".json")}.forEach{try{
        val stream = manager.getResource(it).inputStream
        val reader = BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8))
        val str = reader.lines().toList().joinToString("\n")
        val json: JsonObject = JsonHelper.deserialize(str)
        Identifier(it.namespace,it.path.apply{slice(15..length-6)}).also { id -> StarshipType.REGISTRY.register(id, StarshipType.DATA_TYPE.read(json.apply{addProperty("id", id.toString())})) }
    }catch (e: Exception) { e.printStackTrace() } }

}