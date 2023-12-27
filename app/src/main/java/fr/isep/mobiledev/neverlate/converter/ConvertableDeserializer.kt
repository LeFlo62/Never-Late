package fr.isep.mobiledev.neverlate.converter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type


class ConvertableDeserializer<T : Convertable> : JsonDeserializer<T> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): T {
        if(json == null || context == null) throw NullPointerException("json is null")
        val jsonObject: JsonObject = json.asJsonObject
        println("Deserialize : $jsonObject")
        val prim = jsonObject["className"] as JsonPrimitive
        val className = prim.asString
        val clazz: Class<T> = getClassInstance(className)
        return context.deserialize(jsonObject, clazz)
    }

    private fun getClassInstance(className: String?): Class<T> {
        return try {
            className?.let { Class.forName(it) } as Class<T>
        } catch (cnfe: ClassNotFoundException) {
            throw JsonParseException(cnfe.message)
        }
    }
}