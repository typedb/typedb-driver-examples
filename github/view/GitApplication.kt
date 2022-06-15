/*
 * Copyright (C) 2021 Vaticle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package github.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.DpSize
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
    var repoTextFieldValue by mutableStateOf("vaticle/typedb")
    var exploreButtonTextValue by mutableStateOf("Explore $repoTextFieldValue")
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    var outputTextFieldValue by mutableStateOf("")

    var userCollaboratedFieldValue by mutableStateOf("")
    var userCollaboratedLabel by mutableStateOf("Which users who worked on this file?")

    var filesEditedFieldValue by mutableStateOf("")
    var filesEditedLabel by mutableStateOf("Which files did this user change?")

    var usersWorkedRepoFieldValue by mutableStateOf("")
    var usersWorkedRepoLabel by mutableStateOf("Who worked on this repo?")

    var commitFilesAlsoWorkedFieldValue by mutableStateOf("")
    var commitFilesAlsoWorkedLabel by mutableStateOf("Who also worked on the files in this commit?")

    var filesEditCountFieldValue by mutableStateOf("")
    var filesEditCountLabel by mutableStateOf("How many times has this file been edited?")

//    private var error: Throwable? by mutableStateOf(null)

    @Composable
    private fun MainWindow(exitApplicationFn: () -> Unit) {
        Window(
            title = "Git Application",
            state = rememberWindowState(placement = WindowPlacement.Maximized),
            onCloseRequest = {exitApplicationFn()},

        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TextField(value = repoTextFieldValue, onValueChange = {
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
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TextField(value = userCollaboratedFieldValue, onValueChange = {
                            userCollaboratedFieldValue = it
                        })
                        Button(onClick = {
                            coroutineScope.launch {
                                val result = GlobalState.explorer.usersCollaboratedOnFile(userCollaboratedFieldValue)
                                outputTextFieldValue = result.joinToString(separator = "\n")
                            }
                        }) {
                            Text("Explore")
                        }
                        Text(userCollaboratedLabel)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TextField(value = filesEditedFieldValue, onValueChange = {
                            filesEditedFieldValue = it
                        })
                        Button(onClick = {
                            coroutineScope.launch {
                                val result = GlobalState.explorer.filesEditedByUser(filesEditedFieldValue)
                                outputTextFieldValue = result.joinToString(separator = "\n")
                            }
                        }) {
                            Text("Explore")
                        }
                        Text(filesEditedLabel)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TextField(value = usersWorkedRepoFieldValue, onValueChange = {
                            usersWorkedRepoFieldValue = it
                        })
                        Button(onClick = {
                            coroutineScope.launch {
                                val result = GlobalState.explorer.usersWorkedOnRepo(usersWorkedRepoFieldValue)
                                outputTextFieldValue = result.joinToString(separator = "\n")
                            }
                        }) {
                            Text("Explore")
                        }
                        Text(usersWorkedRepoLabel)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TextField(value = commitFilesAlsoWorkedFieldValue, onValueChange = {
                            commitFilesAlsoWorkedFieldValue = it
                        })
                        Button(onClick = {
                            coroutineScope.launch {
                                val result = GlobalState.explorer.commitFilesAlsoWorkedOnByUsers(commitFilesAlsoWorkedFieldValue)
                                outputTextFieldValue = result.joinToString(separator = "\n")
                            }
                        }) {
                            Text("Explore")
                        }
                        Text(commitFilesAlsoWorkedLabel)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TextField(value = filesEditCountFieldValue, onValueChange = {
                            filesEditCountFieldValue = it
                        })
                        Button(onClick = {
                            coroutineScope.launch {
                                val result = GlobalState.explorer.fileEditCount(filesEditCountFieldValue)
                                outputTextFieldValue = result.toString()
                            }
                        }) {
                            Text("Explore")
                        }
                        Text(filesEditCountLabel)
                    }
                }
                OutlinedTextField(outputTextFieldValue, { }, readOnly = true, modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace))
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        application(exitProcessOnExit = false) {
            MaterialTheme {
                MainWindow(::exitApplication)
            }
        }
    }

    fun repoStatusValue(): String {
        return if (GlobalState.migrator.state >= Migrator.State.CONNECTING) {
            when (GlobalState.migrator.state) {
                Migrator.State.CONNECTING -> "Connecting to TypeDB on localhost:1729..."
                Migrator.State.WRITING_SCHEMA -> "Writing the schema..."
                Migrator.State.MIGRATING -> "Migrating..."
                Migrator.State.COMPLETED -> "Loaded repository into TypeDB."
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
