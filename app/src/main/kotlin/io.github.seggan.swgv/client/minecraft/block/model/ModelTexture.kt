package io.github.seggan.swgv.client.minecraft.block.model

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.Texture
import io.github.seggan.swgv.ASSETS_DIR
import io.github.seggan.swgv.TEMP_DIR
import io.github.seggan.swgv.client.minecraft.block.BlockCache
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import ktx.assets.toAbsoluteFile

@Serializable(with = ModelTextureSerializer::class)
sealed interface ModelTexture {
    data class Texture(val texture: String) : ModelTexture {
        fun blockTexture(): com.badlogic.gdx.graphics.Texture {
            return BlockCache.textures.getOrPut(texture) {
                val original = Texture("$ASSETS_DIR/minecraft/textures/block/$texture.png".toAbsoluteFile())
                if (texture !in UNTRANSPARENTIFY) return@getOrPut original
                original.textureData.prepare()
                val pixmap = original.textureData.consumePixmap()
                val isAlpha = pixmap.format == Pixmap.Format.Alpha || pixmap.format == Pixmap.Format.LuminanceAlpha
                val newPixmap = Pixmap(pixmap.width, pixmap.height, Pixmap.Format.RGBA8888)
                for (x in 0 until pixmap.width) {
                    for (y in 0 until pixmap.height) {
                        val color = Color(pixmap.getPixel(x, y))
                        if (isAlpha) {
                            color.r = 0f
                            color.g = 0f
                            color.b = 0f
                        }
                        color.r *= color.a
                        color.g *= color.a
                        color.b *= color.a
                        color.r += 1 - color.a
                        color.g += 1 - color.a
                        color.b += 1 - color.a
                        color.a = 1f
                        newPixmap.drawPixel(x, y, Color.rgba8888(color))
                    }
                }
                val new = Texture(newPixmap)
                original.dispose()
                pixmap.dispose()
                new
            }
        }
    }

    data class Tag(val tag: String) : ModelTexture
}

private val UNTRANSPARENTIFY = setOf("stone")

private object ModelTextureSerializer : KSerializer<ModelTexture> {

    override val descriptor = PrimitiveSerialDescriptor("ModelTexture", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ModelTexture {
        val value = decoder.decodeString()
        return if (value.startsWith("#")) {
            ModelTexture.Tag(value.substring(1))
        } else {
            ModelTexture.Texture(value.substringAfter('/'))
        }
    }

    override fun serialize(encoder: Encoder, value: ModelTexture) {
        throw UnsupportedOperationException("Serialization is not supported")
    }
}