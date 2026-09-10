extends Control
## App flow coordinates pure engine, local session, renderer and device feedback.

const INK := Color("0b101a")
const SURFACE := Color("151e2d")
const MINT := Color("9af6cc")
const TEXT := Color("f4f5f9")
const MUTED := Color("9caac1")

var progress := ProgressStore.new()
var feedback: GameFeedback
var catalog: Array[Dictionary] = []
var engine: PuzzleEngine
var run: RunSession
var board_view: BoardView
var page: VBoxContainer
var overlay: Control
var score_label: Label
var combo_label: Label
var remaining_label: Label
var guidance_label: Label
var combo_bar: ProgressBar
var current_level := 1
var screen := "home"
var paused := false
var transition_id := 0
var load_error := ""
var app_active := true

func _ready() -> void:
	get_tree().auto_accept_quit = false
	feedback = GameFeedback.new()
	add_child(feedback)
	_load_catalog()
	progress.load_progress(catalog.size())
	_sync_settings()
	_show_home()

func _load_catalog() -> void:
	var manifest: Variant = JSON.parse_string(FileAccess.get_file_as_string("res://levels/campaign.json"))
	if not manifest is Dictionary or not manifest.get("levels") is Array:
		load_error = "The campaign could not be loaded. Please reinstall the game."
		return
	for path in manifest.levels:
		if not path is String:
			load_error = "Invalid campaign entry."
			return
		var data: Variant = JSON.parse_string(FileAccess.get_file_as_string(path))
		if not data is Dictionary:
			load_error = "A level file could not be read."
			return
		var decoded := LevelCodec.decode(data)
		if not decoded.ok or not PuzzleSolver.solve(decoded.board).solvable:
			load_error = "A level could not be validated."
			return
		if int(data.get("id", 0)) != catalog.size() + 1:
			load_error = "The campaign order is invalid."
			return
		catalog.append(data)

func _sync_settings() -> void:
	feedback.sound_enabled = progress.sound
	feedback.haptics_enabled = progress.haptics
	if not progress.sound:
		feedback.silence()
	if is_instance_valid(board_view):
		board_view.reduced_motion = progress.reduced_motion

func _clear_page() -> void:
	transition_id += 1
	paused = false
	_close_overlay()
	if is_instance_valid(page):
		# Remove immediately so deferred deletion cannot leave two interactive pages.
		page.get_parent().remove_child(page)
		page.queue_free()
	var old := get_node_or_null("PageMargins")
	if old != null:
		remove_child(old)
		old.queue_free()
	var margins := MarginContainer.new()
	margins.name = "PageMargins"
	add_child(margins)
	margins.set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
	margins.add_theme_constant_override("margin_left", 36)
	margins.add_theme_constant_override("margin_right", 36)
	margins.add_theme_constant_override("margin_top", 52)
	margins.add_theme_constant_override("margin_bottom", 40)
	page = VBoxContainer.new()
	page.add_theme_constant_override("separation", 18)
	margins.add_child(page)
	board_view = null

func _label(value: String, font_size: int = 24, color: Color = TEXT, centered: bool = false) -> Label:
	var result := Label.new()
	result.text = value
	result.add_theme_font_size_override("font_size", font_size)
	result.add_theme_color_override("font_color", color)
	result.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER if centered else HORIZONTAL_ALIGNMENT_LEFT
	result.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
	result.mouse_filter = Control.MOUSE_FILTER_IGNORE
	return result

func _style(color: Color, radius: int = 22) -> StyleBoxFlat:
	var style := StyleBoxFlat.new()
	style.bg_color = color
	style.set_corner_radius_all(radius)
	style.content_margin_left = 22
	style.content_margin_right = 22
	style.content_margin_top = 16
	style.content_margin_bottom = 16
	return style

func _button(value: String, action: Callable, primary: bool = false) -> Button:
	var result := Button.new()
	result.text = value
	result.custom_minimum_size.y = 76
	result.size_flags_horizontal = Control.SIZE_EXPAND_FILL
	result.add_theme_font_size_override("font_size", 24)
	result.add_theme_color_override("font_color", INK if primary else TEXT)
	result.add_theme_color_override("font_hover_color", INK if primary else TEXT)
	result.add_theme_color_override("font_pressed_color", INK if primary else TEXT)
	result.add_theme_color_override("font_disabled_color", Color("56637a"))
	result.add_theme_stylebox_override("normal", _style(MINT if primary else SURFACE))
	result.add_theme_stylebox_override("hover", _style(Color("baffdf") if primary else Color("223149")))
	result.add_theme_stylebox_override("pressed", _style(Color("73d7ad") if primary else Color("2b3a50")))
	result.add_theme_stylebox_override("disabled", _style(Color("111925")))
	var focus := _style(Color(0, 0, 0, 0))
	focus.set_border_width_all(3)
	focus.border_color = Color("b3a1ff")
	result.add_theme_stylebox_override("focus", focus)
	result.pressed.connect(action)
	return result

