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

package github.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CoroutineScope
import github.state.Downloader
import github.state.Migrator
import github.state.GlobalState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object GitApplication {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var repoTextFieldValue by mutableStateOf("vaticle/typedb")
    private var exploreButtonTextValue by mutableStateOf("Explore $repoTextFieldValue")
    private var outputTextFieldValue by mutableStateOf("")

    private var smallTextFieldModifier = Modifier.height(50.dp).width(200.dp)
    private var smallButtonModifier = Modifier.height(50.dp).width(100.dp)

    @JvmStatic
    fun main(args: Array<String>) {
        application(exitProcessOnExit = false) {
            MaterialTheme {
                MainWindow(::exitApplication)
            }
        }
    }

    @Composable
    private fun MainWindow(exitApplicationFn: () -> Unit) {
        Window(
            title = "GitHub Data Explorer",
            state = rememberWindowState(placement = WindowPlacement.Maximized),
            onCloseRequest = {exitApplicationFn()},

        ) {
            Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                DownloadArea()
                Divider(color = Color.Black, modifier = Modifier.fillMaxHeight().width(1.dp))
                QueryArea()
            }
        }
    }

    @Composable
    private fun QueryFieldAndButton(description: String, explorerFn: (String) -> ArrayList<String>) {
        var textFieldValue by remember {mutableStateOf("")}
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(description)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                TextField(modifier = smallTextFieldModifier, value = textFieldValue,
                    singleLine = true, onValueChange = {
                        textFieldValue = it
                    })
                Button(modifier = smallButtonModifier, onClick = {
                    coroutineScope.launch {
                        val result = explorerFn(textFieldValue)
                        outputTextFieldValue = result.joinToString(separator = "\n")
                    }
                }) {
                    Text("Query")
                }
            }
        }
    }

    @Composable
    private fun DownloadArea() {
        Column(modifier = Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            TextField(value = repoTextFieldValue, singleLine = true, onValueChange = {
                repoTextFieldValue = it
                exploreButtonTextValue = "Explore $it"
            })
            Button(onClick = {
                coroutineScope.launch {
                    val path = GlobalState.downloader.download(repoTextFieldValue)
                    GlobalState.migrator.migrate(path)
                }
            }) {
                Text(exploreButtonTextValue)
            }
            Text(repoStatusValue())
        }
    }

    @Composable
    private fun QueryArea() {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {

            QuerySelectorArea()

            QueryOutputArea()
        }
    }

    @Composable
    private fun QuerySelectorArea() {
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            QueryFieldAndButton("Which users worked on this file?") { input ->
                GlobalState.explorer.usersCollaboratedOnFile(
                    input
                )
            }

            QueryFieldAndButton("Which files did this user change?") { input ->
                GlobalState.explorer.filesEditedByUser(
                    input
                )
            }

            QueryFieldAndButton("Who worked on this repo?") { input ->
                GlobalState.explorer.usersWorkedOnRepo(
                    input
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            QueryFieldAndButton("Who also worked on the files in this commit?") { input ->
                GlobalState.explorer.commitFilesAlsoWorkedOnByUsers(
                    input
                )
            }
            QueryFieldAndButton("How many times has this file been edited?") { input ->
                GlobalState.explorer.fileEditCount(
                    input
                )
            }
        }
    }

    @Composable
    private fun QueryOutputArea() {
        OutlinedTextField(outputTextFieldValue, { }, readOnly = true, modifier = Modifier.fillMaxSize(),
            textStyle = TextStyle(fontFamily = FontFamily.Monospace))
    }


    /**
     * This determines the value of the label underneath the repository text field.
     */
    private fun repoStatusValue(): String {
        // Downloading and migrating a repo is a complicated progress, so we have two global variables that help us
        // determine what point we're at. This limits us to only one download/migration at any one time, but this is
        // acceptable in a simple app.
        return if (GlobalState.migrator.state >= Migrator.State.CONNECTING) {
            when (GlobalState.migrator.state) {
                Migrator.State.CONNECTING -> "Connecting to TypeDB on localhost:1729..."
                Migrator.State.WRITING_SCHEMA -> "Writing the schema..."
                Migrator.State.MIGRATING -> "Migrating..."
                Migrator.State.COMPLETED -> "Loaded repository into TypeDB."
                // Given the above if, this point should never be reached but when requires exhaustive matching.
                else -> {""}
            }
        } else {
            when (GlobalState.downloader.state) {
                Downloader.State.NOT_STARTED -> "Enter a repository to begin."
                Downloader.State.GITHUB_DOWNLOADING -> {
                    if (GlobalState.downloader.githubCommitsTotal > 0)
                        "Fetching commits " +
                        "(${GlobalState.downloader.githubCommitsProgress}" +
                        "/${GlobalState.downloader.githubCommitsTotal})..."
                    else "Fetching commits..."
                }
                Downloader.State.WRITING_TO_FILE -> "Writing data to disk..."
                Downloader.State.COMPLETED -> "Downloaded repository."
            }
        }
    }
}
