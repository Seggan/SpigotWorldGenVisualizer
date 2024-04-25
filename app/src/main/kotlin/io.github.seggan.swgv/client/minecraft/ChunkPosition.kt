package io.github.seggan.swgv.client.minecraft

import org.bukkit.Location

data class ChunkPosition(val x: Int, val z: Int) {

    val location: Location
        get() = Location(null, x * 16.0, 0.0, z * 16.0)
}

fun Location.toChunkPosition(): ChunkPosition {
    return ChunkPosition((x / 16).toInt(), (z / 16).toInt())
}
