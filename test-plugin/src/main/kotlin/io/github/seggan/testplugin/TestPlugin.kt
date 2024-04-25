package io.github.seggan.testplugin

import io.github.seggan.swgv.visualizeWorldGeneration
import org.bukkit.plugin.java.JavaPlugin

open class TestPlugin : JavaPlugin()

fun main() {
    visualizeWorldGeneration(
        TestPlugin::class.java,
        TestGenerator
    )
}