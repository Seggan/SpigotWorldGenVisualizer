package io.github.seggan.swgv.client.minecraft.block.model

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.block.BlockFace
import java.util.*

@Serializable
@Suppress("DataClassPrivateConstructor")
data class ModelElement private constructor(
    @Serializable(with = Vector3Serializer::class)
    val from: Vector3,
    @Serializable(with = Vector3Serializer::class)
    val to: Vector3,
    val faces: Map<BlockFace, FaceInfo> = emptyMap(),
    val rotation: Rotation? = null,
    val shade: Boolean = true
) {

    fun getFaceModels(model: BlockModel): List<ElementFace> {
        val attr = (Usage.Position or
                Usage.Normal or
                Usage.TextureCoordinates
                ).toLong()
        val blending = BlendingAttribute(false, 1f)
        val textures = mutableMapOf<String, Texture>()
        val models = mutableListOf<ElementFace>()
        for ((face, data) in faces) {
            val modelBuilder = ModelBuilder()
            val vertices = face.toRect(from, to)
            val texture = textures.computeIfAbsent(data.texture.substringAfter("#")) {
                model.getTexture(it).blockTexture()
            }
            val region = TextureRegion(texture)
            region.u = data.uv[0] / 16f
            region.v = data.uv[1] / 16f
            region.u2 = data.uv[2] / 16f
            region.v2 = data.uv[3] / 16f
            val normal = normals[face]!!
            val result = modelBuilder.createRect(
                vertices[0] / 16f, vertices[1] / 16f, vertices[2] / 16f,
                vertices[3] / 16f, vertices[4] / 16f, vertices[5] / 16f,
                vertices[6] / 16f, vertices[7] / 16f, vertices[8] / 16f,
                vertices[9] / 16f, vertices[10] / 16f, vertices[11] / 16f,
                normal.x, normal.y, normal.z,
                Material(
                    TextureAttribute.createDiffuse(region),
                    blending,
                    DepthTestAttribute(true)
                ),
                attr
            )
            models.add(ElementFace(face, result))
        }
        return models
    }

    @Serializable
    data class Rotation private constructor(
        @Serializable(with = Vector3Serializer::class)
        val origin: Vector3,
        val axis: String,
        val angle: Float,
        val rescale: Boolean = false
    )

    @Serializable
    @Suppress("ArrayInDataClass")
    data class FaceInfo private constructor(
        val texture: String,
        val uv: IntArray = intArrayOf(0, 0, 16, 16),
        @SerialName("cullface")
        val cullFace: String = texture,
        val rotation: Int = 0,
        @SerialName("tintindex")
        val tintIndex: Int = -1
    ) {
        override fun toString(): String {
            return "FaceInfo(texture='$texture', uv=${uv.contentToString()}, cullFace=$cullFace, rotation=$rotation, tintIndex=$tintIndex)"
        }
    }
}

private fun BlockFace.toRect(from: Vector3, to: Vector3): FloatArray {
    return when (this) {
        BlockFace.UP -> floatArrayOf(
            from.x, to.y, from.z,
            from.x, to.y, to.z,
            to.x, to.y, to.z,
            to.x, to.y, from.z
        )

        BlockFace.DOWN -> floatArrayOf(
            to.x, from.y, from.z,
            to.x, from.y, to.z,
            from.x, from.y, to.z,
            from.x, from.y, from.z
        )

        BlockFace.NORTH -> floatArrayOf(
            to.x, from.y, from.z,
            from.x, from.y, from.z,
            from.x, to.y, from.z,
            to.x, to.y, from.z
        )

        BlockFace.SOUTH -> floatArrayOf(
            from.x, from.y, to.z,
            to.x, from.y, to.z,
            to.x, to.y, to.z,
            from.x, to.y, to.z
        )

        BlockFace.EAST -> floatArrayOf(
            to.x, from.y, to.z,
            to.x, from.y, from.z,
            to.x, to.y, from.z,
            to.x, to.y, to.z
        )

        BlockFace.WEST -> floatArrayOf(
            from.x, from.y, from.z,
            from.x, from.y, to.z,
            from.x, to.y, to.z,
            from.x, to.y, from.z
        )

        else -> throw IllegalArgumentException("Unsupported face: $this")
    }
}

private val normals = BlockFace.entries.associateWithTo(EnumMap(BlockFace::class.java)) {
    Vector3(it.modX.toFloat(), it.modY.toFloat(), it.modZ.toFloat())
}

@OptIn(ExperimentalSerializationApi::class)
private object Vector3Serializer : KSerializer<Vector3> {

    private val delegate = serializer<FloatArray>()
    override val descriptor = SerialDescriptor("Vector3", delegate.descriptor)

    override fun deserialize(decoder: Decoder): Vector3 {
        val array = delegate.deserialize(decoder)
        return Vector3(array[0], array[1], array[2])
    }

    override fun serialize(encoder: Encoder, value: Vector3) {
        delegate.serialize(encoder, floatArrayOf(value.x, value.y, value.z))
    }
}