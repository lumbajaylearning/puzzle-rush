class_name ProgressStore
extends RefCounted
## Versioned local-only save. Write temp, retain backup, then rename.

const SAVE_PATH := "user://arrow_rush_v1.cfg"
const BACKUP_PATH := "user://arrow_rush_v1.backup.cfg"
var unlocked := 1
var sound := true
var haptics := true
var reduced_motion := false
var records: Dictionary = {}
var last_error: Error = OK

func load_progress(level_count: int) -> void:
	var config := ConfigFile.new()
	var error := config.load(SAVE_PATH)
	if error != OK or config.get_value("meta", "version", 0) != 1:
		config = ConfigFile.new()
		error = config.load(BACKUP_PATH)
	if error != OK or config.get_value("meta", "version", 0) != 1:
		return
	# Unlocks are derived from contiguous valid completed records, never trusted blindly.
	records.clear()
	for number in range(1, level_count + 1):
		var raw: Variant = config.get_value("levels", str(number), {})
		if not raw is Dictionary:
			continue
		var star_value: Variant = raw.get("stars", 0)
		if not (star_value is int or star_value is float):
			continue
		if not is_finite(float(star_value)):
			continue
		var stars := clampi(int(star_value), 0, 3)
		if stars > 0:
			records[str(number)] = {"stars": stars, "score": _safe_int(raw.get("score", 0)), "combo": _safe_int(raw.get("combo", 0))}
	unlocked = 1
	while unlocked < level_count and records.has(str(unlocked)):
		unlocked += 1
	sound = config.get_value("settings", "sound", true) == true
	haptics = config.get_value("settings", "haptics", true) == true
	reduced_motion = config.get_value("settings", "reduced_motion", false) == true

func complete_level(number: int, run: RunSession, level_count: int) -> Error:
	var old: Dictionary = records.get(str(number), {})
	records[str(number)] = {"stars": maxi(int(old.get("stars", 0)), run.stars()),
		"score": maxi(int(old.get("score", 0)), run.score), "combo": maxi(int(old.get("combo", 0)), run.best_combo)}
	unlocked = mini(level_count, maxi(unlocked, number + 1))
	return save()

func total_stars() -> int:
	var total := 0
	for record: Dictionary in records.values():
		total += int(record.stars)
	return total

func save() -> Error:
	var config := ConfigFile.new()
	config.set_value("meta", "version", 1)
	config.set_value("settings", "sound", sound)
	config.set_value("settings", "haptics", haptics)
	config.set_value("settings", "reduced_motion", reduced_motion)
	for key in records:
		config.set_value("levels", key, records[key])
	var temporary := SAVE_PATH + ".tmp"
	last_error = config.save(temporary)
	if last_error != OK:
		return last_error
	# Only back up a valid primary, preserving the last good copy after corruption.
	var previous := ConfigFile.new()
	if previous.load(SAVE_PATH) == OK and previous.get_value("meta", "version", 0) == 1:
		last_error = DirAccess.copy_absolute(SAVE_PATH, BACKUP_PATH)
		if last_error != OK:
			return last_error
	last_error = DirAccess.rename_absolute(temporary, SAVE_PATH)
	return last_error

func _safe_int(value: Variant) -> int:
	if value is int or value is float:
		if is_finite(float(value)):
			return clampi(int(value), 0, 10000000)
	return 0
