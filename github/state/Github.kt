package github.state

import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.JsonArray

// All the maps in this file should probably be reduce.

/**
 * This is our local representation of a repository with less information than GitHub gives us for simplification.
 */
class RepoFile(val repoInfo: RepoInfo, val users: Collection<User>, val commits: Collection<Commit>,
               val files: Collection<File>) : ToJson {

    // TypeQL parser doesn't like us having multiple insert blocks in the same query string.
    // It might be worth moving the query string logic into here.
    fun createTransaction(): ArrayList<String> {

        var insertStrings = ArrayList<String>()

        for (user in users) {
            insertStrings.add(user.toInsertString())
        }

        insertStrings.add(repoInfo.toInsertString())

        for (file in files) {
            insertStrings.add(file.toInsertString(repoInfo.name))
        }

        for (commit in commits) {
            insertStrings.add(commit.toInsertString(repoInfo.name))
            for (commitFile in commit.toCommitFiles()) {
                insertStrings.add(commitFile.toInsertString())
            }
        }

        return insertStrings
    }

    override fun toJson(): JsonObject {
        val jo = JsonObject()

        jo.add("repo", repoInfo.toJson())

        val jsonUsersArray = JsonArray()
        users.map { jsonUsersArray.add(it.toJson()) }
        jo.add("users", jsonUsersArray)

        val jsonCommitsArray = JsonArray()
        commits.map { jsonCommitsArray.add(it.toJson()) }
        jo.add("commits", jsonCommitsArray)

        val jsonFilesArray = JsonArray()
        files.map { jsonFilesArray.add(it.name) }
        jo.add("files", jsonFilesArray)

        return jo
    }

    companion object {
        fun fromJson(j: JsonObject): RepoFile {
            val repoInfo = RepoInfo.fromJson(j.get("repo").asObject())
            val users = ArrayList<User>()
            for (u in j.get("users").asArray()) {
                users.add(User.fromJson(u.asObject()))
            }

            val commits = ArrayList<Commit>()
            for (c in j.get("commits").asArray()) {
                commits.add(Commit.fromJson(c.asObject()))
            }

            val files = ArrayList<File>()
            for (f in j.get("files").asArray()) {
                files.add(File(f.asString()))
            }

            return RepoFile(repoInfo, users, commits, files)
        }
    }
}

class CommitFile(val file: String, val commitHash: String) {
    fun toInsertString(): String {
        return "match \$file isa file, has file_name \"$file\"; " +
            "\$commit isa commit, has commit_hash \"$commitHash\";" +
            "insert \$commit_file(commit: \$commit, file: \$file) isa commit_file;"
    }
}

class Commit(val author: String, val hash: String, val date: String, val files: ArrayList<File>) : ToJson {
    fun toInsertString(repoName: String): String {
        return "match \$author isa user, has user_name \"$author\";" +
                "\$repo isa repo, has repo_name \"$repoName\"; " +
                "insert \$commit isa commit, has commit_hash \"$hash\"" +
                ", has commit_date \"$date\"; " +
                "\$commit_author(commit: \$commit, author: \$author) isa commit_author; " +
                "\$commit_repo(commit: \$commit, repo: \$repo) isa commit_repo; "
    }

    fun toCommitFiles(): ArrayList<CommitFile> {
        val result = ArrayList<CommitFile>()
        for (file in files) {
            result.add(CommitFile(file.name, hash))
        }
        return result
    }

    override fun toJson(): JsonObject {
        val jo = JsonObject()

        jo.add("author", author)
        jo.add("hash", hash)
        jo.add("date", date)

        val jsonFilesArray = JsonArray()
        files.map { jsonFilesArray.add(it.name) }
        jo.add("files", jsonFilesArray)

        return jo
    }

    companion object: FromJson<Commit> {
        override fun fromJson(j: JsonObject): Commit {
            val files = ArrayList<File>()
            for (f in j.get("files").asArray()) {
                files.add(File(f.asString()))
            }
            return Commit(j.get("author").asString(), j.get("hash").asString(), j.get("date").asString(), files)
        }
    }
}

class RepoInfo(val id: Long, val name: String, val desc: String, val authorName: String) : ToJson {
    fun toInsertString(): String {
        return "match \$user isa user, has user_name \"$authorName\"; " +
                "insert \$repo isa repo, has repo_id $id" +
                ", has repo_name \"$name\"" +
                ", has repo_description \"$desc\"; " +
                "\$repo_creator(repo: \$repo, owner: \$user) isa repo_creator; "
    }

    override fun toJson(): JsonObject {
        val jo = JsonObject()

        jo.add("id", id)
        jo.add("name", name)
        jo.add("description", desc)
        jo.add("author", authorName)

        return jo
    }

    companion object: FromJson<RepoInfo> {
        override fun fromJson(j: JsonObject): RepoInfo {
            return RepoInfo(j.get("id").asLong(),
                j.get("name").asString(),
                j.get("description").asString(),
                j.get("author").asString())
        }
    }
}

class User(val name: String) : ToJson {
    fun toInsertString(): String {
        return "insert \$user isa user, has user_name \"$name\";"
    }

    override fun toJson(): JsonObject {
        val jo = JsonObject()

        jo.add("name", name)

        return jo
    }

    override fun equals(other: Any?): Boolean {
        other as User
        return this.name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    companion object: FromJson<User> {
        override fun fromJson(j: JsonObject): User {
            return User(j.get("name").asString())
        }
    }
}

class File(val name: String) : ToJson {
    fun toInsertString(repoName: String): String {
        return "match \$repo isa repo, has repo_name \"$repoName\"; " +
                "insert \$file isa file, has file_name \"$name\"; " +
                "\$repo_file(repo: \$repo, file: \$file) isa repo_file; "
    }

    override fun toJson(): JsonObject {
        val jo = JsonObject()

        jo.add("name", name)

        return jo
    }

    override fun equals(other: Any?): Boolean {
        other as File
        return this.name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    companion object: FromJson<File> {
        override fun fromJson(j: JsonObject): File {
            return File(j.get("name").asString())
        }
    }
}