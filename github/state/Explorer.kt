package github.state

import com.vaticle.typedb.client.TypeDB
import com.vaticle.typedb.client.api.TypeDBOptions
import com.vaticle.typedb.client.api.TypeDBSession.Type.DATA
import com.vaticle.typedb.client.api.TypeDBTransaction
import com.vaticle.typedb.client.api.TypeDBTransaction.Type.READ
import com.vaticle.typeql.lang.TypeQL
import com.vaticle.typeql.lang.query.TypeQLMatch


/**
 * Once we're using an explorer, we assume a number of things.
 *      - TypeDB server is running.
 *      - The schema and data are loaded in under 'github'.
 */
class Explorer {
    private fun useTransaction(sessionOptions: TypeDBOptions = TypeDBOptions.core(), fn: (TypeDBTransaction) -> Unit) {
        TypeDB.coreClient(DB_URI).use { client ->
            client.session(DB_NAME, DATA, sessionOptions).use { session ->
                session.transaction(READ).use { fn(it) }
            }
        }
    }

    fun usersCollaboratedOnFile(fileName: String): ArrayList<String> {
        val results = ArrayList<String>()
        useTransaction(TypeDBOptions.core().infer(true)) { transaction ->
            val queryString =
                "match \$file isa file, has file-name \"$fileName\";" +
                        "\$file-collaborator(file: \$file, collaborator: \$c) isa file-collaborator;" +
                        "\$c has user-name \$user-name;"
            val query = TypeQL.parseQuery<TypeQLMatch>(queryString)
            collectAttributeValues("user-name", transaction, query, results)
            results.sort()
        }
        return results
    }

    fun filesEditedByUser(userName: String): ArrayList<String> {
        val results = ArrayList<String>()
        useTransaction { transaction ->
            val queryString =
                "match \$user isa user, has user-name \"$userName\";" +
                        "\$commit-author(commit: \$commit, author: \$user) isa commit-author;" +
                        "\$commit-file(file: \$file, commit: \$commit) isa commit-file;" +
                        "\$file has file-name \$file-name;" +
                        "get \$file-name;"
            val query = TypeQL.parseQuery<TypeQLMatch>(queryString)
            collectAttributeValues("file-name", transaction, query, results)
            results.sort()
        }
        return results
    }

    fun usersWorkedOnRepo(repoName: String): ArrayList<String> {
        val results = ArrayList<String>()
        useTransaction { transaction ->
            val queryString =
                "match \$repo isa repo, has repo-name \"$repoName\";" +
                        "\$commit-repo(commit: \$commit, repo: \$repo) isa commit-repo;" +
                        "\$commit-author(commit: \$commit, author: \$author) isa commit-author;" +
                        "\$author has user-name \$user-name;" +
                        "get \$user-name;"
            val query = TypeQL.parseQuery<TypeQLMatch>(queryString)
            collectAttributeValues("user-name", transaction, query, results)
            results.sort()
        }
        return results
    }

    fun commitFilesAlsoWorkedOnByUsers(commitHash: String): ArrayList<String> {
        val results = ArrayList<String>()
        useTransaction { transaction ->
            val queryString =
                "match \$commit isa commit, has commit-hash \"$commitHash\";" +
                        "\$commit-file(commit: \$commit, file: \$file) isa commit-file;" +
                        "\$commit-file2(commit: \$commit2, file: \$file) isa commit-file;" +
                        "\$commit-author(commit: \$commit2, author: \$author) isa commit-author;" +
                        "\$author has user-name \$user-name;" +
                        "get \$user-name;"
            val query = TypeQL.parseQuery<TypeQLMatch>(queryString)
            collectAttributeValues("user-name", transaction, query, results)
            results.sort()
        }
        return results
    }

    private fun collectAttributeValues(
        attributeVarName: String,
        transaction: TypeDBTransaction,
        query: TypeQLMatch?,
        results: ArrayList<String>
    ) {
        transaction.query().match(query).forEach { result ->
            results.add(
                result.get(attributeVarName).asAttribute().value.toString()
            )
        }
    }

    fun fileEditCount(fileName: String): ArrayList<String> {
        val result = ArrayList<String>()
        useTransaction { transaction ->
            val queryString =
                "match \$file isa file, has file-name \"$fileName\";" +
                        "\$commit-file(commit: \$commit, file: \$file) isa commit-file;" +
                        "\$commit-file2(commit: \$commit2, file: \$file) isa commit-file;" +
                        "not {\$commit-file is \$commit-file2;};" +
                        "get \$commit-file; count;"
            val query = TypeQL.parseQuery<TypeQLMatch.Aggregate>(queryString)
            val count = transaction.query().match(query).get().toString()
            result.add(count)
        }
        return result
    }

//    private fun collectAttributeValues(attributeName: String)

    companion object {
        private const val DB_URI = "localhost:1729"
        private const val DB_NAME = "github"
    }
}