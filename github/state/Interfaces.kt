package github.state

import com.eclipsesource.json.JsonObject

/**
 * An interface a class implements to turn itself into a JsonObject.
 */
interface ToJson {
    fun toJson(): JsonObject
}

/**
 * An interface a class implements to create itself from a JsonObject.
 */
interface FromJson<T> {
    fun fromJson(j: JsonObject): T
}