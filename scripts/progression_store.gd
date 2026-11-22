extends RefCounted
class_name ProgressionStore

const SAVE_PATH := "user://arrow_rush_progress_v2.json"
const BACKUP_PATH := "user://arrow_rush_progress_v2.bak"
const VERSION := 2

var levels: Dictionary = {}
var highest_unlocked: int = 1

func load_progress() -> void:
	var data := _read(SAVE_PATH)
	if data.is_empty():
		data = _read(BACKUP_PATH)
	levels = data.get("levels", {}) if data.get("levels", {}) is Dictionary else {}
	highest_unlocked = max(1, int(data.get("highest_unlocked", 1)))

func record_win(level_id: int, stars: int, moves: int, elapsed_seconds: float) -> bool:
	if level_id < 1 or level_id > highest_unlocked:
		return false
	var key := str(level_id)
	var previous: Dictionary = levels.get(key, {})
	levels[key] = {
		"stars": max(int(previous.get("stars", 0)), clampi(stars, 1, 3)),
		"best_moves": mini(int(previous.get("best_moves", moves)), moves),
		"best_time": minf(float(previous.get("best_time", elapsed_seconds)), elapsed_seconds),
		"completed": true
	}
	highest_unlocked = maxi(highest_unlocked, level_id + 1)
	return save_progress()

func stars_for(level_id: int) -> int:
	return int(levels.get(str(level_id), {}).get("stars", 0))

func is_unlocked(level_id: int) -> bool:
	return level_id >= 1 and level_id <= highest_unlocked

func save_progress() -> bool:
	var payload := JSON.stringify({"version": VERSION, "highest_unlocked": highest_unlocked, "levels": levels})
	if FileAccess.file_exists(SAVE_PATH):
		var previous := FileAccess.get_file_as_string(SAVE_PATH)
		if not previous.is_empty():
			var backup := FileAccess.open(BACKUP_PATH, FileAccess.WRITE)
			if backup != null:
				backup.store_string(previous)
				backup.close()
	var file := FileAccess.open(SAVE_PATH, FileAccess.WRITE)
	if file == null:
		push_error("Unable to save Arrow Rush progress: %s" % FileAccess.get_open_error())
		return false
	file.store_string(payload)
	file.flush()
	file.close()
	return true

func _read(path: String) -> Dictionary:
	if not FileAccess.file_exists(path):
		return {}
	var parsed: Variant = JSON.parse_string(FileAccess.get_file_as_string(path))
	if parsed is Dictionary and int(parsed.get("version", 0)) == VERSION:
		return parsed
	return {}
