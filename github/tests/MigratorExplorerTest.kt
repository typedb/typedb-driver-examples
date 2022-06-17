package github.tests

import github.state.Explorer
import github.state.Migrator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.BeforeClass



class MigratorExplorerTest {
    @Test
    fun testUsersCollaboratedOnFile() {
        val automationEditors = Explorer().usersCollaboratedOnFile(".grabl/automation.yml")
        assertEquals(automationEditors.size, 2)
        assertTrue(automationEditors.contains("jmsfltchr"))
        assertTrue(automationEditors.contains("lolski"))

        val repositoriesEditors = Explorer().usersCollaboratedOnFile("dependencies/vaticle/repositories.bzl")
        assertEquals(repositoriesEditors.size, 4)
        assertTrue(repositoriesEditors.contains("jmsfltchr"))
        assertTrue(repositoriesEditors.contains("lolski"))
        assertTrue(repositoriesEditors.contains("flyingsilverfin"))
        assertTrue(repositoriesEditors.contains("haikalpribadi"))
    }

    @Test
    fun testFilesEditByUser() {
        val lolskiEditedFiles = Explorer().filesEditedByUser("lolski")
        assertEquals(lolskiEditedFiles.size, 3)
        assertTrue(lolskiEditedFiles.contains(".grabl/automation.yml"))
        assertTrue(lolskiEditedFiles.contains("database/RocksConfiguration.java"))
        assertTrue(lolskiEditedFiles.contains("dependencies/vaticle/repositories.bzl"))
    }

    @Test
    fun testUsersWorkedOnRepo() {
        val usersWorkedOnRepo = Explorer().usersWorkedOnRepo("typedb")
        assertEquals(usersWorkedOnRepo.size, 5)
        assertTrue(usersWorkedOnRepo.contains("dmitrii-ubskii"))
        assertTrue(usersWorkedOnRepo.contains("flyingsilverfin"))
        assertTrue(usersWorkedOnRepo.contains("haikalpribadi"))
        assertTrue(usersWorkedOnRepo.contains("jmsfltchr"))
        assertTrue(usersWorkedOnRepo.contains("lolski"))
    }

    @Test
    fun testCommitFilesAlsoWorkedOnByUsers() {
        val commitFilesUsers = Explorer().commitFilesAlsoWorkedOnByUsers("76fe2f37018d17151402e00c4d12bf10c7a61950")
        assertEquals(commitFilesUsers.size, 4)
        assertTrue(commitFilesUsers.contains("flyingsilverfin"))
        assertTrue(commitFilesUsers.contains("haikalpribadi"))
        assertTrue(commitFilesUsers.contains("jmsfltchr"))
        assertTrue(commitFilesUsers.contains("lolski"))
    }

    @Test
    fun testFileEditCount() {
        val automationFileEditCount = Explorer().fileEditCount(".grabl/automation.yml")
        assertEquals(automationFileEditCount, 2.toString())
        val repositoriesFileEditCount = Explorer().fileEditCount("dependencies/vaticle/repositories.bzl")
        assertEquals(repositoriesFileEditCount, 9.toString())
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun setupForExplorer() {
            Migrator().migrate("github/datasets/typedb.json")
        }
    }
}