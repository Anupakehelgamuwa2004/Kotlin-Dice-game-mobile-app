package com.example.w2051599_dicegame_anupa

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.w2051599_dicegame_anupa.screens.MainMenuScreen

/**
 * MainActivity is the entry point of the Dice Game App.
 *
 * It configures the window to allow drawing behind system bars, sets the status bar as transparent,
 * and displays the MainMenuScreen composable within the MaterialTheme.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting.
     *
     * Configures the window to draw behind system bars, sets up the appearance of system bars,
     * and initializes the UI with the MainMenuScreen composable.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Allow drawing behind system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Set the status bar to transparent
        window.statusBarColor = Color.TRANSPARENT
        // Set the appearance for system bars (false = dark icons, light background)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        // Set the main content of the activity using Jetpack Compose
        setContent {
            MaterialTheme {
                // Display the main menu screen
                MainMenuScreen()
            }
        }
    }
}
