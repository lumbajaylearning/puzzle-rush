extends Node
class_name ArrowFeedback

signal animation_finished

@export var exit_duration := 0.24
@export var blocked_duration := 0.12
var busy := false

# Call after the existing collision engine has already decided whether a move is valid.
func animate_exit(arrow: Node2D, travel: Vector2) -> void:
	if busy or not is_instance_valid(arrow):
		return
	busy = true
	var tween := create_tween()
	tween.set_trans(Tween.TRANS_CUBIC).set_ease(Tween.EASE_OUT)
	tween.tween_property(arrow, "position", arrow.position + travel, exit_duration)
	await tween.finished
	busy = false
	animation_finished.emit()

func animate_blocked(arrow: Node2D, direction: Vector2) -> void:
	if busy or not is_instance_valid(arrow):
		return
	busy = true
	var original := arrow.position
	var tween := create_tween()
	tween.tween_property(arrow, "position", original + direction.normalized() * 9.0, blocked_duration * 0.45)
	tween.tween_property(arrow, "position", original, blocked_duration * 0.55)
	await tween.finished
	busy = false
	animation_finished.emit()
