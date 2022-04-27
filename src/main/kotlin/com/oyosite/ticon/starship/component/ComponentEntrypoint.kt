package com.oyosite.ticon.starship.component

import com.oyosite.ticon.starship.Util.identifier
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer

object ComponentEntrypoint : WorldComponentInitializer{
    val STARSHIP_COMPONENT = ComponentRegistryV3.INSTANCE.getOrCreate(identifier("starship_component"), StarshipWorldComponent::class.java)
    override fun registerWorldComponentFactories(registry: WorldComponentFactoryRegistry) {
        registry.register(STARSHIP_COMPONENT, StarshipWorldComponent::Impl)
    }
}