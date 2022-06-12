plugins {
    kotlin("jvm") version "1.7.0"
    id("fabric-loom")
    `maven-publish`
    java
}

group = property("maven_group")!!
version = property("mod_version")!!

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    mavenCentral()
    mavenLocal()
    maven {setUrl("https://maven.blamejared.com")}
    maven {setUrl("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")}
    maven {setUrl("https://maven.blamejared.com")}
    maven {setUrl("https://cfa2.cursemaven.com")}
    maven { setUrl("https://jitpack.io") }
    maven { setUrl("https://ladysnake.jfrog.io/artifactory/mods");name = "Ladysnake Libs" }
    maven { setUrl("https://maven.shedaniel.me/") }
    maven { setUrl("https://maven.terraformersmc.com/") }
    maven { setUrl("https://maven.jamieswhiteshirt.com/libs-release/");content{includeGroup ("com.jamieswhiteshirt")} }
    maven { setUrl("https://maven.bai.lol") }
    maven { setUrl("https://server.bbkr.space/artifactory/libs-release");content{includeGroup ("io.github.cottonmc")};name = "CottonMC" }
}

dependencies {
    fun ExternalModuleDependency.excludeFabric() = exclude ("net.fabricmc")
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")

    modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-base:${property("cca-version")}")
    include("dev.onyxstudios.cardinal-components-api:cardinal-components-base:${property("cca-version")}")

    modImplementation("com.github.apace100:apoli:${property("apoli_version")}"){excludeFabric()}
    include("com.github.apace100:apoli:${property("apoli_version")}"){excludeFabric()}

    modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-world:${property("cca-version")}")
    include("dev.onyxstudios.cardinal-components-api:cardinal-components-world:${property("cca-version")}")

    modImplementation("io.github.cottonmc:LibGui:${property("lib-gui-version")}"){excludeFabric()}
    include("io.github.cottonmc:LibGui:${property("lib-gui-version")}"){excludeFabric()}
    //modRuntimeOnly("mcp.mobius.waila:wthit:fabric-${property("wthit_version")}")
}

tasks {

    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to project.version))
        }
    }

    jar {
        from("LICENSE")
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifact(remapJar) {
                    builtBy(remapJar)
                }
                artifact(kotlinSourcesJar) {
                    builtBy(remapSourcesJar)
                }
            }
        }

        // select the repositories you want to publish to
        repositories {
            // uncomment to publish to the local maven
            // mavenLocal()
        }
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}



// configure the maven publication
