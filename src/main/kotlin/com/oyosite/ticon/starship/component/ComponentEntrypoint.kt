package com.oyosite.ticon.starship.component

import com.oyosite.ticon.starship.Util.identifier
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer

object ComponentEntrypoint : WorldComponentInitializer, EntityComponentInitializer{
    val STARSHIP_WORLD_COMPONENT: ComponentKey<StarshipWorldComponent> = ComponentRegistryV3.INSTANCE.getOrCreate(identifier("starship_component"), StarshipWorldComponent::class.java)
    /**
     * As of now, IDK of any reason this would be registered for non players, but I'll let that be an option in case there's a reason for it
     * */
    val STARSHIP_ENTITY_COMPONENT: ComponentKey<StarshipEntityComponent> = ComponentRegistryV3.INSTANCE.getOrCreate(identifier("starship_entity_component"), StarshipEntityComponent::class.java)
    override fun registerWorldComponentFactories(registry: WorldComponentFactoryRegistry) {
        registry.register(STARSHIP_WORLD_COMPONENT, StarshipWorldComponent::Impl)
    }

    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerForPlayers(STARSHIP_ENTITY_COMPONENT, StarshipEntityComponent::Impl, RespawnCopyStrategy.CHARACTER)
    }
}