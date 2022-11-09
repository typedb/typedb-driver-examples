/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.vaticle.typedb.example.software.github.tests

import com.vaticle.typedb.example.software.github.state.Explorer
import com.vaticle.typedb.example.software.github.state.Migrator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test

class MigratorExplorerTest {
    @Test
    fun testUsersCollaboratedOnFile() {
        val automationEditors = Explorer().usersCollaboratedOnFile(".factory/automation.yml")
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
        assertTrue(lolskiEditedFiles.contains(".factory/automation.yml"))
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
        val automationFileEditCount = Explorer().fileEditCount(".factory/automation.yml")
        assertEquals(automationFileEditCount.size, 1)
        assertTrue(automationFileEditCount.contains("2"))
        val repositoriesFileEditCount = Explorer().fileEditCount("dependencies/vaticle/repositories.bzl")
        assertEquals(repositoriesFileEditCount.size, 1)
        assertTrue(repositoriesFileEditCount.contains("9"))
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun setupForExplorer() {
            Migrator().migrate("software/github/datasets/typedb.json")
        }
    }
}
