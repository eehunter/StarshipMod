package com.oyosite.ticon.starship

import com.oyosite.ticon.starship.Util.identifier
import com.oyosite.ticon.starship.Util.register
import com.oyosite.ticon.starship.block.StarshipBlocks
import com.oyosite.ticon.starship.component.ComponentEntrypoint
import com.oyosite.ticon.starship.gui.TeleporterGuiDescription
import com.oyosite.ticon.starship.starships.StarshipType
import com.oyosite.ticon.starship.world.VoidChunkGenerator
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.TeleportTarget
import net.minecraft.world.World


object StarshipMod : ModInitializer {
    const val MODID = "starship"
    val STARSHIP_WORLD: RegistryKey<World> = RegistryKey.of(Registry.WORLD_KEY, Identifier(MODID, "starship"))
    val TELEPORTER_SCREEN_HANDLER: ScreenHandlerType<TeleporterGuiDescription> by lazy {Registry.SCREEN_HANDLER.register("teleporter", ScreenHandlerType(::TeleporterGuiDescription))}
    val TELEPORT_PACKET_ID = identifier("teleport_player")
    val TELEPORTER_RENAME_PACKET_ID = identifier("rename_teleporter")
    override fun onInitialize() {
        Registry.CHUNK_GENERATOR.register("starship", VoidChunkGenerator.CODEC)
        StarshipBlocks.register()
        StarshipType
        TELEPORTER_SCREEN_HANDLER
        ServerPlayNetworking.registerGlobalReceiver(TELEPORT_PACKET_ID){server,player,_,buf,_->
            val key = RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier())
            val pos = buf.readBlockPos()
            server.execute {
                FabricDimensions.teleport(player, player.getWorld().server.getWorld(key), TeleportTarget(pos.run { Vec3d(x.toDouble() + .5, y.toDouble() + 1, z.toDouble() + .5) }, Vec3d.ZERO, player.yaw, player.pitch))
                player.closeHandledScreen()
            }
        }
        ServerPlayNetworking.registerGlobalReceiver(TELEPORTER_RENAME_PACKET_ID){server,_,_,buf,_->
            val pos = buf.readBlockPos()
            val world = server.getWorld(RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier()))?:return@registerGlobalReceiver Unit.apply { NullPointerException("World not found").printStackTrace() }
            val name = buf.readString()
            server.execute{
                val comp = ComponentEntrypoint.STARSHIP_WORLD_COMPONENT[world]
                if(comp.getTeleporters { (p,_)->p==pos }.isEmpty()) return@execute Unit.apply { RuntimeException("Tried to rename unregistered teleporter").printStackTrace() }
                comp.addTeleporter(pos, name)
            }
        }
    }
}