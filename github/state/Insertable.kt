package github.state

/**
 * Interface for turning an object into a TypeDB insert query.
 * This should also include any relations that can be derived from the object.
 */
interface Insertable {
    fun toInsertString(): String
}