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
    private val client = TypeDB.coreClient(DB_URI)
    private val session = client.session(DB_KEYSPACE, TypeDBSession.Type.DATA)
    private val transaction = session.transaction(TypeDBTransaction.Type.READ)

    fun usersCollaboratedOnFile(fileName: String): ArrayList<String> {
        val results = ArrayList<String>()
        val queryString =
            "match \$file isa file, has file_name \"$fileName\";" +
            "\$commit_file(file: \$file, commit: \$commit) isa commit_file;" +
            "\$commit_author(commit: \$commit, author: \$author) isa commit_author;" +
            "\$author has user_name \$x;"
            "get \$x;"
        var query = TypeQL.parseQuery<TypeQLQuery>(queryString)
        this.transaction.query().match(query.asMatch()).forEach { result ->
            results.add(
                result.get("user_name").asAttribute().toString()
            )
        }
        return results
    }

    fun filesEditedByUser(userName: String): ArrayList<String> {
        val results = ArrayList<String>()
        val queryString =
            "match \$user isa user, has user_name \"$userName;\"" +
            "\$commit_author(commit: \$commit, author: \$user) isa commit_author;" +
            "\$commit_file(file: \$file, commit: \$commit) isa commit_file;" +
            "\$file has file_name \$x;" +
            "get \$x;"
        var query = TypeQL.parseQuery<TypeQLQuery>(queryString)
        this.transaction.query().match(query.asMatch()).forEach { result ->
            results.add(
                result.get("file_name").asAttribute().toString()
            )
        }
        return results
    }

    fun usersWorkedOnRepo(repoName: String): ArrayList<String> {
        val results = ArrayList<String>()
        val queryString =
            "match \$repo isa repo, has repo_name \"$repoName\";" +
            "\$commit_repo(commit: \$commit, repo: \$repo) isa commit_repo;" +
            "\$commit_author(commit: \$commit, author: \$author) isa commit_author;" +
            "\$author has user_name \$x;" +
            "get \$x;"
        var query = TypeQL.parseQuery<TypeQLQuery>(queryString)
        this.transaction.query().match(query.asMatch()).forEach { result ->
            results.add(
                result.get("user_name").asAttribute().toString()
            )
        }
        return results
    }

    fun reposWorkedOnByUser(userName: String): ArrayList<String> {
        val results = ArrayList<String>()
        val queryString =
            "match \$user isa user, has user_name \"$userName\";" +
            "\$commit_author(commit: \$commit, author: \$user) isa commit_author;" +
            "\$commit_repo(commit: \$commit, repo: \$repo) isa commit_repo;" +
            "\$repo has repo_name \$x;" +
            "get \$x;"
        var query = TypeQL.parseQuery<TypeQLQuery>(queryString)
        this.transaction.query().match(query.asMatch()).forEach { result ->
            results.add(
                result.get("repo_name").asAttribute().toString()
            )
        }
        return results
    }

    fun commitFilesAlsoWorkedOnByUsers(commitHash: String): ArrayList<String> {
        val results = ArrayList<String>()
        val queryString =
            "match \$commit isa commit, has commit_hash \"$commitHash\";" +
            "\$commit_file(commit: \$commit, file: \$file) isa commit_file;" +
            "\$commit_file2(commit: \$commit2, file: \$file) isa commit_file;" +
            "\$commit_author(commit: \$commit2, author: \$author) isa commit_author;" +
            "\$author has user_name \$x;" +
            "get \$x;"
        var query = TypeQL.parseQuery<TypeQLQuery>(queryString)
        this.transaction.query().match(query.asMatch()).forEach { result ->
            results.add(
                result.get("user_name").asAttribute().toString()
            )
        }
        return results
    }

    fun fileEditCount(fileName: String): Int {
        val results = ArrayList<Int>()
        val queryString =
            "match \$file isa file, has file_name \"$fileName\";" +
            "\$commit_file(commit: \$commit, file: \$file) isa commit_file;" +
            "\$commit_file2(commit: \$commit2, file: \$file) isa commit_file;" +
            "not {\$commit_file is \$commit_file2;};" +
            "get \$commit_file; count;"
        var query = TypeQL.parseQuery<TypeQLQuery>(queryString)
        this.transaction.query().match(query.asMatch()).forEach { result ->
            results.add(
                result.get("").asEntity().toString().toInt()
            )
        }
        return results[0]
    }
}