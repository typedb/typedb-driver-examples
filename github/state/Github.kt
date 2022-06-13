package github.state

class Commit(val hash: String, val date: String, val files: ArrayList<File>) : Insertable {
    override fun toInsertString(): String {
        TODO("Not yet implemented")
    }
}

class Repo(val id: String, val name: String, val desc: String) : Insertable {
    override fun toInsertString(): String {
        TODO("Not yet implemented")
    }
}

class User(val name: String) : Insertable {
    override fun toInsertString(): String {
        TODO("Not yet implemented")
    }
}

class File(val name: String) : Insertable {
    override fun toInsertString(): String {
        TODO("Not yet implemented")
    }
}