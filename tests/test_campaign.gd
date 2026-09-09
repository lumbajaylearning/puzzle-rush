extends SceneTree

func _initialize() -> void:
	var manifest: Dictionary = JSON.parse_string(FileAccess.get_file_as_string("res://levels/campaign.json"))
	if manifest.levels.size() != 30:
		_fail("Expected 30 campaign levels")
		return
	for path in manifest.levels:
		var data: Dictionary = JSON.parse_string(FileAccess.get_file_as_string(path))
		var decoded := LevelCodec.decode(data)
		if not decoded.ok:
			_fail("Invalid level: " + path)
			return
		if not PuzzleSolver.solve(decoded.board).solvable:
			_fail("Unsolvable level: " + path)
			return
		var engine := PuzzleEngine.new(decoded.board)
		for arrow_id in data.solution:
			if not engine.try_escape(int(arrow_id)).ok:
				_fail("Invalid stored witness: " + path)
				return
		if not engine.is_complete():
			_fail("Incomplete witness: " + path)
			return
		var measured := DifficultyEvaluator.evaluate(decoded.board)
		for key in ["arrows", "initial_choices", "dependency_layers", "forced_layers", "score"]:
			if int(measured[key]) != int(data.difficulty[key]):
				_fail("Difficulty disagreement: " + path + "/" + key)
				return
	var generated_count := 0
	for seed_value in range(100):
		var result := PuzzleGenerator.generate(5, 5, 12, seed_value)
		if not result.ok:
			continue # Bounded generator explicitly reports density failure.
		generated_count += 1
		var engine := PuzzleEngine.new(result.board)
		for arrow_id in result.solution:
			if not engine.try_escape(arrow_id).ok:
				_fail("Generator produced an invalid witness")
				return
		if not engine.is_complete():
			_fail("Generator witness did not finish")
			return
	if generated_count < 90:
		_fail("Generator success rate below 90% for modest density")
		return
	print("PASS: 30 campaign solutions and %d generated boards" % generated_count)
	quit(0)

func _fail(message: String) -> void:
	push_error(message)
	quit(1)
