class_name LevelCodec
extends RefCounted
## Strict external data boundary. Reject malformed levels before play.

static func decode(data: Dictionary) -> Dictionary:
	for key in ["width", "height", "arrows"]:
		if not data.has(key):
			return {"ok": false, "error": "Missing field: " + key}
	if not _integer(data.width) or not _integer(data.height):
		return {"ok": false, "error": "Board dimensions must be integers"}
	if int(data.width) < 2 or int(data.width) > 12 or int(data.height) < 2 or int(data.height) > 12:
		return {"ok": false, "error": "Board dimensions out of range"}
	if not data.arrows is Array or data.arrows.is_empty():
		return {"ok": false, "error": "A level must contain arrows"}
	var board := BoardModel.new(int(data.width), int(data.height))
	for item in data.arrows:
		if not item is Dictionary:
			return {"ok": false, "error": "Invalid arrow object"}
		if not _integer(item.get("id")) or not item.get("cells") is Array:
			return {"ok": false, "error": "Invalid arrow id or cells"}
		if not ArrowModel.DIRECTIONS.has(item.get("direction", "")):
			return {"ok": false, "error": "Invalid direction"}
		if not _integer(item.get("color", 0)):
			return {"ok": false, "error": "Invalid color"}
		var cells: Array[Vector2i] = []
		for point in item.cells:
			if not point is Array or point.size() != 2:
				return {"ok": false, "error": "Invalid coordinate"}
			if not _integer(point[0]) or not _integer(point[1]):
				return {"ok": false, "error": "Non-integer coordinate"}
			cells.append(Vector2i(int(point[0]), int(point[1])))
		var arrow := ArrowModel.new(int(item.id), cells, ArrowModel.DIRECTIONS[item.direction], int(item.get("color", 0)))
		if not board.add_arrow(arrow):
			return {"ok": false, "error": "Overlapping, duplicate, invalid, or out-of-bounds arrow"}
	return {"ok": true, "board": board}

static func _integer(value: Variant) -> bool:
	return (value is int or value is float) and is_finite(float(value)) and float(value) == floor(float(value))
