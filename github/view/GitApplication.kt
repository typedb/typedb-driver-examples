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
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import github.state.Downloader

object GitApplication {
    var repo by mutableStateOf("")

//    private var error: Throwable? by mutableStateOf(null)

    @Composable
    private fun MainWindow(exitApplicationFn: () -> Unit) {
        Window(
            title = "Git Application",
            state = rememberWindowState(WindowPlacement.Maximized),
            onCloseRequest = {exitApplicationFn()},
        ) {
            Column(modifier = Modifier.offset(x = 100.dp, y = 100.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(value = repo, onValueChange = {repo = it})
                Button(onClick = { Downloader().explore(repo) }) {
                    Text("Explore Repo")
                }
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
}