func _space(parent: Control, minimum: float = 0.0, expand: bool = false) -> void:
	var spacer := Control.new()
	spacer.custom_minimum_size.y = minimum
	if expand:
		spacer.size_flags_vertical = Control.SIZE_EXPAND_FILL
	parent.add_child(spacer)

func _show_home() -> void:
	_clear_page()
	screen = "home"
	page.add_child(_label("A LITTLE FOCUS. A LOT OF FLOW.", 18, MINT, true))
	_space(page, 14)
	page.add_child(_label("ARROW\nRUSH", 84, TEXT, true))
	page.add_child(_label("Clear the way. Find your rhythm.", 25, MUTED, true))
	if not load_error.is_empty():
		page.add_child(_label(load_error, 24, Color("ffb58d"), true))
		return
	var art := BoardView.new()
	art.custom_minimum_size.y = 290
	art.size_flags_vertical = Control.SIZE_EXPAND_FILL
	art.interactive = false
	art.mouse_filter = Control.MOUSE_FILTER_IGNORE
	page.add_child(art)
	art.display(LevelCodec.decode(catalog[0]).board)
	page.add_child(_label("30 LEVELS  /  3 CHAPTERS  /  YOUR PACE", 17, MUTED, true))
	page.add_child(_button("Play  •  Level %02d" % progress.unlocked, _start_level.bind(progress.unlocked), true))
	page.add_child(_button("Explore levels", _show_levels))
	var row := HBoxContainer.new()
	row.add_theme_constant_override("separation", 14)
	page.add_child(row)
	row.add_child(_button("How to play", _show_help))
	row.add_child(_button("Settings", _show_settings))
	page.add_child(_label("%d / 90 stars collected" % progress.total_stars(), 20, MUTED, true))

func _top_row(title: String, back: Callable) -> void:
	var row := HBoxContainer.new()
	row.add_theme_constant_override("separation", 18)
	page.add_child(row)
	var button := _button("‹", back)
	button.size_flags_horizontal = Control.SIZE_SHRINK_BEGIN
	button.custom_minimum_size.x = 72
	row.add_child(button)
	var heading := _label(title, 28)
	heading.size_flags_horizontal = Control.SIZE_EXPAND_FILL
	row.add_child(heading)

func _show_levels() -> void:
	_clear_page()
	screen = "levels"
	_top_row("Your journey", _show_home)
	page.add_child(_label("%d / 90 stars  •  Keep your best on every replay." % progress.total_stars(), 21, MUTED))
	var scroll := ScrollContainer.new()
	scroll.size_flags_vertical = Control.SIZE_EXPAND_FILL
	scroll.horizontal_scroll_mode = ScrollContainer.SCROLL_MODE_DISABLED
	page.add_child(scroll)
	var content := VBoxContainer.new()
	content.size_flags_horizontal = Control.SIZE_EXPAND_FILL
	content.add_theme_constant_override("separation", 20)
	scroll.add_child(content)
	for chapter in range(3):
		content.add_child(_label("0%d  /  %s" % [chapter + 1, ["DRIFT", "SPARK", "SURGE"][chapter]], 22, MINT))
		var grid := GridContainer.new()
		grid.columns = 5
		grid.add_theme_constant_override("h_separation", 10)
		grid.add_theme_constant_override("v_separation", 12)
		content.add_child(grid)
		for index in range(chapter * 10, mini(chapter * 10 + 10, catalog.size())):
			var number := index + 1
			var record: Dictionary = progress.records.get(str(number), {})
			var stars := int(record.get("stars", 0))
			var caption := "%02d\n%s" % [number, "★".repeat(stars) + "·".repeat(3 - stars)]
			if number > progress.unlocked:
				caption = "%02d\n—" % number
			var button := _button(caption, _start_level.bind(number), number == progress.unlocked)
			button.custom_minimum_size = Vector2(100, 112)
			button.add_theme_font_size_override("font_size", 22)
			button.disabled = number > progress.unlocked
			grid.add_child(button)
		_space(content, 12)

