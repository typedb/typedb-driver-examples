import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import common.Settings
import window.FlightWindowState

@Composable
fun rememberApplicationState() = remember {
    FlightApplicationState().apply {
        newWindow()
    }
}

class FlightApplicationState {
    val settings = Settings()
    val tray = TrayState()
    val dbConn = TypeDB

    private val _windows = mutableStateListOf<FlightWindowState>()
    val windows: List<FlightWindowState> get() = _windows

    fun newWindow() {
        _windows.add(
            FlightWindowState(
                application = this,
                path = null,
                exit = _windows::remove
            )
        )
    }

    fun sendNotification(notification: Notification) {
        tray.sendNotification(notification)
    }

    suspend fun exit() {
        val windowsCopy = windows.reversed()
        for (window in windowsCopy) {
            if (!window.exit()) {
                break
            }
        }
    }
}