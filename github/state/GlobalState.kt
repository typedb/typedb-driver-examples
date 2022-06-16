package github.state

object GlobalState {
    val downloader = Downloader()
    val migrator = Migrator()
    val explorer = Explorer()
}