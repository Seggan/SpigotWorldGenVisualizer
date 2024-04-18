package io.github.seggan.swgv

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

fun main() {
    Lwjgl3Application(Main(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("SpigotWorldGenVis")
        setWindowedMode(640, 480)
    })
}
