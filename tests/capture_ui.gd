extends SceneTree
## Render actual Godot screens under a display server, with isolated user data.

var game: Control

func _initialize() -> void:
	call_deferred("_capture")

func _capture() -> void:
	root.size = Vector2i(720, 1280)
	game = load("res://scenes/game.tscn").instantiate()
	root.add_child(game)
	await _save("home")
	game.progress.unlocked = 30
	game._show_levels()
	await _save("levels")
	game._start_level(1)
	await _save("tutorial")
	game._start_level(30)
	await _save("level30")
	var solution: Dictionary = PuzzleSolver.solve(game.engine.snapshot())
	for arrow_id in solution.moves:
		game._on_arrow_tapped(arrow_id)
	await create_timer(0.65).timeout
	await _save("complete")
	game.queue_free()
	await process_frame
	quit(0)

func _save(label: String) -> void:
	for i in range(5):
		await process_frame
	await RenderingServer.frame_post_draw
	var folder := "res://build/screenshots"
	DirAccess.make_dir_recursive_absolute(folder)
	var error := root.get_texture().get_image().save_png(folder + "/" + label + ".png")
	if error != OK:
		push_error("Screenshot failed: " + label)
		quit(1)
