@file:JvmName("WorldGenVisualizer")

package io.github.seggan.swgv

import be.seeseemelk.mockbukkit.MockBukkit
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import io.github.seggan.swgv.client.Client
import org.bukkit.Bukkit
import org.bukkit.WorldCreator
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.java.JavaPlugin

fun visualizeWorldGeneration(
    plugin: Class<out JavaPlugin>,
    generator: ChunkGenerator,
    vararg otherPlugins: Class<out JavaPlugin>
) {
    MockBukkit.mock()
    try {
        for (otherPlugin in otherPlugins) {
            MockBukkit.load(otherPlugin)
        }
        MockBukkit.load(plugin)
        val world = WorldCreator("world").createWorld()!!
        Lwjgl3Application(Client(world, generator), Lwjgl3ApplicationConfiguration().apply {
            setTitle("SpigotWorldGenVis")
            setWindowedMode(640, 480)
            setResizable(true)
        })
    } finally {
        MockBukkit.unmock()
    }
}
