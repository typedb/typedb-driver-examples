package github.state

import com.vaticle.typedb.client.TypeDB
import com.vaticle.typedb.client.api.TypeDBSession
import com.vaticle.typedb.client.api.TypeDBTransaction
import com.vaticle.typeql.lang.TypeQL
import com.vaticle.typeql.lang.query.TypeQLQuery


/**
 * Once we're using an explorer, we assume a number of things.
 *      - TypeDB server is running.
 *      - The schema and data are loaded in under 'github'.
 */
class Explorer {
    private val DB_URI = "localhost:1729"
    private val DB_KEYSPACE = "github"

    fun usersCollaboratedOnFile(fileName: String): ArrayList<String> {
        val client = TypeDB.coreClient(DB_URI)
        val session = client.session(DB_KEYSPACE, TypeDBSession.Type.DATA)
        val transaction = session.transaction(TypeDBTransaction.Type.READ)
        val results = ArrayList<String>()
        val queryString =
            "match \$file isa file, has file_name \"$fileName\";" +
            "\$commit_file(file: \$file, commit: \$commit) isa commit_file;" +
            "\$commit_author(commit: \$commit, author: \$author) isa commit_author;" +
            "\$author has user_name \$user_name;" +
            "get \$user_name;"
        var query = TypeQL.parseQuery<TypeQLQuery>(queryString)
        transaction.query().match(query.asMatch()).forEach { result ->
            results.add(
                result.get("user_name").asAttribute().value.toString()
            )
        }
        transaction.close()
        results.sort()
        return results
    }

    fun filesEditedByUser(userName: String): ArrayList<String> {
        val client = TypeDB.coreClient(DB_URI)
        val session = client.session(DB_KEYSPACE, TypeDBSession.Type.DATA)
        val transaction = session.transaction(TypeDBTransaction.Type.READ)
        val results = ArrayList<String>()
        val queryString =
            "match \$user isa user, has user_name \"$userName\";" +
            "\$commit_author(commit: \$commit, author: \$user) isa commit_author;" +
            "\$commit_file(file: \$file, commit: \$commit) isa commit_file;" +
            "\$file has file_name \$file_name;" +
            "get \$file_name;"
        var query = TypeQL.parseQuery<TypeQLQuery>(queryString)
        transaction.query().match(query.asMatch()).forEach { result ->
            results.add(
                result.get("file_name").asAttribute().value.toString()
            )
        }
        transaction.close()
        results.sort()
        return results
    }

    fun usersWorkedOnRepo(repoName: String): ArrayList<String> {
        val client = TypeDB.coreClient(DB_URI)
        val session = client.session(DB_KEYSPACE, TypeDBSession.Type.DATA)
        val transaction = session.transaction(TypeDBTransaction.Type.READ)
        val results = ArrayList<String>()
        val queryString =
            "match \$repo isa repo, has repo_name \"$repoName\";" +
            "\$commit_repo(commit: \$commit, repo: \$repo) isa commit_repo;" +
            "\$commit_author(commit: \$commit, author: \$author) isa commit_author;" +
            "\$author has user_name \$user_name;" +
            "get \$user_name;"
        var query = TypeQL.parseQuery<TypeQLQuery>(queryString)
        transaction.query().match(query.asMatch()).forEach { result ->
            results.add(
                result.get("user_name").asAttribute().value.toString()
            )
        }
        transaction.close()
        results.sort()
        return results
    }

    fun commitFilesAlsoWorkedOnByUsers(commitHash: String): ArrayList<String> {
        val client = TypeDB.coreClient(DB_URI)
        val session = client.session(DB_KEYSPACE, TypeDBSession.Type.DATA)
        val transaction = session.transaction(TypeDBTransaction.Type.READ)
        val results = ArrayList<String>()
        val queryString =
            "match \$commit isa commit, has commit_hash \"$commitHash\";" +
            "\$commit_file(commit: \$commit, file: \$file) isa commit_file;" +
            "\$commit_file2(commit: \$commit2, file: \$file) isa commit_file;" +
            "\$commit_author(commit: \$commit2, author: \$author) isa commit_author;" +
            "\$author has user_name \$user_name;" +
            "get \$user_name;"
        var query = TypeQL.parseQuery<TypeQLQuery>(queryString)
        transaction.query().match(query.asMatch()).forEach { result ->
            results.add(
                result.get("user_name").asAttribute().value.toString()
            )
        }
        transaction.close()
        results.sort()
        return results
    }

    fun fileEditCount(fileName: String): String {
        val client = TypeDB.coreClient(DB_URI)
        val session = client.session(DB_KEYSPACE, TypeDBSession.Type.DATA)
        val transaction = session.transaction(TypeDBTransaction.Type.READ)
        val queryString =
            "match \$file isa file, has file_name \"$fileName\";" +
            "\$commit_file(commit: \$commit, file: \$file) isa commit_file;" +
            "\$commit_file2(commit: \$commit2, file: \$file) isa commit_file;" +
            "not {\$commit_file is \$commit_file2;};" +
            "get \$commit_file; count;"
        var query = TypeQL.parseQuery<TypeQLQuery>(queryString)
        val result = transaction.query().match(query.asMatchAggregate()).get().toString()
        transaction.close()
        return result
    }
}