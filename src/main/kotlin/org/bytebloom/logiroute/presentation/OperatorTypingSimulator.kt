package org.bytebloom.logiroute.presentation

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Simulates an operator typing in the search box:
 *  1. types "TRK-001" quickly (5 keystrokes within 200 ms),
 *  2. pauses long enough for that search to start,
 *  3. pastes "VAN-010" while the first search is still running,
 *  4. stays idle until the last result arrives.
 */
fun simulatedOperatorTyping(): Flow<String> = flow {
    RAPID_KEYSTROKES.forEachIndexed { index, text ->
        if (index > 0) delay(KEYSTROKE_GAP)
        emit(text)
    }
    delay(PAUSE_WHILE_FIRST_SEARCH_RUNS)
    emit(PASTED_QUERY)
    delay(IDLE_UNTIL_LAST_RESULT)
}

private val RAPID_KEYSTROKES = listOf("T", "TR", "TRK", "TRK-", "TRK-001")
private const val PASTED_QUERY = "VAN-010"
private val KEYSTROKE_GAP = 40.milliseconds
private val PAUSE_WHILE_FIRST_SEARCH_RUNS = 350.milliseconds
private val IDLE_UNTIL_LAST_RESULT = 1.seconds