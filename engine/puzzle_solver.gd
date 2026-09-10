class_name PuzzleSolver
extends RefCounted
## Removal only reduces blockers, so any legal removal preserves solvability.
## Greedy elimination is complete for V1. Revisit this proof for new mechanics.

static func solve(board: BoardModel) -> Dictionary:
	var engine := PuzzleEngine.new(board)
	var moves: Array[int] = []
	while not engine.is_complete():
		var legal := engine.legal_moves()
		if legal.is_empty():
			return {"solvable": false, "moves": moves}
		moves.append(legal[0])
		engine.try_escape(legal[0])
	return {"solvable": true, "moves": moves}
