package github.state

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.kohsuke.github.*
import java.io.IOError
import java.nio.file.Paths

class Downloader {
    enum class State {
        NOT_STARTED,
        GITHUB_DOWNLOADING,
        WRITING_TO_FILE,
        COMPLETED,
    }
    // Need to choose a sane multi-platform place for these to go, can't go in bazel's temporary execution environment.
    var githubCommitsProgress by mutableStateOf(0)
    var githubCommitsTotal by mutableStateOf(0)
    var state by mutableStateOf(State.NOT_STARTED)

    /**
     * repo_path is a string like "vaticle/typedb-examples". This class only supports downloading from GitHub.
     */
    fun download(input: String): String {
        // Did what the user enter match the shape of what we're expecting? That is, two non-empty strings separated by
        // a slash.
        var repoPath = input.lowercase()
        while (repoPath.split("/").size != 2 || repoPath.split("/")[0].isEmpty()
                || repoPath.split("/")[1].isEmpty()) {
            throw IOError(Throwable("Repos should be formatted <repo_owner>/<repo_name>."))
        }

        val repoInput = repoPath.split("/")[1]
        if (!java.io.File("$folderPath/${repoInput}.json").exists()) {
            val gh = GitHub.connect()

            this.state = State.GITHUB_DOWNLOADING
            val repo = buildRepo(gh.getRepository(repoPath))

            this.state = State.WRITING_TO_FILE
            val fileBuffer = java.io.File("$folderPath/$repoInput.json").bufferedWriter()
            repo.toJson().writeTo(fileBuffer)
            fileBuffer.flush()

            this.state = State.COMPLETED
        }

        return "$folderPath/$repoInput.json"
    }

    /**
     * Turns a GitHub repository object into our RepoFile representation.
     */
    private fun buildRepo(repo: GHRepository): RepoFile {
        val repoInfo = RepoInfo(repo.id, repo.name, repo.description.orEmpty(), repo.ownerName)
        val commits = ArrayList<Commit>()
        val fileSet = HashSet<File>()
        val userSet = HashSet<User>()
        userSet.add(User(repo.ownerName))

        githubCommitsTotal = repo.size.coerceAtMost(COMMITS_TO_DOWNLOAD)
        val pagedCommits = repo.listCommits().take(githubCommitsTotal)
        for (commit in pagedCommits) {
            userSet.add(User(commit.author.login.orEmpty()))

            val commitFiles = commit.files
            // Each commit needs the names of the files it modifies. We maintain this alongside the repo-level file set.
            val commitFileList = ArrayList<File>()
            for (ghFile in commitFiles) {
                val file = File(ghFile.fileName)
                commitFileList.add(file)
                fileSet.add(file)
            }

            commits.add(Commit(commit.author.login, commit.shA1, commit.commitDate.toString(), commitFileList))
            githubCommitsProgress++
        }

        return RepoFile(repoInfo, userSet, commits, fileSet)
    }

    companion object {
        private const val COMMITS_TO_DOWNLOAD = 25
        private const val PROJECT_DIR_NAME = "github"
        private val folderPath = Paths.get("").toAbsolutePath().toString() + "/$PROJECT_DIR_NAME"
    }
}
