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


/**
 * https://kotlinlang.org/docs/object-declarations.html#companion-objects
 * Chose to stick with this way of working, this is recommended as per above kotlin docs.
 */
abstract class Migratable: Insertable, ToJson, FromJson<Migratable>