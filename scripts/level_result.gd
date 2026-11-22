extends RefCounted
class_name LevelResult

# Design policy: mastery stars do not gate progression.
static func calculate_stars(mistakes: int, hints: int, elapsed_seconds: float, par_seconds: float) -> int:
	if mistakes <= 0 and hints == 0 and elapsed_seconds <= par_seconds:
		return 3
	if mistakes <= 2 and hints <= 1:
		return 2
	return 1

static func summary(level_id: int, mistakes: int, hints: int, moves: int, elapsed_seconds: float, par_seconds: float) -> Dictionary:
	return {
		"level_id": level_id,
		"stars": calculate_stars(mistakes, hints, elapsed_seconds, par_seconds),
		"moves": moves,
		"elapsed_seconds": elapsed_seconds
	}
