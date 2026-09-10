class_name ArrowModel
extends RefCounted
## Immutable-by-convention value object. Cells run from tail to head.

const DIRECTIONS := {
	"up": Vector2i.UP, "right": Vector2i.RIGHT,
	"down": Vector2i.DOWN, "left": Vector2i.LEFT,
}

var id: int
var direction: Vector2i
var cells: Array[Vector2i] = []
var color_index: int

func _init(arrow_id: int, arrow_cells: Array[Vector2i], vector: Vector2i, color: int = 0) -> void:
	id = arrow_id
	cells.assign(arrow_cells)
	direction = vector
	color_index = color

func head() -> Vector2i:
	return cells.back()

func copy() -> ArrowModel:
	return ArrowModel.new(id, cells, direction, color_index)

func to_data() -> Dictionary:
	var points: Array = []
	for cell in cells:
		points.append([cell.x, cell.y])
	var direction_name := "right"
	for key in DIRECTIONS:
		if DIRECTIONS[key] == direction:
			direction_name = key
	return {"id": id, "cells": points, "direction": direction_name, "color": color_index}
