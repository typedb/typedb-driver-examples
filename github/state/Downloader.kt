package github.state

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.kohsuke.github.GitHub
import org.kohsuke.github.GHRepository
import java.io.IOError

class Downloader {
    enum class State {
        NOT_STARTED,
        GITHUB_DOWNLOADING,
        WRITING_TO_FILE,
        COMPLETED,
    }

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
        if (!java.io.File("$DATASETS_PATH/${repoInput}.json").exists()) {
            val gh = GitHub.connect()

            this.state = State.GITHUB_DOWNLOADING
            val repo = toRepoFile(gh.getRepository(repoPath))

            this.state = State.WRITING_TO_FILE
            val fileBuffer = java.io.File("$DATASETS_PATH/$repoInput.json").bufferedWriter()
            repo.toJson().writeTo(fileBuffer)
            fileBuffer.flush()

            this.state = State.COMPLETED
        }

        return "$DATASETS_PATH/$repoInput.json"
    }

    /**
     * Turns a GitHub repository object into our RepoFile representation.
     */
    private fun toRepoFile(repo: GHRepository): RepoFile {
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
        private const val DATASETS_PATH = "github/datasets"
    }
}
