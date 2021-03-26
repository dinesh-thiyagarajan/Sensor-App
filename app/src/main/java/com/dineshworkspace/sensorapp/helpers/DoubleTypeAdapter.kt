package com.dineshworkspace.sensorapp.helpers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type


class DoubleTypeAdapter : JsonDeserializer<Double?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        jsonElement: JsonElement,
        type: Type?,
        jsonDeserializationContext: JsonDeserializationContext?
    ): Double? {
        var result: Double? = null
        result = try {
            jsonElement.asDouble
        } catch (e: NumberFormatException) {
            return 0.0
        }
        return result
    }
}