package com.navloka.arrowrush.core.scoring

/** Campaign mistakes are meaningful without adding a timer to the puzzle. */
object FocusRules {
    const val MAX_FOCUS = 3

    fun afterBlockedMove(currentFocus: Int): Int =
        (currentFocus - 1).coerceAtLeast(0)

    fun isDepleted(focus: Int): Boolean = focus <= 0
}
