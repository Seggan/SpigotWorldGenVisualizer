package io.github.seggan.swgv.client.minecraft.block

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Disposable
import io.github.seggan.swgv.client.minecraft.McMaterial
import io.github.seggan.swgv.client.minecraft.block.model.BlockModel
import io.github.seggan.swgv.client.minecraft.block.model.ElementFace
import io.github.seggan.swgv.client.util.identityHashSet
import java.util.*

object BlockCache : Disposable {

    private val blocks = EnumMap<McMaterial, List<ElementFace>>(McMaterial::class.java)
    val textures = mutableMapOf<String, Texture>()

    operator fun get(material: McMaterial): List<ElementFace> {
        if (material.isEmpty) return emptyList()
        return blocks.getOrPut(material) {
            val model = BlockModel.getModel(material.name.lowercase())
            val (models, _) = model.elements.map { it.getFaceModels(model) }.unzip()
            models.flatten()
        }
    }

    override fun dispose() {
        textures.values.forEach(Texture::dispose)
        blocks.values.flatten().forEach(ElementFace::dispose)
        blocks.clear()
    }
}