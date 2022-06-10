import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.vaticle.typedb.client.TypeDB
import com.vaticle.typedb.client.api.TypeDBSession
import com.vaticle.typedb.client.api.TypeDBTransaction
import com.vaticle.typeql.lang.TypeQL.parseQuery
import com.vaticle.typeql.lang.query.TypeQLDefine
import com.vaticle.typeql.lang.query.TypeQLInsert
import mjson.Json
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class FlightMigration {

    private val DB_NAME = "flights"
    private val DB_URI = "localhost:1729"
    private val SCHEMA_PATH_STRING = "schemas/flight-schema.tql"

    abstract class Input(val path: String) {

        fun getDataPath(): String {
            return path
        }

        abstract fun template(data: Json): String
    }

    fun run() {
        val fm = FlightMigration()
        fm.clearDatabase()
        fm.connectAndWriteSchema(SCHEMA_PATH_STRING)
        fm.connectAndMigrate(fm.initialiseInputs())
    }

    private fun clearDatabase() {
        val client = TypeDB.coreClient(DB_URI)
        client.databases().get(DB_NAME).delete()
        println("Deleted flights.")
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

    private fun connectAndMigrate(inputs: Collection<Input>) {
        val client = TypeDB.coreClient(DB_URI)
        val session = client.session(DB_NAME, TypeDBSession.Type.DATA)

        for (input in inputs) {
            println("Loading " + input.getDataPath() + " into TypeDB...")
            loadDataIntoTypeDB(input, session)
        }

        session.close()
        client.close()
    }

    private fun initialiseInputs() : Collection<Input> {
        val inputs = ArrayList<Input>()

        inputs.add(object: Input("datasets/airlines.json") {
            override fun template(airline: Json): String {
                var response = String()
                response += "insert \$airline isa airline, has airline_name " + airline.at("Name")
                response += ", has airline_code " + airline.at("Code")
                response += ", has airline_icao " + airline.at("ICAO")
                response += ";"
                return response
            }
        })

        inputs.add(object: Input("datasets/airports.json"){
            override fun template(airport: Json): String {
                var response = String()
                response += "insert \$airport isa airport, has airport_name " + airport.at("name")
                response += ", has airport_iata " + airport.at("iata")
                response += ", has airport_icao " + airport.at("icao")
                response += ", has country " + airport.at("country")
                response += ", has longitude " + airport.at("lon")
                response += ", has latitude " + airport.at("lat")
                response += ";"
                return response
            }
        })

        inputs.add(object : Input("datasets/routes.json") {
            override fun template(route: Json): String {
                var response = String()
                response += "match \$origin isa airport, has airport_icao "+ route.at("origin") + "; "
                response += "\$destination isa airport, has airport_icao " + route.at("destination") + "; "
                response += "insert \$route(origin: \$origin, destination: \$destination) isa route; "
                response += "\$route has route_code " + route.at("code") + "; "
                response += "\$route has flight_time " + route.at("flighttime") + "; "
                return response
            }
        })

        return inputs
    }

    private fun loadDataIntoTypeDB(input: Input, session: TypeDBSession) {
        val items = parseDataToJson(input)
        val transaction = session.transaction(TypeDBTransaction.Type.WRITE)
        for (item in items) {
            val insertQuery = input.template(item)
            println("Executing query: $insertQuery")
            transaction.query().insert(parseQuery(insertQuery) as TypeQLInsert)
        }
        transaction.commit()
    }

    private fun parseDataToJson(input: Input) : ArrayList<Json> {
        val items = ArrayList<Json>()
        val jsonReader = JsonReader(getReader(input.getDataPath()))

        jsonReader.beginArray()

        while (jsonReader.hasNext()) {
            jsonReader.beginObject()
            val item = Json.`object`()
            while (jsonReader.hasNext()) {
                val key: String = jsonReader.nextName()
                when (jsonReader.peek()) {
                    JsonToken.STRING -> item.set(key, jsonReader.nextString())
                    JsonToken.NUMBER -> item.set(key, jsonReader.nextDouble())
                }
            }
            jsonReader.endObject()
            items.add(item)
        }
        jsonReader.endArray()
        return items
    }

    @Throws(FileNotFoundException::class)
    fun getReader(relativePath: String): Reader {
        return InputStreamReader(FileInputStream(relativePath))
    }
}


fun main() {
    FlightMigration().run()
}