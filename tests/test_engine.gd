extends SceneTree
## Execute with: godot --headless --path . --script tests/test_engine.gd

var failures := 0
var checks := 0

func check(condition: bool, description: String) -> void:
	checks += 1
	if not condition:
		failures += 1
		push_error(description)

func _initialize() -> void:
	var data: Dictionary = JSON.parse_string(FileAccess.get_file_as_string("res://levels/level_01.json"))
	var decoded := LevelCodec.decode(data)
	check(decoded.ok, "Load tutorial")
	var engine := PuzzleEngine.new(decoded.board)
	check(not engine.can_escape(0), "Blocker ahead prevents escape")
	check(engine.blocker(0) == 1, "Report nearest blocker")
	check(engine.can_escape(1), "Boundary path is free")
	check(not engine.try_escape(0).ok, "Invalid attempt rejected")
	check(engine.snapshot().count() == 4, "Rejected attempt never changes board")
	check(not engine.can_escape(999), "Unknown id is not legal")
	check(not engine.try_escape(999).ok, "Unknown id does not mutate")
	var view := engine.snapshot()
	view.remove_arrow(1)
	check(not engine.can_escape(0), "Snapshot is isolated")
	check(engine.try_escape(1).ok, "Remove legal piece")
	check(not engine.try_escape(1).ok, "Repeated tap cannot remove twice")
	check(engine.can_escape(0), "Removal unblocks dependent arrow")
	for arrow_id in [0, 2, 3]:
		check(engine.try_escape(arrow_id).ok, "Follow tutorial solution")
	check(engine.is_complete(), "Win only when empty")
	check(decoded.board.count() == 4, "Original board is not mutated")
	for direction: Vector2i in ArrowModel.DIRECTIONS.values():
		var board := BoardModel.new(3, 3)
		var cells: Array[Vector2i] = [Vector2i(1, 1)]
		check(board.add_arrow(ArrowModel.new(0, cells, direction)), "Create cardinal arrow")
		check(PuzzleEngine.new(board).can_escape(0), "Every direction escapes")
	var cycle := {"width": 3, "height": 2, "arrows": [
		{"id": 0, "cells": [[0, 0]], "direction": "right"},
		{"id": 1, "cells": [[2, 0]], "direction": "left"}]}
	check(not PuzzleSolver.solve(LevelCodec.decode(cycle).board).solvable, "Solver detects cycle")
	cycle.arrows[1].cells = [[0, 0]]
	check(not LevelCodec.decode(cycle).ok, "Reject overlap")
	cycle.arrows[1].cells = [[3, 0]]
	check(not LevelCodec.decode(cycle).ok, "Reject outside board")
	cycle.arrows[1].cells = [[1.5, 0]]
	check(not LevelCodec.decode(cycle).ok, "Reject fractional coordinates")
	print("Engine checks: %d; failures: %d" % [checks, failures])
	quit(1 if failures else 0)
