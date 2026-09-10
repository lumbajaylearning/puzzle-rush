class_name PuzzleEngine
extends RefCounted
## No scene tree, input, timers, audio, save files, or animation dependencies.

var _board: BoardModel

func _init(board: BoardModel) -> void:
	_board = board.copy()

func snapshot() -> BoardModel:
	return _board.copy()

func blocker(arrow_id: int) -> int:
	var arrow := _board.arrow_by_id(arrow_id)
	if arrow == null:
		return -1
	var cell := arrow.head() + arrow.direction
	while _board.contains(cell):
		var other := _board.occupant(cell)
		if other != -1:
			return other
		cell += arrow.direction
	return -1

func can_escape(arrow_id: int) -> bool:
	return _board.arrow_by_id(arrow_id) != null and blocker(arrow_id) == -1

func legal_moves() -> Array[int]:
	var result: Array[int] = []
	for arrow in _board.arrows():
		if can_escape(arrow.id):
			result.append(arrow.id)
	result.sort()
	return result

func try_escape(arrow_id: int) -> Dictionary:
	if _board.arrow_by_id(arrow_id) == null:
		return {"ok": false, "reason": "missing", "blocker": -1}
	var blocked_by := blocker(arrow_id)
	if blocked_by != -1:
		return {"ok": false, "reason": "blocked", "blocker": blocked_by}
	_board.remove_arrow(arrow_id)
	return {"ok": true, "complete": is_complete(), "remaining": _board.count()}

func is_complete() -> bool:
	return _board.count() == 0
