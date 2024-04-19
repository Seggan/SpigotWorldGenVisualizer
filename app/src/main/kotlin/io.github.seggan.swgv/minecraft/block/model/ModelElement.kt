package io.github.seggan.swgv.minecraft.block.model

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import io.github.seggan.swgv.util.identityHashSet
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Collections
import java.util.IdentityHashMap

@Serializable
@Suppress("DataClassPrivateConstructor")
data class ModelElement private constructor(
    @Serializable(with = Vector3Serializer::class)
    val from: Vector3,
    @Serializable(with = Vector3Serializer::class)
    val to: Vector3,
    val faces: Map<Face, FaceInfo> = emptyMap(),
    val rotation: Rotation? = null,
    val shade: Boolean = true
) {

    fun render(builder: ModelBuilder, model: BlockModel): Collection<Texture> {
        val textures = mutableMapOf<String, Texture>()
        val attr =
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()
        val blending = BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        for ((face, data) in faces.toSortedMap()) {
            val (vertices, normal) = face.vertexSelector(from, to)
            val texture = textures.computeIfAbsent(data.texture.substringAfter("#")) {
                model.getTexture(it).blockTexture()
            }
            builder.part(
                face.name,
                GL20.GL_TRIANGLES,
                attr,
                Material(TextureAttribute.createDiffuse(texture), blending),
            ).rect(
                vertices[0] / 16, vertices[1] / 16, vertices[2] / 16,
                vertices[3] / 16, vertices[4] / 16, vertices[5] / 16,
                vertices[6] / 16, vertices[7] / 16, vertices[8] / 16,
                vertices[9] / 16, vertices[10] / 16, vertices[11] / 16,
                normal.x, normal.y, normal.z
            )
        }
        return textures.values
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
        val cullFace: String? = null,
        val rotation: Int = 0,
        @SerialName("tintindex")
        val tintIndex: Int = -1
    ) {
        override fun toString(): String {
            return "FaceInfo(texture='$texture', uv=${uv.contentToString()}, cullFace=$cullFace, rotation=$rotation, tintIndex=$tintIndex)"
        }
    }

    @Serializable
    enum class Face(val vertexSelector: (from: Vector3, to: Vector3) -> Pair<FloatArray, Vector3>) {
        UP({ from, to ->
            floatArrayOf(
                from.x, to.y, from.z,
                from.x, to.y, to.z,
                to.x, to.y, to.z,
                to.x, to.y, from.z
            ) to Vector3(0f, 1f, 0f)
        }),
        DOWN({ from, to ->
            floatArrayOf(
                from.x, from.y, from.z,
                from.x, from.y, to.z,
                to.x, from.y, to.z,
                to.x, from.y, from.z
            ) to Vector3(0f, -1f, 0f)
        }),
        NORTH({ from, to ->
            floatArrayOf(
                from.x, from.y, from.z,
                from.x, to.y, from.z,
                to.x, to.y, from.z,
                to.x, from.y, from.z
            ) to Vector3(0f, 0f, -1f)
        }),
        SOUTH({ from, to ->
            floatArrayOf(
                from.x, from.y, to.z,
                to.x, from.y, to.z,
                to.x, to.y, to.z,
                from.x, to.y, to.z
            ) to Vector3(0f, 0f, 1f)
        }),
        EAST({ from, to ->
            floatArrayOf(
                to.x, from.y, from.z,
                to.x, to.y, from.z,
                to.x, to.y, to.z,
                to.x, from.y, to.z
            ) to Vector3(1f, 0f, 0f)
        }),
        WEST({ from, to ->
            floatArrayOf(
                from.x, from.y, from.z,
                from.x, from.y, to.z,
                from.x, to.y, to.z,
                from.x, to.y, from.z
            ) to Vector3(-1f, 0f, 0f)
        });
    }
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