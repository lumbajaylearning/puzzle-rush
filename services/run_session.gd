class_name RunSession
extends RefCounted
## Score depends on accuracy; the optional combo rewards rhythm without a timer penalty.

const COMBO_WINDOW := 2.4
var moves := 0
var mistakes := 0
var combo := 0
var best_combo := 0
var score := 0
var elapsed := 0.0
var last_move := -100.0

func advance(delta: float) -> void:
	elapsed += delta
	if elapsed - last_move > COMBO_WINDOW:
		combo = 0

func correct_move() -> void:
	combo = combo + 1 if elapsed - last_move <= COMBO_WINDOW else 1
	last_move = elapsed
	best_combo = maxi(best_combo, combo)
	moves += 1
	score += 100 + mini(combo - 1, 10) * 25

func blocked_move() -> void:
	mistakes += 1
	combo = 0
	last_move = -100.0

func break_combo() -> void:
	combo = 0
	last_move = -100.0

func meter() -> float:
	return clampf(1.0 - (elapsed - last_move) / COMBO_WINDOW, 0.0, 1.0) if combo > 0 else 0.0

func stars() -> int:
	if mistakes == 0:
		return 3
	return 2 if mistakes <= maxi(1, floori(moves * 0.12)) else 1
