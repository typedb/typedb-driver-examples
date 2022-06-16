package github.tests

import github.state.Migrator
import github.state.Explorer

import org.junit.Test
import org.junit.Assert.assertEquals

class Test {
    @Test
    fun testExplorer() {
        val fileEditCount = Explorer().fileEditCount(".grabl/automation.yml")
        assertEquals(fileEditCount, 2.toString())
    }
}