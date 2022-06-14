package github.state

import com.vaticle.typedb.client.TypeDB
import com.vaticle.typedb.client.api.TypeDBSession
import com.vaticle.typedb.client.api.TypeDBTransaction
import com.vaticle.typeql.lang.TypeQL.parseQuery
import com.vaticle.typeql.lang.query.TypeQLDefine
import com.vaticle.typeql.lang.query.TypeQLInsert
import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject

import java.io.*
import java.io.File
//import java.io.BufferedReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import github.state.RepoFile.Companion as RepoFile

class Migrator {

    private val DB_NAME = "github"
    private val DB_URI = "localhost:1729"
    private val SCHEMA_PATH_STRING = "schemas/github-schema.tql"

    fun run(data_path: String) {
        val migrator = Migrator()
        migrator.clearDatabase()
        migrator.connectAndWriteSchema(SCHEMA_PATH_STRING)
        migrator.connectAndMigrate(initialiseInputs(data_path))
    }

    private fun clearDatabase() {
        val client = TypeDB.coreClient(DB_URI)
        client.databases().get(DB_NAME).delete()
        println("Deleted $DB_NAME.")
        client.close()
    }

    private fun connectAndWriteSchema(path: String) {
        val client = TypeDB.coreClient(DB_URI)
        client.databases().create(DB_NAME)
        val session = client.session(DB_NAME, TypeDBSession.Type.SCHEMA)
        val transaction = session.transaction(TypeDBTransaction.Type.WRITE)

        try {
            val bytes = Files.readAllBytes(Paths.get(path))
            val schemaString = String(bytes, StandardCharsets.UTF_8)
            transaction.query().define(parseQuery(schemaString) as TypeQLDefine)
            transaction.commit()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            println("Wrote $path to $DB_NAME.")
            session.close()
        }
        client.close()
    }

    private fun connectAndMigrate(insertables: Collection<Migratable>) {
        val client = TypeDB.coreClient(DB_URI)
        val session = client.session(DB_NAME, TypeDBSession.Type.DATA)

        loadDataIntoTypeDB(insertables, session)

        session.close()
        client.close()
    }

    private fun initialiseInputs(data_path: String) : Collection<Migratable> {
        val inputs = ArrayList<Migratable>()
        inputs.add(pathToRepository(data_path))
        return inputs
    }

    private fun loadDataIntoTypeDB(insertables: Collection<Migratable>, session: TypeDBSession) {
        val transaction = session.transaction(TypeDBTransaction.Type.WRITE)
        for (insert in insertables) {
            val insertQuery = insert.toInsertString()
            println("Executing query: $insertQuery")
            transaction.query().insert(parseQuery(insertQuery) as TypeQLInsert)
        }
        transaction.commit()
    }

    private fun pathToRepository(data_path: String) : Migratable {
        // Awful!
        return RepoFile.fromJson(getJson(data_path)) as Migratable
    }

    @Throws(FileNotFoundException::class)
    fun getJson(relativePath: String): JsonObject {
        return Json.parse(File(relativePath).bufferedReader()).asObject()
    }
}