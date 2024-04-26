package io.github.seggan.swgv.client.minecraft.block.model

import com.badlogic.gdx.graphics.Texture
import io.github.seggan.swgv.ASSETS_DIR
import io.github.seggan.swgv.TEMP_DIR
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
            return Texture("$ASSETS_DIR/minecraft/textures/block/$texture.png".toAbsoluteFile())
        }
    }

    data class Tag(val tag: String) : ModelTexture
}

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