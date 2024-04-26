package io.github.seggan.testplugin

import io.github.seggan.swgv.visualizeWorldGeneration
import org.bukkit.plugin.java.JavaPlugin

open class TestPlugin : JavaPlugin()

fun main() {
    visualizeWorldGeneration(
        "1.20.2",
        TestPlugin::class.java,
        TestGenerator
    )
}