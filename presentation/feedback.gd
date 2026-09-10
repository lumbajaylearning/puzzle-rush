class_name GameFeedback
extends Node
## Original synthesized effects: no third-party audio assets or network use.

var sound_enabled := true
var haptics_enabled := true
var players: Array[AudioStreamPlayer] = []
var tones: Array[AudioStreamWAV] = []
var blocked_tone: AudioStreamWAV
var win_tone: AudioStreamWAV
var cursor := 0

func _ready() -> void:
	for i in range(6):
		var player := AudioStreamPlayer.new()
		player.volume_db = -13.0
		add_child(player)
		players.append(player)
	for semitone in [0, 2, 4, 7, 9, 12, 14, 16, 19, 21, 24]:
		tones.append(_synthesize(330.0 * pow(2.0, semitone / 12.0), 0.19, false))
	blocked_tone = _synthesize(125.0, 0.11, false)
	win_tone = _synthesize(523.25, 0.65, true)

func success(combo: int) -> void:
	_play(tones[mini(maxi(combo - 1, 0), tones.size() - 1)])
	_vibrate(mini(12 + combo * 2, 35), minf(0.25 + combo * 0.045, 0.7))

func blocked() -> void:
	_play(blocked_tone)
	_vibrate(35, 0.3)

func completed() -> void:
	_play(win_tone)
	_vibrate(65, 0.6)

func _play(stream: AudioStreamWAV) -> void:
	if not sound_enabled or players.is_empty():
		return
	var player := players[cursor % players.size()]
	cursor += 1
	player.stream = stream
	player.play()

func silence() -> void:
	for player in players:
		player.stop()

func _vibrate(duration: int, amplitude: float) -> void:
	if haptics_enabled and OS.has_feature("android"):
		Input.vibrate_handheld(duration, amplitude)

func _synthesize(frequency: float, duration: float, chord: bool) -> AudioStreamWAV:
	const RATE := 22050
	var samples := int(duration * RATE)
	var bytes := PackedByteArray()
	bytes.resize(samples * 2)
	for i in range(samples):
		var t := float(i) / RATE
		var envelope := minf(t / 0.008, 1.0) * pow(1.0 - t / duration, 2.0)
		var value := sin(TAU * frequency * t) * 0.6 + sin(TAU * frequency * 2.0 * t) * 0.12
		if chord:
			value += sin(TAU * frequency * 1.25 * t) * 0.16 + sin(TAU * frequency * 1.5 * t) * 0.12
		bytes.encode_s16(i * 2, int(clampf(value * envelope, -1.0, 1.0) * 32767))
	var stream := AudioStreamWAV.new()
	stream.format = AudioStreamWAV.FORMAT_16_BITS
	stream.mix_rate = RATE
	stream.data = bytes
	return stream
