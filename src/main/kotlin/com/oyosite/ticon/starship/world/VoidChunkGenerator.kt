package com.oyosite.ticon.starship.world

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.structure.StructureSet
import net.minecraft.util.dynamic.RegistryOps
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryEntryList
import net.minecraft.world.ChunkRegion
import net.minecraft.world.HeightLimitView
import net.minecraft.world.Heightmap
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeKeys
import net.minecraft.world.biome.source.BiomeAccess
import net.minecraft.world.biome.source.FixedBiomeSource
import net.minecraft.world.biome.source.util.MultiNoiseUtil
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.Blender
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.chunk.VerticalBlockSample
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.BiFunction

/**
 * This is a kotlin adaptation of me.modmuss50.svw.VoidChunkGenerator, which is available under an MIT license
 * The original can be found here: https://github.com/modmuss50/SimpleVoidWorld/blob/1.18/src/main/java/me/modmuss50/svw/VoidChunkGenerator.java
 * */
open class VoidChunkGenerator(structures: Registry<StructureSet>, val biomes: Registry<Biome>) : ChunkGenerator(structures, Optional.of(RegistryEntryList.of(Collections.emptyList())), FixedBiomeSource(biomes.getOrCreateEntry(BiomeKeys.PLAINS))) {
    companion object {val CODEC: Codec<VoidChunkGenerator> = RecordCodecBuilder.create {method_41042(it).and(RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter{ i->i.biomes}).apply(it, it.stable(BiFunction(::VoidChunkGenerator)))}}
    override fun getCodec(): Codec<out ChunkGenerator> = CODEC
    override fun withSeed(seed: Long): ChunkGenerator = this
    override fun getMultiNoiseSampler(): MultiNoiseUtil.MultiNoiseSampler = MultiNoiseUtil.method_40443()
    override fun carve(chunkRegion: ChunkRegion?, seed: Long, biomeAccess: BiomeAccess?, structureAccessor: StructureAccessor?, chunk: Chunk?, generationStep: GenerationStep.Carver?) = Unit
    override fun buildSurface(region: ChunkRegion?, structures: StructureAccessor?, chunk: Chunk?) = Unit
    override fun populateEntities(region: ChunkRegion?) = Unit
    override fun getWorldHeight(): Int = 512
    override fun populateNoise(executor: Executor?, blender: Blender?, structureAccessor: StructureAccessor?, chunk: Chunk?): CompletableFuture<Chunk> = CompletableFuture.completedFuture(chunk)
    override fun getSeaLevel(): Int = 64
    override fun getMinimumY(): Int = 0
    override fun getHeight(x: Int, z: Int, heightmap: Heightmap.Type?, world: HeightLimitView?): Int = 255
    override fun getColumnSample(x: Int, z: Int, world: HeightLimitView?): VerticalBlockSample = VerticalBlockSample(0, arrayOf())
    override fun getDebugHudText(text: MutableList<String>?, pos: BlockPos?) = Unit
}