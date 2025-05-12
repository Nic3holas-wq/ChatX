package com.example.chatx.view
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.chatx.viewmodel.SettingsViewModel

@Composable
fun DarkModeSwitch(viewModel: SettingsViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val context = LocalContext.current

    fun restartApp(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent?.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }
    Row(
        modifier = Modifier
            .padding(start = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = if (isDarkMode) "Dark Mode" else "Light Mode",
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = isDarkMode,
            onCheckedChange = {
                viewModel.toggleDarkMode(it)
                Toast.makeText(context, "${if (isDarkMode) "LightMode Activated" else "DarkMode Activated"}", Toast.LENGTH_LONG).show()
                restartApp(context)
            }
        )
    }



}