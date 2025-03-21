package com.example.w2051599_dicegame_anupa.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.w2051599_dicegame_anupa.MainActivity
import com.example.w2051599_dicegame_anupa.R

/**
 * HomeScreen Activity displays the home screen for the Dice Game App.
 *
 * It sets the content to the [HomeScreenUI] composable, which provides the UI for
 * selecting default or custom win point options and navigating to the gameplay screen.
 */
class HomeScreen : ComponentActivity() {
    /**
     * Called when the activity is starting.
     *
     * Initializes the UI by setting the content view to [HomeScreenUI].
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data supplied in onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreenUI()
        }
    }
}

/**
 * Composable function that defines the Home Screen UI.
 *
 * The UI includes a background image with a gradient overlay for readability,
 * a back button for navigation to [MainActivity], a title text, and options for playing
 * the game with either the default win point or a custom win point provided via an input field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenUI() {
    val context = LocalContext.current
    // Use rememberSaveable so that the input state is preserved on rotation
    val winPointInput = rememberSaveable { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(R.drawable.background_image3),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // Gradient overlay for better readability
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))
                    )
                )
        )

        // Back button (Top-Left)
        IconButton(
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp, top = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Title centered at the top
        Text(
            text = "Main Menu",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 12.dp)
                .shadow(2.dp)
        )

        // Centered Buttons at the Bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Play Button with Default Win Point
                Button(
                    onClick = {
                        val intent = Intent(context, GamePlayScreen::class.java)
                        intent.putExtra("winPoint", 101) // Default win point
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5D3FD3),
                        contentColor = Color.White
                    )
                ) {
                    Text("Play with default win point (101)")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card for "Play With Custom Win Point" and Input Field
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, shape = RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Play With Custom Win Point",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Input field for custom win point
                        OutlinedTextField(
                            value = winPointInput.value,
                            onValueChange = { winPointInput.value = it },
                            label = { Text("Enter Win Point") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF5D3FD3),
                                unfocusedBorderColor = Color(0xFF5D3FD3).copy(alpha = 0.7f),
                                cursorColor = Color(0xFF5D3FD3)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Button to play with the custom win point
                        Button(
                            onClick = {
                                val customWinPoint = winPointInput.value.toIntOrNull() ?: 101
                                val intent = Intent(context, GamePlayScreen::class.java)
                                intent.putExtra("winPoint", customWinPoint)
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5D3FD3),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Play Now")
                        }
                    }
                }
            }
        }
    }
}
