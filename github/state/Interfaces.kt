package github.state

import com.eclipsesource.json.JsonObject

interface ToJson {
    fun toJson(): JsonObject
}

interface FromJson<T> {
    fun fromJson(j: JsonObject): T
}