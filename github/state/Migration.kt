package github.state

import com.vaticle.typedb.client.TypeDB
import com.vaticle.typedb.client.api.TypeDBSession
import com.vaticle.typedb.client.api.TypeDBTransaction
import com.vaticle.typeql.lang.TypeQL.parseQuery
import com.vaticle.typeql.lang.query.TypeQLDefine
import com.vaticle.typeql.lang.query.TypeQLInsert
import com.eclipsesource.json.Json

import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class Migration {

    private val DB_NAME = "github"
    private val DB_URI = "localhost:1729"
    private val SCHEMA_PATH_STRING = "schemas/github-schema.tql"

    fun run(data_path: String) {
        val fm = GithubMigration()
        fm.clearDatabase()
        fm.connectAndWriteSchema(SCHEMA_PATH_STRING)
        fm.connectAndMigrate(getJson(data_path))
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

    private fun connectAndMigrate(inputs: Collection<Insertable>) {
        val client = TypeDB.coreClient(DB_URI)
        val session = client.session(DB_NAME, TypeDBSession.Type.DATA)

        for (input in inputs) {
            loadDataIntoTypeDB(input, session)
        }

        session.close()
        client.close()
    }

    private fun initialiseInputs() : Collection<Insertable> {
        val inputs = ArrayList<Insertable>()

        return inputs
    }

    private fun loadDataIntoTypeDB(input: Insertable, session: TypeDBSession) {
        val items = parseDataToJson(input)
        val transaction = session.transaction(TypeDBTransaction.Type.WRITE)
        for (item in items) {
            val insertQuery = input.template(item)
            println("Executing query: $insertQuery")
            transaction.query().insert(parseQuery(insertQuery) as TypeQLInsert)
        }
        transaction.commit()
    }

    private fun pathToInsertables(data_path: String) : ArrayList<Insertable> {
        val insertables = ArrayList<Insertable>()
        var json = getJson(data_path)
        json.get()
    }

    @Throws(FileNotFoundException::class)
    fun getJson(relativePath: String): Json {
        return Json.parse(relativePath.readText()).asObject()
    }
}