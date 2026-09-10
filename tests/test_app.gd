extends SceneTree
## Exercises actual scene + pointer routing + campaign flow, not screenshot quality.

var game: Control

func _initialize() -> void:
	call_deferred("_exercise")

func _exercise() -> void:
	game = load("res://scenes/game.tscn").instantiate()
	root.add_child(game)
	await process_frame
	if game.catalog.size() != 30 or not game.load_error.is_empty():
		_fail("Campaign did not initialize")
		return
	game._show_levels()
	game._start_level(1)
	await process_frame
	var tap := InputEventMouseButton.new()
	tap.button_index = MOUSE_BUTTON_LEFT
	tap.pressed = true
	tap.position = game.board_view.cell_center(Vector2i(1, 1))
	game.board_view._gui_input(tap)
	if game.run.mistakes != 1 or game.engine.snapshot().count() != 4:
		_fail("Blocked pointer tap changed the board")
		return
	game._start_level(1)
	for number in range(1, 31):
		if game.current_level != number or game.screen != "game":
			_fail("Next-level transition failed")
			return
		game._show_pause()
		var before: int = game.engine.snapshot().count()
		game._on_arrow_tapped(game.engine.legal_moves()[0])
		if game.engine.snapshot().count() != before:
			_fail("Paused input mutated engine")
			return
		game._resume()
		var solution: Dictionary = PuzzleSolver.solve(game.engine.snapshot())
		for arrow_id in solution.moves:
			game._on_arrow_tapped(arrow_id)
		await create_timer(0.60).timeout
		if game.screen != "complete" or game.progress.records[str(number)].stars != 3:
			_fail("Complete/save flow failed for level %d" % number)
			return
		if number < 30:
			game._start_level(number + 1)
	game._show_home()
	game._show_settings()
	game._setting_changed(false, "sound")
	game._close_overlay()
	if game.progress.total_stars() != 90:
		_fail("Campaign stars do not total 90")
		return
	game.queue_free()
	await process_frame
	print("PASS: scene initialized, input blocking, pause, 30 wins, next-level flow, 90 stars")
	quit(0)

func _fail(message: String) -> void:
	push_error(message)
	quit(1)
