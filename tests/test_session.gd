extends SceneTree
## Run only via tools/run_checks.py, which isolates user data in a temp directory.

var failures := 0

func check(value: bool, message: String) -> void:
	if not value:
		failures += 1
		push_error(message)

func _initialize() -> void:
	var run := RunSession.new()
	run.correct_move()
	run.advance(1.0)
	run.correct_move()
	check(run.combo == 2 and run.score == 225, "Combo scoring")
	run.advance(2.41)
	check(run.combo == 0, "Idle combo expiry")
	check(run.stars() == 3, "Taking time never costs stars")
	run.correct_move()
	check(run.combo == 1, "Expired streak starts again")
	run.blocked_move()
	check(run.combo == 0 and run.stars() == 2, "Blocked tap breaks combo and affects stars")
	run.blocked_move()
	check(run.stars() == 1, "Repeated blocked taps")
	var progress := ProgressStore.new()
	var perfect := RunSession.new()
	perfect.correct_move()
	check(progress.complete_level(1, perfect, 30) == OK, "First save")
	check(progress.unlocked == 2, "Unlock next level")
	check(progress.complete_level(1, run, 30) == OK, "Replay save")
	check(progress.records["1"].stars == 3, "Replay cannot lower earned stars")
	var loaded := ProgressStore.new()
	loaded.load_progress(30)
	check(loaded.unlocked == 2 and loaded.total_stars() == 3, "Progress survives reload")
	loaded.sound = false
	loaded.reduced_motion = true
	check(loaded.save() == OK, "Settings save")
	var again := ProgressStore.new()
	again.load_progress(30)
	check(not again.sound and again.reduced_motion, "Settings round trip")
	var file := FileAccess.open(ProgressStore.SAVE_PATH, FileAccess.WRITE)
	file.store_string("[meta]\nversion=99\n")
	file.close()
	var recovered := ProgressStore.new()
	recovered.load_progress(30)
	check(recovered.unlocked == 2 and recovered.total_stars() == 3, "Corrupt primary recovers backup")
	check(recovered.complete_level(30, perfect, 30) == OK and recovered.unlocked == 30, "Final level stays in range")
	print("Session/progress failures: ", failures)
	quit(1 if failures else 0)
