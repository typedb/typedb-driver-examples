package github.state

import org.kohsuke.github.*
import java.io.IOError
import java.nio.file.Paths

class Downloader {
    // Need to choose a sane multi-platform place for these to go, can't go in bazel's temporary execution environment.
    val folderPath = "/Users/jameswilliams/Projects/typedb-examples/github/datasets"
    /**
     * repo_path is a string like "vaticle/typedb-examples"
     * We only support GitHub repository paths.
     */
    fun explore(repo_path: String) {
        while (repo_path.split("/").size != 2 || repo_path.split("/")[0].isEmpty()
                || repo_path.split("/")[1].isEmpty()) {
            throw IOError(Throwable("Repos should be formatted <repo_owner>/<repo_name>."))
        }
        val repoName = repo_path.split("/")[1]

        val currentRelativePath = Paths.get("")
        val s: String = currentRelativePath.toAbsolutePath().toString()
        println("Current absolute path is: $s")

        if (java.io.File("$folderPath/$repoName.json").exists()) {
            Migrator().run("$folderPath/$repoName.json")
        } else {
            val gh = GitHub.connect()
            println("Connected to GitHub.")
            val repo = buildRepo(gh.getRepository(repo_path))
            println("Fetched repository.")
            val repoName = repo.repoInfo.name
            val fileBuffer = java.io.File("$folderPath/$repoName.json").bufferedWriter()
            repo.toJson().writeTo(fileBuffer)
            fileBuffer.flush()
            println("Written repository to disk at datasets/$repoName.json")
            Migrator().run("$folderPath/$repoName.json")
        }
    }

    private fun buildRepo(repo: GHRepository): RepoFile {
        val repoInfo = RepoInfo(repo.id, repo.name, repo.description.orEmpty(), repo.ownerName)
        val commits = ArrayList<Commit>()
        val fileSet = HashSet<File>()
        val userSet = HashSet<User>()
        userSet.add(User(repo.ownerName))
        val commitIter = repo.listCommits().take(25)
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
