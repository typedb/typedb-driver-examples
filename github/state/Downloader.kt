package github.state

import org.kohsuke.github.*
import java.io.IOError

class Downloader {
    /**
     * repo_path is a string like "vaticle/typedb-examples"
     * We only support GitHub repository paths.
     */
    fun explore(repo_path: String) {
        if (repo_path.split("/").size != 2) {
            throw IOError(Throwable("Repos should be formatted <repo_owner>/<repo_name>. Remove any other details."))
        }
        val repoName = repo_path.split("/")[1]

        if (java.io.File("/Users/jameswilliams/Projects/typedb-examples/github/datasets/$repoName.json").exists()) {
            Migrator().run("/Users/jameswilliams/Projects/typedb-examples/github/datasets/$repoName.json")
        } else {
            val gh = GitHub.connect()
            println("Connected to GitHub.")
            try {
                val repo = buildRepo(gh.getRepository(repo_path))
                println("Fetched repository.")
                val repoName = repo.repoInfo.name
                val fileBuffer = java.io.File("/Users/jameswilliams/Projects/typedb-examples/github/datasets/$repoName.json").bufferedWriter()
                repo.toJson().writeTo(fileBuffer)
                fileBuffer.flush()
                println("Written repository to disk at datasets/$repoName.json")
                Migrator().run("/Users/jameswilliams/Projects/typedb-examples/github/datasets/$repoName.json")
            } catch (e: Exception) {
                throw IOError(Throwable(e.toString()))
            }
        }
    }

    private fun buildRepo(repo: GHRepository): RepoFile {
        val repoInfo = RepoInfo(repo.id, repo.name, repo.description.orEmpty())
        val commits = ArrayList<Commit>()
        val fileSet = HashSet<File>()
        val userSet = HashSet<User>()
        val commitIter = repo.listCommits()
        for (commit in commitIter) {
            userSet.add(User(commit.author.login.orEmpty()))

            val commitFiles = commit.files
            // Each commit needs the names of the files it modifies. We maintain this alongside the repo-level file set.
            val commitFileList = ArrayList<File>()
            for (ghFile in commitFiles) {
                val file = File(ghFile.fileName)
                commitFileList.add(file)
                fileSet.add(file)
            }
            println("Got new commit.")
            commits.add(Commit(commit.author.login, commit.shA1, commit.commitDate.toString(), commitFileList))
        }
        val fileList = ArrayList<File>()
        fileList.addAll(fileSet)
        return RepoFile(repoInfo, userSet, commits, fileSet)
    }
}
