package com.navloka.arrowrush.data

import android.content.Context
import android.content.SharedPreferences

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("arrow_rush_prefs", Context.MODE_PRIVATE)

    var highestUnlockedLevel: Int
        get() = prefs.getInt(KEY_UNLOCKED_LEVEL, 1)
        set(value) = prefs.edit().putInt(KEY_UNLOCKED_LEVEL, value).apply()

    fun getLevelStars(levelNum: Int): Int {
        return prefs.getInt(KEY_LEVEL_STARS_PREFIX + levelNum, 0)
    }

    fun setLevelStars(levelNum: Int, stars: Int) {
        val current = getLevelStars(levelNum)
        if (stars > current) {
            prefs.edit().putInt(KEY_LEVEL_STARS_PREFIX + levelNum, stars).apply()
        }
    }

    fun getBestMoves(levelNum: Int): Int {
        return prefs.getInt(KEY_LEVEL_MOVES_PREFIX + levelNum, 0)
    }

    fun setBestMoves(levelNum: Int, moves: Int) {
        val current = getBestMoves(levelNum)
        if (current == 0 || moves < current) {
            prefs.edit().putInt(KEY_LEVEL_MOVES_PREFIX + levelNum, moves).apply()
        }
    }

    var rushHighScore: Int
        get() = prefs.getInt(KEY_RUSH_HIGH_SCORE, 0)
        set(value) = prefs.edit().putInt(KEY_RUSH_HIGH_SCORE, value).apply()

    var rushMaxCombo: Int
        get() = prefs.getInt(KEY_RUSH_MAX_COMBO, 0)
        set(value) = prefs.edit().putInt(KEY_RUSH_MAX_COMBO, value).apply()

    var rushTotalArrowsCleared: Int
        get() = prefs.getInt(KEY_RUSH_ARROWS_CLEARED, 0)
        set(value) = prefs.edit().putInt(KEY_RUSH_ARROWS_CLEARED, value).apply()

    var currentTheme: String
        get() = prefs.getString(KEY_CURRENT_THEME, "ZEN") ?: "ZEN"
        set(value) = prefs.edit().putString(KEY_CURRENT_THEME, value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var hapticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS_ENABLED, value).apply()

    var hasSeenTutorial: Boolean
        get() = prefs.getBoolean(KEY_HAS_SEEN_TUTORIAL, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_SEEN_TUTORIAL, value).apply()

    companion object {
        private const val KEY_UNLOCKED_LEVEL = "unlocked_level"
        private const val KEY_LEVEL_STARS_PREFIX = "level_stars_"
        private const val KEY_LEVEL_MOVES_PREFIX = "level_moves_"
        private const val KEY_RUSH_HIGH_SCORE = "rush_high_score"
        private const val KEY_RUSH_MAX_COMBO = "rush_max_combo"
        private const val KEY_RUSH_ARROWS_CLEARED = "rush_arrows_cleared"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_HAPTICS_ENABLED = "haptics_enabled"
        private const val KEY_CURRENT_THEME = "current_theme"
        private const val KEY_HAS_SEEN_TUTORIAL = "has_seen_tutorial"
    }
}
