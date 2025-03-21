package com.example.w2051599_dicegame_anupa.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.w2051599_dicegame_anupa.R
import com.example.w2051599_dicegame_anupa.strategies.ComputerPlayStrategy
import kotlin.random.Random

/**
 * Activity representing the main game play screen.
 *
 * It sets the content view to the DiceGameApp composable.
 */
class GamePlayScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceGameApp()
        }
    }
}

/**
 * Main composable for the Dice Game App.
 *
 * This composable initializes and manages game state, handles user interactions,
 * displays dice images, the scoreboard, and game buttons.
 */
@Composable
fun DiceGameApp() {
    val context = LocalContext.current

    // Retrieve the custom win point from the Intent (default = 101)
    val winPoint = rememberSaveable {
        val intent = (context as ComponentActivity).intent
        intent.getIntExtra("winPoint", 101)
    }

    // Create an instance of our computer strategy
    val computerStrategy = remember { ComputerPlayStrategy() }

    // Persisted States via rememberSaveable

    // Dice state for player and computer
    var playerDice by rememberSaveable { mutableStateOf(List(5) { 1 }) }
    var computerDice by rememberSaveable { mutableStateOf(List(5) { 1 }) }

    // Which dice the player keeps (true means keep)
    var keptDice by rememberSaveable { mutableStateOf(List(5) { false }) }

    // Number of rolls in the current turn
    var rollCount by rememberSaveable { mutableIntStateOf(0) }         // player's roll count
    var computerRollCount by rememberSaveable { mutableIntStateOf(0) } // computer's roll count

    // Scores for player and computer
    var playerScore by rememberSaveable { mutableIntStateOf(0) }
    var computerScore by rememberSaveable { mutableIntStateOf(0) }

    // Round wins
    var playerWins by rememberSaveable { mutableIntStateOf(0) }
    var computerWins by rememberSaveable { mutableIntStateOf(0) }

    // Game over state and winner flag
    var gameOver by rememberSaveable { mutableStateOf(false) }
    var winner by rememberSaveable { mutableStateOf("") }

    // Button states for enabling/disabling actions
    var isThrowButtonEnabled by rememberSaveable { mutableStateOf(true) }
    var isReThrowButtonEnabled by rememberSaveable { mutableStateOf(false) }
    var isScoreButtonEnabled by rememberSaveable { mutableStateOf(false) }

    // Flag to indicate whether dice should be shown (after the first throw)
    var showDice by rememberSaveable { mutableStateOf(false) }

    // ----------- Functions -----------

    /**
     * Rolls the player's dice.
     *
     * Only dice that are not marked as kept are re-rolled.
     * Increments the player's roll count.
     */
    fun rollPlayerDice() {
        playerDice = playerDice.mapIndexed { index, oldValue ->
            if (keptDice[index]) oldValue else Random.nextInt(1, 7)
        }
        rollCount++
    }

    /**
     * Performs the initial dice roll for the computer.
     */
    fun firstRollComputerDice() {
        computerDice = List(5) { Random.nextInt(1, 7) }
        computerRollCount = 1
    }

    /**
     * Re-rolls the computer's dice using a computer strategy.
     *
     * Uses the ComputerPlayStrategy to determine which dice to re-roll based on
     * current dice, scores, win point, and roll count.
     */
    fun reRollComputerDiceOptimized() {
        val result = computerStrategy.reRollComputerDiceOptimized(
            currentDice = computerDice,
            computerScore = computerScore,
            playerScore = playerScore,
            winPoint = winPoint,
            rollCount = computerRollCount
        )
        computerDice = result.updatedDice
        computerRollCount = result.updatedRollCount
    }

    /**
     * Updates the scores by comparing the sums of dice values.
     *
     * In a tie scenario, additional rolls are performed until the tie is broken.
     * Updates win counts and total scores, then resets the state for the next turn.
     * Checks if the game is over based on the win point.
     */
    fun updateScores() {
        val pSum = playerDice.sum()
        val cSum = computerDice.sum()

        if (pSum > cSum) {
            // Normal case: Player wins the round
            playerWins++
            playerScore += pSum
            computerScore += cSum
        } else if (cSum > pSum) {
            // Normal case: Computer wins the round
            computerWins++
            computerScore += cSum
            playerScore += pSum
        } else {
            // -------------------------------
            // TIE: Keep rolling (no re-rolls) until tie is broken
            // -------------------------------
            var tieBroken = false
            var finalPSum = pSum
            var finalCSum = cSum

            while (!tieBroken) {
                // Roll 5 dice once for each player
                val newPlayerDice = List(5) { Random.nextInt(1, 7) }
                val newComputerDice = List(5) { Random.nextInt(1, 7) }

                val tieP = newPlayerDice.sum()
                val tieC = newComputerDice.sum()

                if (tieP != tieC) {
                    // Tie is broken here
                    tieBroken = true

                    // Update UI dice to show the final tie-break roll
                    playerDice = newPlayerDice
                    computerDice = newComputerDice

                    finalPSum = tieP
                    finalCSum = tieC
                }
            }

            // Now we have a winner from the tie-break
            if (finalPSum > finalCSum) {
                playerWins++
            } else {
                computerWins++
            }
            playerScore += finalPSum
            computerScore += finalCSum
        }

        // Reset state for the next turn
        rollCount = 0
        computerRollCount = 0
        keptDice = List(5) { false }

        // Check for game over condition
        if (playerScore >= winPoint) {
            gameOver = true
            winner = "player"
        } else if (computerScore >= winPoint) {
            gameOver = true
            winner = "computer"
        }

        // Reset button states
        isThrowButtonEnabled = true
        isReThrowButtonEnabled = false
        isScoreButtonEnabled = false
    }

    // Auto-finalize round if the player has used all 3 rolls
    if (rollCount == 3) {
        updateScores()
    }

    // Display a dialog when the game is over
    if (gameOver) {
        Dialog(onDismissRequest = {
            gameOver = false
            isThrowButtonEnabled = false
            isReThrowButtonEnabled = false
            isScoreButtonEnabled = false
        }) {
            Surface(
                modifier = Modifier
                    .width(280.dp)
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                color = if (winner == "player") Color.Green else Color.Red
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (winner == "player") "You win!" else "You lose!",
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            gameOver = false
                            isThrowButtonEnabled = false
                            isReThrowButtonEnabled = false
                            isScoreButtonEnabled = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = if (winner == "player") Color.Green else Color.Red
                        )
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }

    // ----------- Main UI -----------
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.ig_background_image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Scoreboard display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "H:$playerWins / C:$computerWins",
                    color = Color.White,
                    fontSize = 18.sp
                )
                Text(
                    text = "Player: $playerScore | Computer: $computerScore",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            // Re-throws remaining indicator
            if (showDice) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Re-throws remaining: ${3 - rollCount}",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.weight(1f))

            // Display dice for player and computer if dice should be shown
            if (showDice) {
                // Player dice UI
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Player",
                        fontSize = 24.sp,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        playerDice.forEachIndexed { index, value ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                DiceImage(value)
                                Spacer(modifier = Modifier.height(8.dp))
                                // Keep checkbox for each dice
                                Checkbox(
                                    checked = keptDice[index],
                                    onCheckedChange = {
                                        // Toggle which dice are kept by the player
                                        keptDice = keptDice.toMutableList().also { it2 ->
                                            it2[index] = it
                                        }
                                    },
                                    enabled = (rollCount >= 1 && rollCount < 3)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Computer dice UI
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Computer",
                        fontSize = 24.sp,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        computerDice.forEach { value ->
                            DiceImage(value)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.3f))

            // Buttons for game actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // "Throw" button to initiate the game round
                Button(
                    onClick = {
                        showDice = true
                        rollPlayerDice()
                        firstRollComputerDice()
                        isThrowButtonEnabled = false
                        isReThrowButtonEnabled = true
                        isScoreButtonEnabled = true
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5D3FD3),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFBDBDBD),
                        disabledContentColor = Color(0xFF424242)
                    ),
                    enabled = isThrowButtonEnabled
                ) {
                    Text("Throw")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // "Re-throw" button for additional dice rolls
                Button(
                    onClick = {
                        rollPlayerDice()
                        reRollComputerDiceOptimized()

                        if (rollCount == 3) {
                            updateScores()
                        }
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E90FF),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFBDBDBD),
                        disabledContentColor = Color(0xFF424242)
                    ),
                    enabled = isReThrowButtonEnabled
                ) {
                    Text("Re-throw")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // "Score" button to finalize the round and update scores
                Button(
                    onClick = { updateScores() },
                    modifier = Modifier
                        .width(300.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E90FF),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFBDBDBD),
                        disabledContentColor = Color(0xFF424242)
                    ),
                    enabled = isScoreButtonEnabled
                ) {
                    Text("Score")
                }
            }
        }
    }
}

/**
 * Displays the dice image corresponding to the provided value.
 *
 * The dice image is shifted upward by 10dp using an offset modifier.
 *
 * @param value the dice value (expected between 1 and 6)
 */
@Composable
fun DiceImage(value: Int) {
    val imageRes = when (value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_1
    }
    Image(
        painter = painterResource(imageRes),
        contentDescription = "Dice $value",
        modifier = Modifier
            .size(64.dp)
            .offset(y = (-10).dp) // Shifts the dice image 10dp higher
    )
}
