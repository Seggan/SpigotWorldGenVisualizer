plugins {
    kotlin("jvm")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    application
}

dependencies {
    implementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation(project(":app"))
}

application {
    mainClass = "io.github.seggan.testplugin.TestPluginKt"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

bukkit {
    name = "SpigotWorldGenVis"
    version = "1.0"
    description = "Visualizes world generation in Spigot"
    main = "io.github.seggan.testplugin.TestPlugin"
    authors = listOf("Seggan")
}