func _start_level(number: int) -> void:
	if number < 1 or number > catalog.size() or number > progress.unlocked:
		return
	_clear_page()
	screen = "game"
	current_level = number
	var data: Dictionary = catalog[number - 1]
	engine = PuzzleEngine.new(LevelCodec.decode(data).board)
	run = RunSession.new()
	_top_row("%s  /  %02d" % [data.chapter.to_upper(), number], _show_pause)
	page.add_child(_label(data.title, 38))
	var stats := HBoxContainer.new()
	page.add_child(stats)
	score_label = _label("0 points", 24)
	score_label.size_flags_horizontal = Control.SIZE_EXPAND_FILL
	stats.add_child(score_label)
	remaining_label = _label("", 23, MUTED)
	stats.add_child(remaining_label)
	board_view = BoardView.new()
	board_view.custom_minimum_size.y = 430
	board_view.size_flags_vertical = Control.SIZE_EXPAND_FILL
	board_view.reduced_motion = progress.reduced_motion
	board_view.arrow_tapped.connect(_on_arrow_tapped)
	page.add_child(board_view)
	board_view.reset(engine.snapshot())
	combo_label = _label("FIND YOUR FLOW", 22, MINT, true)
	page.add_child(combo_label)
	combo_bar = ProgressBar.new()
	combo_bar.custom_minimum_size.y = 8
	combo_bar.show_percentage = false
	combo_bar.max_value = 1.0
	for entry in [["background", SURFACE], ["fill", MINT]]:
		var bar_style := _style(entry[1], 4)
		bar_style.content_margin_left = 0
		bar_style.content_margin_right = 0
		bar_style.content_margin_top = 0
		bar_style.content_margin_bottom = 0
		combo_bar.add_theme_stylebox_override(entry[0], bar_style)
	page.add_child(combo_bar)
	guidance_label = _label(data.get("hint", "") if not str(data.get("hint", "")).is_empty() else "Look for a clear path to the edge.", 21, MUTED, true)
	guidance_label.custom_minimum_size.y = 60
	page.add_child(guidance_label)
	var row := HBoxContainer.new()
	row.add_theme_constant_override("separation", 14)
	page.add_child(row)
	row.add_child(_button("Restart", _confirm_restart))
	row.add_child(_button("Pause", _show_pause))
	_update_hud()
	# Tutorial spotlights only the first legal move; subsequent taps teach blocking.
	if number == 1:
		board_view.hint_id = 1

func _on_arrow_tapped(arrow_id: int) -> void:
	if paused or screen != "game" or engine.is_complete():
		return
	var arrow := engine.snapshot().arrow_by_id(arrow_id)
	var result := engine.try_escape(arrow_id)
	if result.ok:
		run.correct_move()
		board_view.hint_id = -1
		board_view.escape(arrow, run.combo)
		board_view.display(engine.snapshot())
		feedback.success(run.combo)
		guidance_label.text = "Clean escape." if run.combo < 3 else "Keep the rhythm."
		_update_hud()
		if engine.is_complete():
			board_view.interactive = false
			screen = "completing"
			var save_error := progress.complete_level(current_level, run, catalog.size())
			var token := transition_id
			await get_tree().create_timer(0.50).timeout
			if token == transition_id and screen == "completing":
				_show_complete(save_error)
	elif result.reason == "blocked":
		run.blocked_move()
		board_view.blocked(arrow_id, result.blocker)
		feedback.blocked()
		guidance_label.text = "Path blocked. Follow the highlighted piece's path."
		_update_hud()

func _update_hud() -> void:
	score_label.text = "%s points" % run.score
	remaining_label.text = "%d left" % engine.snapshot().count()
	combo_label.text = "FIND YOUR FLOW" if run.combo < 2 else "%d× COMBO  /  %s" % [run.combo, "FLOW" if run.combo < 5 else "ON FIRE" if run.combo < 9 else "SUPER FLOW"]

func _process(delta: float) -> void:
	if screen == "game" and not paused and run != null:
		var previous_combo := run.combo
		run.advance(delta)
		combo_bar.value = run.meter()
		if previous_combo != run.combo:
			_update_hud()

func _modal(title: String, subtitle: String) -> VBoxContainer:
	_close_overlay()
	overlay = Control.new()
	add_child(overlay)
	overlay.set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
	var shade := ColorRect.new()
	shade.color = Color(0.02, 0.03, 0.06, 0.91)
	overlay.add_child(shade)
	shade.set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
	var center := CenterContainer.new()
	overlay.add_child(center)
	center.set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
	var panel := PanelContainer.new()
	panel.custom_minimum_size.x = 600
	panel.add_theme_stylebox_override("panel", _style(SURFACE, 32))
	center.add_child(panel)
	var content := VBoxContainer.new()
	content.add_theme_constant_override("separation", 20)
	panel.add_child(content)
	content.add_child(_label(title, 42, TEXT, true))
	if not subtitle.is_empty():
		var copy := _label(subtitle, 23, MUTED, true)
		copy.custom_minimum_size.x = 540
		content.add_child(copy)
	return content

