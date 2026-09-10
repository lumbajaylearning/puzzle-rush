class_name PuzzleGenerator
extends RefCounted
## Authoring tool for V1; gameplay loads reviewed JSON campaign files.
## Reverse insertion creates a solution witness: reverse insertion order.

static func generate(columns: int, rows: int, target: int, seed_value: int) -> Dictionary:
	if columns < 2 or rows < 2 or columns > 12 or rows > 12 or target < 1 or target > columns * rows:
		return {"ok": false, "error": "Invalid generation parameters"}
	var rng := RandomNumberGenerator.new()
	rng.seed = seed_value
	var board := BoardModel.new(columns, rows)
	var witness: Array[int] = []
	var attempts := 0
	while board.count() < target and attempts < target * 250:
		attempts += 1
		var direction: Vector2i = ArrowModel.DIRECTIONS.values()[rng.randi_range(0, 3)]
		var head := Vector2i(rng.randi_range(0, columns - 1), rng.randi_range(0, rows - 1))
		var length := 2 if rng.randf() < 0.35 else 1
		var cells: Array[Vector2i] = []
		for i in range(length - 1, -1, -1):
			cells.append(head - direction * i)
		var arrow := ArrowModel.new(board.count(), cells, direction, rng.randi_range(0, 3))
		var candidate := board.copy()
		if not candidate.add_arrow(arrow):
			continue
		if not PuzzleEngine.new(candidate).can_escape(arrow.id):
			continue
		board = candidate
		witness.push_front(arrow.id)
	if board.count() != target:
		return {"ok": false, "error": "Target density unavailable; try another seed"}
	return {"ok": true, "board": board, "solution": witness,
		"difficulty": DifficultyEvaluator.evaluate(board), "seed": seed_value}
