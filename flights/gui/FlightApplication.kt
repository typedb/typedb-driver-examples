import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.MenuScope
import androidx.compose.ui.window.Tray
import common.LocalAppResources
import kotlinx.coroutines.launch
import window.FlightWindow

@Composable
fun ApplicationScope.FlightApplication(state: FlightApplicationState) {
    if (state.settings.isTrayEnabled && state.windows.isNotEmpty()) {
        ApplicationTray(state)
    }

    for (window in state.windows) {
        key(window) {
            FlightWindow(window)
        }
    }
}

@Composable
private fun ApplicationScope.ApplicationTray(state: FlightApplicationState) {
    Tray(
        LocalAppResources.current.icon,
        state = state.tray,
        tooltip = "Flight",
        menu = { ApplicationMenu(state) }
    )
}

@Composable
private fun MenuScope.ApplicationMenu(state: FlightApplicationState) {
    val scope = rememberCoroutineScope()
    fun exit() = scope.launch { state.exit() }

    Item("New", onClick = state::newWindow)
    Separator()
    Item("Exit", onClick = { exit() })
}