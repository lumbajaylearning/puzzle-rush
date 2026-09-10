# Architecture and invariants

## Pure puzzle domain

`ArrowModel` is a value object: stable nonnegative ID, cardinal direction, cells ordered tail to head, cosmetic color index. `BoardModel` maintains both ID lookup and occupancy. Insertion validates dimensions at the level boundary, bounds, duplicate IDs, overlap, cardinal orientation and straight continuity. Engine constructor and snapshots copy models so presentation cannot mutate the live board accidentally.

A V1 arrow is a straight rigid piece. It escapes if the ray starting one cell beyond its head reaches the boundary without intersecting another piece. Because every own cell lies directly behind the head in the same direction, head-ray checking is equivalent to sweeping its entire rigid body. The independent Python oracle implements the latter, not the engine's shorter ray algorithm.

`try_escape` rejects missing/blocked IDs without mutation. A legal arrow disappears from occupancy immediately; presentation keeps a private departure copy. Thus rapid taps cannot remove the same piece twice, and a newly unblocked piece responds immediately while a preceding animation finishes. Win means exactly zero remaining pieces. The renderer clips departing art and never owns collision truth.

## Solvability and generation

For this rule set, legal removal cannot introduce a blocker. Therefore any legal move preserves solvability. Greedy elimination is complete; no backtracking is required. If arrows remain with no legal move, their dependencies contain a cycle and the puzzle is unsolvable.

Generator construction is the reverse process: propose a new piece only if it fits and can escape in the board containing all previously placed pieces. Store the reverse insertion order as a solution witness. A bounded attempt budget returns a failure rather than hanging at impossible density. Campaign authoring samples several candidates and selects stronger dependency structures.

Difficulty combines piece count, initial legal choices, dependency layers and forced layers. This is a structural estimate, not player-calibrated difficulty. The authored tutorial intentionally introduces a four-step dependency chain. The larger campaign is not strictly monotonically harder on every level; playtesting should tune pacing before release.

## Presentation and session

`game.gd` is the app-flow controller. It loads validated level documents and coordinates `PuzzleEngine`, `RunSession`, `BoardView`, `GameFeedback` and `ProgressStore`. UI is currently built with Godot containers in code under one main scene. At the next UI-heavy milestone, separate page components without moving game rules into them.

`BoardView` translates pointer input to grid occupancy, draws typed snapshots, and maintains finite-lived exits, bumps and particles. Color is decorative; direction is encoded geometrically. `GameFeedback` synthesizes PCM audio, uses a small player pool and scales Android vibration. Reduced motion replaces translation with fading and suppresses particles/trails.

`RunSession` owns combo, elapsed active time, blocked attempts, score and stars. Pause breaks combo and stops session time. Completed progress is saved before the result animation; a page transition token prevents delayed callbacks from opening obsolete screens.

## Persistence

`ProgressStore` writes a versioned ConfigFile to a temporary path, backs up a valid prior primary, and renames the temporary file. On startup it falls back to a backup if the primary is missing, unreadable or has an unsupported version. Unlocks are reconstructed from contiguous valid completion records. Best values merge using maxima. Save failures appear on the completion screen; settings failure logs a warning and settings remain usable for that session.

Progress is local to `user://`; no login, backend or cloud backup exists. The file is not intended to be tamper-resistant. The app does not resume an unfinished puzzle after process death.

## Extension boundary

Do not put future mechanic checks in `BoardView`. Add an explicit rule/transition interface in the domain, version the level schema, and return an event list for rendering. Frozen and rotating pieces require stateful transitions; linked pieces require atomic group validation; portals require transformed travel paths; bombs require timed rules only in modes that explicitly need them.

The current monotonic solver proof does **not** automatically extend to these mechanics. Introduce state search, cycle detection and bounded generation appropriate to the new rules before shipping mixed-mechanic levels.
