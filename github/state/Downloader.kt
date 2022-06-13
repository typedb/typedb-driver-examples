package github.state

import org.kohsuke.github-api.*

class Downloader {
    fun download(repo: String) {
        var gh = GitHub.connect()
        var repo = gh.getRepository(repo)
    }

    fun getRepoInfo(repo: GitHubRepository): Repo {

    }

    fun getUserInfo(user: GitHubUser): User {

    }

    fun getCommitInfo(commit: GitHubCommit): ArrayList<Commit> {

    }
}
