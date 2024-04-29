package io.github.seggan.swgv.client.minecraft.block.model

import io.github.seggan.swgv.ASSETS_DIR
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import ktx.assets.toAbsoluteFile

@Serializable
@Suppress("DataClassPrivateConstructor")
data class BlockModel private constructor(
    val textures: Map<String, ModelTexture> = emptyMap(),
    val elements: List<ModelElement> = emptyList(),
    @SerialName("ambientocclusion")
    val ambientOcclusion: Boolean = true
) {

    fun getTexture(texture: String): ModelTexture.Location {
        val tex = textures[texture] ?: throw IllegalArgumentException("Texture $texture not found")
        return when (tex) {
            is ModelTexture.Location -> tex
            is ModelTexture.Tag -> getTexture(tex.tag)
        }
    }

    companion object {

        private fun appendData(location: String, data: MutableMap<String, JsonElement>) {
            val file = "$ASSETS_DIR/minecraft/models/block/$location.json".toAbsoluteFile()
            val json = Json.parseToJsonElement(file.readString()).jsonObject.toMutableMap()
            json["parent"]?.let {
                appendData(it.jsonPrimitive.content.split('/')[1], json)
                json.remove("parent")
            }
            data.recursivelyPutAll(json)
        }

        private fun MutableMap<String, JsonElement>.recursivelyPutAll(obj: Map<String, JsonElement>) {
            for ((key, value) in obj) {
                if (value is JsonObject) {
                    val map = this[key]?.jsonObject?.toMutableMap() ?: mutableMapOf()
                    map.recursivelyPutAll(value)
                    put(key, JsonObject(map))
                } else {
                    put(key, value)
                }
            }
        }

        @OptIn(ExperimentalSerializationApi::class)
        private val decoder = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            decodeEnumsCaseInsensitive = true
        }

        fun getModel(name: String): BlockModel {
            val data = mutableMapOf<String, JsonElement>()
            appendData(name, data)
            return decoder.decodeFromJsonElement<BlockModel>(JsonObject(data))
        }
    }
}