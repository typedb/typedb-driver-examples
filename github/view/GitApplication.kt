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

//import androidx.compose.foundation.layout.
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
    private var userCollaboratedFieldValue by mutableStateOf("")
    private var filesEditedFieldValue by mutableStateOf("")
    private var usersWorkedRepoFieldValue by mutableStateOf("")
    private var commitFilesAlsoWorkedFieldValue by mutableStateOf("")
    private var filesEditCountFieldValue by mutableStateOf("")

    private var smallTextFieldModifier = Modifier.height(50.dp).width(200.dp)
    private var smallButtonModifier = Modifier.height(50.dp).width(100.dp)

    @JvmStatic
    fun main(args: Array<String>) {
        application(exitProcessOnExit = false) {
            MaterialTheme {
                mainWindow(::exitApplication)
            }
        }
    }

    @Composable
    private fun mainWindow(exitApplicationFn: () -> Unit) {
        Window(
            title = "Git Application",
            state = rememberWindowState(placement = WindowPlacement.Maximized),
            onCloseRequest = {exitApplicationFn()},

        ) {
            Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
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
                Divider(color = Color.Black, modifier = Modifier.fillMaxHeight().width(1.dp))
                Column(verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        queryFieldAndButton("Which users worked on this file?") { input ->
                            GlobalState.explorer.usersCollaboratedOnFile(
                                input
                            )
                        }

                        queryFieldAndButton("Which files did this user change?") { input ->
                            GlobalState.explorer.filesEditedByUser(
                                input
                            )
                        }

                        queryFieldAndButton("Who worked on this repo?") { input ->
                            GlobalState.explorer.usersWorkedOnRepo(
                                input
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        queryFieldAndButton("Who also worked on the files in this commit?") { input ->
                            GlobalState.explorer.commitFilesAlsoWorkedOnByUsers(
                                input
                            )
                        }
                        queryFieldAndButton("How many times has this file been edited?") { input ->
                            GlobalState.explorer.fileEditCount(
                                input
                            )
                        }
                    }
                    OutlinedTextField(outputTextFieldValue, { }, readOnly = true, modifier = Modifier.fillMaxSize(),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace))
                }
            }
        }
    }

    @Composable
    private fun queryFieldAndButton(description: String, explorerFn: (String) -> ArrayList<String>) {
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
