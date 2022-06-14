package github.state

import org.kohsuke.github.*

class Downloader {
    /**
     * repo_path is a string like "vaticle/typedb-examples"
     * We only support GitHub repository paths.
     */
    fun download(repo_path: String): RepoFile {
        val gh = GitHub.connect()
        val repo = buildRepo(gh.getRepository(repo_path))
        val repoName = repo.repoInfo.name
        repo.toJson().writeTo(java.io.File("datasets/$repoName").bufferedWriter())
        return repo
    }

    private fun buildRepo(repo: GHRepository): RepoFile {
        val repoInfo = RepoInfo(repo.id, repo.name, repo.description)
        val commits = ArrayList<Commit>()
        val fileSet = HashSet<File>()
        val userSet = HashSet<User>()
        val commitIter = repo.listCommits()

        for (commit in commitIter) {
            userSet.add(User(commit.author.name))

            val commitFiles = commit.files
            // Each commit needs the names of the files it modifies. We maintain this alongside the repo-level file set.
            val commitFileList = ArrayList<File>()
            for (ghFile in commitFiles) {
                val file = File(ghFile.fileName)
                commitFileList.add(file)
                fileSet.add(file)
            }

            commits.add(Commit(commit.author.name, commit.shA1, commit.commitDate.toString(), commitFileList))
        }
        val fileList = ArrayList<File>()
        fileList.addAll(fileSet)
        return RepoFile(repoInfo, userSet, commits, fileSet)
    }
}
