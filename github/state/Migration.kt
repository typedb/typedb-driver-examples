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

class Migrator {

    private val DB_NAME = "github"
    private val DB_URI = "localhost:1729"
    private val SCHEMA_PATH_STRING = "/Users/jameswilliams/Projects/typedb-examples/github/schemas/github-schema.tql"

    fun run(dataPath: String) {
        this.clearDatabase()
        this.connectAndWriteSchema(SCHEMA_PATH_STRING)
        this.connectAndMigrate(initialiseInputs(dataPath))
    }

    private fun clearDatabase() {
        val client = TypeDB.coreClient(DB_URI)
        if (client.databases().contains("github")) {
            client.databases().get("github").delete()
            println("Deleted $DB_NAME.")
        }
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

    private fun connectAndMigrate(repoFiles: Collection<RepoFile>) {
        val client = TypeDB.coreClient(DB_URI)
        val session = client.session(DB_NAME, TypeDBSession.Type.DATA)

        loadDataIntoTypeDB(repoFiles, session)

        session.close()
        client.close()
    }

    private fun initialiseInputs(data_path: String) : Collection<RepoFile> {
        val inputs = ArrayList<RepoFile>()
        inputs.add(pathToRepoFile(data_path))
        return inputs
    }

    private fun loadDataIntoTypeDB(repoFiles: Collection<RepoFile>, session: TypeDBSession) {
        val transaction = session.transaction(TypeDBTransaction.Type.WRITE)
        for (repo in repoFiles) {
            for (insertString in repo.createTransaction()) {
                println("Executing query: $insertString")
                transaction.query().insert(parseQuery(insertString) as TypeQLInsert)
            }
        }
        transaction.commit()
    }

    private fun pathToRepoFile(data_path: String) : RepoFile {
        return RepoFile.fromJson(getJson(data_path))
    }

    @Throws(FileNotFoundException::class)
    fun getJson(relativePath: String): JsonObject {
        return Json.parse(File(relativePath).bufferedReader()).asObject()
    }
}