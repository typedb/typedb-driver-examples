package github.state

import com.eclipsesource.json.JsonObject

/**
 * Interface for turning an object into a TypeDB insert query.
 * This should also include any relations that can be derived from the object.
 */
interface Insertable {
    fun toInsertString(): String
}

interface ToJson {
    fun toJson(): JsonObject
}

interface FromJson<T> {
    fun fromJson(j: JsonObject): T
}

abstract class Migratable : Insertable, ToJson, FromJson<Migratable>