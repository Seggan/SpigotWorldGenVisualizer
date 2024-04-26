@file:JvmName("WorldGenVisualizer")

package io.github.seggan.swgv

import be.seeseemelk.mockbukkit.MockBukkit
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import io.github.seggan.swgv.client.Client
import kotlinx.serialization.json.*
import org.bukkit.WorldCreator
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.java.JavaPlugin
import java.net.URI
import java.nio.file.Path
import java.util.zip.ZipInputStream
import kotlin.io.path.*

@OptIn(ExperimentalPathApi::class)
fun visualizeWorldGeneration(
    minecraftVersion: String,
    plugin: Class<out JavaPlugin>,
    generator: ChunkGenerator,
    vararg otherPlugins: Class<out JavaPlugin>
) {
    val mcJar = TEMP_DIR / "minecraft-$minecraftVersion.jar"
    if (!mcJar.exists()) {
        TEMP_DIR.deleteRecursively()
        TEMP_DIR.createDirectories()
        println("Downloading Minecraft $minecraftVersion")
        downloadMinecraftJar(minecraftVersion, mcJar)
        println("Extracting Minecraft $minecraftVersion")
        extractMinecraftJar(mcJar)
    }
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

val TEMP_DIR = Path(System.getProperty("java.io.tmpdir"), "swgv")
val ASSETS_DIR = TEMP_DIR / "assets"

private fun downloadMinecraftJar(version: String, path: Path) {
    val versions = URI("https://launchermeta.mojang.com/mc/game/version_manifest.json").toURL().readText()
    val versionList = Json.decodeFromString<JsonObject>(versions)["versions"]!!.jsonArray
    val versionInfo = versionList.firstOrNull { it.jsonObject["id"]!!.jsonPrimitive.content == version }
        ?.jsonObject?.get("url")?.jsonPrimitive?.content ?: throw IllegalArgumentException("Version $version not found")
    val versionData = URI(versionInfo).toURL().readText()
    val jarUrl = Json.decodeFromString<JsonObject>(versionData)["downloads"]!!
        .jsonObject["client"]!!.jsonObject["url"]!!.jsonPrimitive.content
    URI(jarUrl).toURL().openStream().use { input ->
        path.outputStream().use { output -> input.copyTo(output) }
    }
}

private fun extractMinecraftJar(jar: Path) {
    ZipInputStream(jar.inputStream()).use { zip ->
        while (true) {
            val entry = zip.nextEntry ?: break
            val entryPath = TEMP_DIR / entry.name
            if (entry.isDirectory) {
                entryPath.createDirectories()
            } else if (!entry.name.endsWith(".class")) {
                entryPath.parent.createDirectories()
                entryPath.createFile().outputStream().use { output -> zip.copyTo(output) }
            }
        }
    }
}
