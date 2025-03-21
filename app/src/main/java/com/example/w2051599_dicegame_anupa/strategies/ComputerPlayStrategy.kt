package com.example.w2051599_dicegame_anupa.strategies

import kotlin.random.Random

/**
 * ComputerPlayStrategy is responsible for determining the optimal dice re-roll strategy
 * for the computer player during its turn in the dice game.
 *
 * Strategy Overview:
 *
 * The computer is allowed up to 3 rolls per turn:
 * - The first roll is entirely random.
 * - For the subsequent re-rolls (second and third rolls), this strategy analyzes the
 *   computer's current dice and game state to decide which dice to keep and which to re-roll.
 *
 * The strategy uses the following key inputs:
 *   - currentDice: the current dice values (List of 5 integers, each 1..6)
 *   - computerScore: the computer's total score before this turn
 *   - playerScore: the player's total score before this turn
 *   - winPoint: the target score to win the game (typically 101 by default)
 *   - rollCount: the number of rolls already performed in this turn by the computer:
 *       * rollCount == 1 indicates this is the second roll.
 *       * rollCount == 2 indicates this is the third (final) roll.
 *
 * Detailed Strategy:
 *
 * 1. For the second roll (rollCount == 1):
 *    - If the computer's total (current score plus current dice sum) is enough to win,
 *      then keep all dice.
 *    - If the dice sum is very high (≥ 22), then keep all dice.
 *    - Otherwise, the strategy examines each die:
 *         - If the computer is far behind the player (score difference < -20), be aggressive
 *           and only keep high dice (values 5 or 6) to maximize the chance of a high sum.
 *         - If not far behind, keep dice that are moderately high (values 4 or more).
 *
 * 2. For the third roll (rollCount == 2):
 *    - If the computer's potential total (score plus dice sum) meets or exceeds the win point,
 *      then keep all dice.
 *    - If the dice sum is decent (≥ 18):
 *         - If the computer is not behind (scoreDiff >= 0) or if the points needed to win are
 *           less than or equal to the dice sum, then keep all dice.
 *         - Otherwise, only keep dice with high values (≥ 5).
 *    - If the dice sum is low, only keep dice that are at least moderately high (≥ 4).
 *
 * Justification:
 * - This heuristic strategy is designed to balance risk and reward. It is more aggressive when
 *   the computer is far behind (only keeping very high dice) and more conservative when it is
 *   ahead or in a winning position.
 * - By considering the current dice sum and comparing the potential total (computerScore + diceSum)
 *   to the winPoint, the strategy minimizes unnecessary re-rolls when the computer is in a strong
 *   position.
 *
 * Advantages:
 * - More efficient than a completely random strategy.
 * - Adapts to the game state: if the computer is behind, it takes greater risks; if it is close to
 *   winning, it plays conservatively.
 *
 * Disadvantages:
 * - The thresholds (e.g., diceSum ≥ 22, diceSum ≥ 18, and the score difference of -20) are heuristic
 *   and might need fine-tuning depending on game dynamics.
 * - It does not use advanced probability modeling (like Monte Carlo simulations), so while it is
 *   more efficient than random, it might not be optimal in every scenario.
 *
 * Usage:
 * Call [reRollComputerDiceOptimized] during the computer’s re-throw phases (second and third roll)
 * to get updated dice values and an updated roll count.
 *
 * @return [ComputerRollResult] containing the updated dice values and updated roll count.
 */

data class ComputerRollResult(
    val updatedDice: List<Int>,
    val updatedRollCount: Int
)

class ComputerPlayStrategy {

    fun reRollComputerDiceOptimized(
        currentDice: List<Int>,
        computerScore: Int,
        playerScore: Int,
        winPoint: Int,
        rollCount: Int  // <-- The parameter is named "rollCount"
    ): ComputerRollResult {
        if (rollCount >= 3) {
            return ComputerRollResult(currentDice, rollCount)
        }

        val diceSum = currentDice.sum()
        val scoreDiff = computerScore - playerScore
        val neededToWin = winPoint - computerScore
        val keep = BooleanArray(5) { false }

        when (rollCount) {
            1 -> {
                // second roll
                if (computerScore + diceSum >= winPoint) {
                    keep.fill(true)
                } else if (diceSum >= 22) {
                    keep.fill(true)
                } else {
                    for (i in currentDice.indices) {
                        val value = currentDice[i]
                        if (scoreDiff < -20) {
                            // far behind => keep only 5-6
                            if (value >= 5) keep[i] = true
                        } else {
                            // keep 4-6
                            if (value >= 4) keep[i] = true
                        }
                    }
                }
            }
            2 -> {
                // third roll
                if (computerScore + diceSum >= winPoint) {
                    keep.fill(true)
                } else if (diceSum >= 18) {
                    if (scoreDiff >= 0 || neededToWin <= diceSum) {
                        keep.fill(true)
                    } else {
                        for (i in currentDice.indices) {
                            if (currentDice[i] >= 5) keep[i] = true
                        }
                    }
                } else {
                    for (i in currentDice.indices) {
                        if (currentDice[i] >= 4) keep[i] = true
                    }
                }
            }
        }

        val newDice = currentDice.mapIndexed { index, oldVal ->
            if (keep[index]) oldVal else Random.nextInt(1, 7)
        }

        return ComputerRollResult(
            updatedDice = newDice,
            updatedRollCount = rollCount + 1
        )
    }
}
