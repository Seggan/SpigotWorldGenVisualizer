package io.github.seggan.swgv.client.minecraft.block

import com.badlogic.gdx.utils.Disposable
import io.github.seggan.swgv.client.minecraft.McMaterial
import java.util.EnumMap

object BlockCache : Disposable {

    private val blocks = EnumMap<McMaterial, Block>(McMaterial::class.java)

    operator fun get(material: McMaterial): Block? {
        if (material.isEmpty) return null
        return blocks.computeIfAbsent(material, ::Block)
    }

    override fun dispose() {
        blocks.values.forEach(Block::dispose)
        blocks.clear()
    }
}