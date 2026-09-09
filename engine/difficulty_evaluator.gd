class_name DifficultyEvaluator
extends RefCounted
## Structural estimate, not a claim of measured human difficulty.

static func evaluate(board: BoardModel) -> Dictionary:
	var engine := PuzzleEngine.new(board)
	var initial_choices := engine.legal_moves().size()
	var layers := 0
	var forced_layers := 0
	while not engine.is_complete():
		var legal := engine.legal_moves()
		if legal.is_empty():
			return {"solvable": false, "score": -1}
		if legal.size() == 1:
			forced_layers += 1
		for arrow_id in legal:
			engine.try_escape(arrow_id)
		layers += 1
	return {"solvable": true, "arrows": board.count(), "initial_choices": initial_choices,
		"dependency_layers": layers, "forced_layers": forced_layers,
		"score": board.count() + layers * 5 + forced_layers * 2 - initial_choices}
