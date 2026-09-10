class_name BoardView
extends Control
## Draws snapshots only. Logical removals occur before their visual animation.

signal arrow_tapped(arrow_id: int)

const COLORS := [Color("9af6cc"), Color("b3a1ff"), Color("ffb58d"), Color("83d9ff")]
var board: BoardModel
var departing: Array[Dictionary] = []
var particles: Array[Dictionary] = []
var bumps: Dictionary = {}
var hint_id: int = -1
var reduced_motion := false
var interactive := true
var clock := 0.0

func _ready() -> void:
	mouse_filter = Control.MOUSE_FILTER_STOP
	clip_contents = true

func display(snapshot: BoardModel) -> void:
	board = snapshot
	queue_redraw()

func reset(snapshot: BoardModel) -> void:
	departing.clear()
	particles.clear()
	bumps.clear()
	hint_id = -1
	display(snapshot)

func geometry() -> Dictionary:
	if board == null:
		return {"cell": 60.0, "origin": Vector2.ZERO}
	var cell := minf((size.x - 36.0) / board.width, (size.y - 36.0) / board.height)
	return {"cell": cell, "origin": (size - Vector2(board.width, board.height) * cell) * 0.5}

func cell_center(cell: Vector2i) -> Vector2:
	var g := geometry()
	return g.origin + (Vector2(cell) + Vector2.ONE * 0.5) * float(g.cell)

func _gui_input(event: InputEvent) -> void:
	if not interactive or board == null:
		return
	# Touch is emulated as mouse once by the project settings: no duplicate taps.
	if event is InputEventMouseButton and event.button_index == MOUSE_BUTTON_LEFT and event.pressed:
		var g := geometry()
		var local: Vector2 = (event.position - g.origin) / float(g.cell)
		var cell := Vector2i(floori(local.x), floori(local.y))
		var arrow_id := board.occupant(cell)
		if arrow_id != -1:
			accept_event()
			arrow_tapped.emit(arrow_id)

func escape(arrow: ArrowModel, combo: int) -> void:
	var travel := maxf(size.x, size.y) + float(arrow.cells.size()) * float(geometry().cell)
	departing.append({"arrow": arrow, "age": 0.0, "duration": 0.18 if reduced_motion else 0.42, "travel": travel, "combo": combo})
	if reduced_motion:
		return
	var count := mini(6 + combo * 2, 28)
	for i in range(count):
		var angle := TAU * float(i) / count + clock
		particles.append({"position": cell_center(arrow.head()), "velocity": Vector2.from_angle(angle) * (65.0 + (i % 4) * 30.0), "age": 0.0, "color": COLORS[posmod(arrow.color_index, COLORS.size())]})

func blocked(arrow_id: int, blocking_id: int) -> void:
	bumps[arrow_id] = 0.0
	hint_id = blocking_id

func _process(delta: float) -> void:
	clock += delta
	for i in range(departing.size() - 1, -1, -1):
		departing[i].age += delta
		if departing[i].age >= departing[i].duration:
			departing.remove_at(i)
	for i in range(particles.size() - 1, -1, -1):
		particles[i].age += delta
		particles[i].position += particles[i].velocity * delta
		if particles[i].age >= 0.55:
			particles.remove_at(i)
	for arrow_id in bumps.keys():
		bumps[arrow_id] += delta
		if bumps[arrow_id] >= 0.32:
			bumps.erase(arrow_id)
	queue_redraw()

func _draw() -> void:
	if board == null:
		return
	var g := geometry()
	var cell: float = g.cell
	for y in range(board.height):
		for x in range(board.width):
			draw_circle(cell_center(Vector2i(x, y)), 2.3, Color("293447"))
	for arrow in board.arrows():
		var offset := Vector2.ZERO
		if bumps.has(arrow.id) and not reduced_motion:
			offset = Vector2(arrow.direction) * sin(float(bumps[arrow.id]) * 30.0) * 7.0
		_draw_arrow(arrow, offset, 1.0, arrow.id == hint_id, cell)
	for item in departing:
		var t: float = item.age / item.duration
		var shift := Vector2.ZERO if reduced_motion else Vector2(item.arrow.direction) * float(item.travel) * t * t
		if int(item.combo) >= 3 and not reduced_motion:
			_draw_arrow(item.arrow, shift - Vector2(item.arrow.direction) * cell * 0.3, (1.0 - t) * 0.16, false, cell)
		_draw_arrow(item.arrow, shift, 1.0 - t, false, cell)
	for particle in particles:
		var color: Color = particle.color
		color.a = 1.0 - float(particle.age) / 0.55
		draw_circle(particle.position, 3.0 * color.a + 1.0, color)

func _draw_arrow(arrow: ArrowModel, shift: Vector2, alpha: float, highlighted: bool, cell: float) -> void:
	var color: Color = COLORS[posmod(arrow.color_index, COLORS.size())]
	color.a = alpha
	var d := Vector2(arrow.direction)
	var normal := Vector2(-d.y, d.x)
	var tail := cell_center(arrow.cells.front()) - d * cell * 0.23 + shift
	var tip := cell_center(arrow.head()) + d * cell * 0.25 + shift
	var line_width := maxf(5.0, cell * 0.085)
	var back := tip - d * cell * 0.21
	var faint := Color(color, alpha * (0.23 if highlighted else 0.055))
	draw_line(tail, tip, faint, cell * 0.65, true)
	draw_circle(tail, cell * 0.325, faint)
	draw_circle(tip, cell * 0.325, faint)
	if highlighted:
		draw_arc(cell_center(arrow.head()) + shift, cell * 0.44, 0, TAU, 40, Color(color, alpha * 0.7), 2.0, true)
	draw_line(tail, tip, color, line_width, true)
	draw_circle(tail, line_width * 0.5, color)
	draw_polyline(PackedVector2Array([back + normal * cell * 0.19, tip, back - normal * cell * 0.19]), color, line_width, true)