func _close_overlay() -> void:
	if is_instance_valid(overlay):
		remove_child(overlay)
		overlay.queue_free()
	overlay = null

func _resume() -> void:
	_close_overlay()
	paused = false
	if is_instance_valid(board_view):
		board_view.interactive = true

func _show_pause() -> void:
	if screen != "game":
		return
	paused = true
	run.break_combo()
	board_view.interactive = false
	feedback.silence()
	var content := _modal("Take a breath", "Your board is waiting right here.")
	content.add_child(_button("Resume", _resume, true))
	content.add_child(_button("Restart level", _confirm_restart))
	content.add_child(_button("Home", _show_home))

func _confirm_restart() -> void:
	if screen != "game":
		return
	paused = true
	run.break_combo()
	board_view.interactive = false
	var content := _modal("Start fresh?", "This attempt will reset. Your saved stars stay safe.")
	content.add_child(_button("Restart", _start_level.bind(current_level), true))
	content.add_child(_button("Keep playing", _resume))

func _show_complete(save_error: Error) -> void:
	screen = "complete"
	if app_active:
		feedback.completed()
	var final_level := current_level == catalog.size()
	var content := _modal("Campaign cleared!" if final_level else "Beautifully done.", str(catalog[current_level - 1].title))
	content.add_child(_label("★".repeat(run.stars()) + "☆".repeat(3 - run.stars()), 66, MINT, true))
	content.add_child(_label("%d points   /   %d× best combo" % [run.score, run.best_combo], 26, TEXT, true))
	content.add_child(_label("%d blocked taps  •  %d clean escapes" % [run.mistakes, run.moves], 22, MUTED, true))
	content.add_child(_label("Perfect accuracy." if run.stars() == 3 else "For three stars, clear without a blocked tap.", 22, MUTED, true))
	if save_error != OK:
		content.add_child(_label("Progress could not be saved. Check device storage.", 22, Color("ffb58d"), true))
	content.add_child(_button("Explore your stars" if final_level else "Next level  →", _show_levels if final_level else _start_level.bind(current_level + 1), true))
	content.add_child(_button("Play again", _start_level.bind(current_level)))
	content.add_child(_button("Home", _show_home))

func _show_help() -> void:
	var content := _modal("Clear the way", "Tap an arrow to send it out in the direction it points.\n\nIf another piece is in its path, clear that piece first.\n\nConsecutive clean moves within 2.4 seconds build your combo.\n\nThree stars means no blocked taps. Take your time: speed is optional.")
	content.add_child(_button("Got it", _close_overlay, true))

func _show_settings() -> void:
	var content := _modal("Make it yours", "A little quieter. A little calmer. Your choice.")
	for entry in [["Sound effects", "sound"], ["Haptic feedback", "haptics"], ["Reduced motion", "reduced_motion"]]:
		var toggle := CheckButton.new()
		toggle.text = entry[0]
		toggle.custom_minimum_size.y = 76
		toggle.add_theme_font_size_override("font_size", 25)
		toggle.button_pressed = bool(progress.get(entry[1]))
		toggle.toggled.connect(_setting_changed.bind(entry[1]))
		content.add_child(toggle)
	content.add_child(_button("Done", _close_overlay, true))

func _setting_changed(value: bool, key: String) -> void:
	progress.set(key, value)
	_sync_settings()
	if progress.save() != OK:
		push_warning("Settings could not be saved; available for this session only.")

func _notification(what: int) -> void:
	if not is_node_ready():
		return
	if what == NOTIFICATION_APPLICATION_FOCUS_OUT or what == NOTIFICATION_APPLICATION_PAUSED:
		app_active = false
		feedback.silence()
		if screen == "game" and not paused:
			_show_pause()
	elif what == NOTIFICATION_APPLICATION_FOCUS_IN or what == NOTIFICATION_APPLICATION_RESUMED:
		app_active = true
	elif what == NOTIFICATION_WM_GO_BACK_REQUEST:
		_go_back()
	elif what == NOTIFICATION_WM_CLOSE_REQUEST:
		get_tree().quit()

func _unhandled_key_input(event: InputEvent) -> void:
	if event.is_action_pressed("ui_cancel"):
		get_viewport().set_input_as_handled()
		_go_back()

func _go_back() -> void:
	if screen == "completing":
		return
	if screen == "game":
		if paused:
			_resume()
		else:
			_show_pause()
	elif is_instance_valid(overlay):
		if screen == "complete":
			_show_home()
		else:
			_close_overlay()
	elif screen == "home":
		var content := _modal("See you soon?", "Your completed levels are saved on this device.")
		content.add_child(_button("Keep playing", _close_overlay, true))
		content.add_child(_button("Exit", get_tree().quit))
	else:
		_show_home()
