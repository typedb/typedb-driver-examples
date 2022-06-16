package github.state

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.vaticle.typedb.client.TypeDB
import com.vaticle.typedb.client.api.TypeDBSession
import com.vaticle.typedb.client.api.TypeDBTransaction
import com.vaticle.typeql.lang.TypeQL.parseQuery
import com.vaticle.typeql.lang.query.TypeQLDefine
import com.vaticle.typeql.lang.query.TypeQLInsert
import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject

import java.io.File
import java.io.IOException
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class Migrator {
    enum class State {
        NOT_STARTED,
        CONNECTING,
        WRITING_SCHEMA,
        MIGRATING,
        COMPLETED,
    }

    var state by mutableStateOf(State.NOT_STARTED)

    fun migrate(dataPath: String) {
        this.state = State.CONNECTING
        this.clearDatabase()
        this.state = State.WRITING_SCHEMA
        this.connectAndWriteSchema(SCHEMA_PATH_STRING)
        this.state = State.MIGRATING
        this.connectAndMigrate(pathToRepoFile(dataPath))
        this.state = State.COMPLETED
    }

    private fun clearDatabase() {
        val client = TypeDB.coreClient(DB_URI)
        if (client.databases().contains(DB_NAME)) {
            client.databases().get(DB_NAME).delete()
        }
        client.databases().create(DB_NAME)
    }

    private fun connectAndWriteSchema(path: String) {
        val client = TypeDB.coreClient(DB_URI)
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
            session.close()
        }
        client.close()
    }

    private fun connectAndMigrate(repoFile: RepoFile) {
        val client = TypeDB.coreClient(DB_URI)
        val session = client.session(DB_NAME, TypeDBSession.Type.DATA)

        loadDataIntoTypeDB(repoFile, session)

        session.close()
        client.close()
    }

    private fun loadDataIntoTypeDB(repoFile: RepoFile, session: TypeDBSession) {
        val transaction = session.transaction(TypeDBTransaction.Type.WRITE)
        for (insertString in repoFile.createInsertStrings()) {
            transaction.query().insert(parseQuery(insertString) as TypeQLInsert)
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

    companion object {
        private var SCHEMA_PATH_STRING = "github/schemas/github-schema.tql"
        private const val DB_NAME = "github"
        private const val DB_URI = "localhost:1729"
    }
}