class_name BoardModel
extends RefCounted
## Owns occupancy. Presentation receives copies and cannot mutate the live board.

var width: int
var height: int
var _arrows: Dictionary = {}
var _occupancy: Dictionary = {}

func _init(columns: int = 4, rows: int = 4) -> void:
	width = columns
	height = rows

func contains(cell: Vector2i) -> bool:
	return cell.x >= 0 and cell.y >= 0 and cell.x < width and cell.y < height

func occupant(cell: Vector2i) -> int:
	return int(_occupancy.get(cell, -1))

func add_arrow(arrow: ArrowModel) -> bool:
	if arrow.id < 0 or _arrows.has(arrow.id) or arrow.cells.is_empty():
		return false
	if not ArrowModel.DIRECTIONS.values().has(arrow.direction):
		return false
	var visited: Dictionary = {}
	for i in range(arrow.cells.size()):
		var cell := arrow.cells[i]
		if not contains(cell) or occupant(cell) != -1 or visited.has(cell):
			return false
		# V1 arrows are straight rigid pieces. Future mechanics get explicit rules.
		if i > 0 and cell - arrow.cells[i - 1] != arrow.direction:
			return false
		visited[cell] = true
	_arrows[arrow.id] = arrow.copy()
	for cell in arrow.cells:
		_occupancy[cell] = arrow.id
	return true

func arrow_by_id(arrow_id: int) -> ArrowModel:
	var arrow: ArrowModel = _arrows.get(arrow_id)
	return arrow.copy() if arrow != null else null

func arrows() -> Array[ArrowModel]:
	var result: Array[ArrowModel] = []
	for arrow: ArrowModel in _arrows.values():
		result.append(arrow.copy())
	return result

func count() -> int:
	return _arrows.size()

func remove_arrow(arrow_id: int) -> bool:
	if not _arrows.has(arrow_id):
		return false
	for cell in _arrows[arrow_id].cells:
		_occupancy.erase(cell)
	_arrows.erase(arrow_id)
	return true

func copy() -> BoardModel:
	var result := BoardModel.new(width, height)
	for arrow in arrows():
		result.add_arrow(arrow)
	return result
