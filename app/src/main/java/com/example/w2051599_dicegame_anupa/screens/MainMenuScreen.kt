package com.example.w2051599_dicegame_anupa.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.w2051599_dicegame_anupa.R

@Composable
fun MainMenuScreen() {
    val context = LocalContext.current
    var openDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.background_image3),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ComposeColor.Transparent,
                            ComposeColor.Black.copy(alpha = 0.3f)
                        )
                    )
                )
        )

        Text(
            text = "Welcome to the Dice Game!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ComposeColor.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp)
                .shadow(2.dp)
        )

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
                Button(
                    onClick = {
                        val intent = Intent(context, HomeScreen::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ComposeColor(0xFF5D3FD3),
                        contentColor = ComposeColor.White
                    )
                ) {
                    Text("New Game")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { openDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ComposeColor(0xFF1E90FF),
                        contentColor = ComposeColor.White
                    )
                ) {
                    Text("About Us")
                }
            }
        }

        if (openDialog) {
            AlertDialog(
                onDismissRequest = { openDialog = false },
                title = { Text("About Us") },
                text = {
                    Text(
                        "Student ID = W2051599 \n" +
                                "Student Name = Anupa Kehelgamuwa \n \n" +
                                "I confirm that I understand what plagiarism is and have read and understood the section " +
                                "on Assessment Offences in the Essential Information for Students. The work that I have " +
                                "submitted is entirely my own. Any work from other authors is duly referenced and acknowledged."
                    )
                },
                confirmButton = {
                    TextButton(onClick = { openDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
