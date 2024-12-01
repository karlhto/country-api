package karlhto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object CountrySerializer : KSerializer<Country> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CountryList") {
        element<JsonObject>("countries")
    }

    override fun deserialize(decoder: Decoder): Country {
        val json = decoder.decodeSerializableValue(JsonObject.serializer())
        val name = json["name"]?.jsonObject?.get("common")?.jsonPrimitive?.content ?: ""
        val region = json["region"]?.jsonPrimitive?.content ?: ""
        val currencies = json["currencies"]?.jsonObject?.keys?.toList() ?: emptyList()
        return Country(name, region, currencies)
    }

    override fun serialize(encoder: Encoder, value: Country) {
        // Serialization is not required for this task
        throw NotImplementedError("Serialization is not implemented")
    }
}