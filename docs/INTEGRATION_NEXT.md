# Integrating the first gameplay improvements

The repository default branch currently exposes a README describing a Godot 4 project, but the expected Godot project manifest and gameplay scenes were not found at standard paths. These scripts are **standalone building blocks**, not yet wired into a playable scene.

1. Add `scripts/progression_store.gd` to the Godot project and instantiate it in the game controller. Call `load_progress()` at startup.
2. When the existing board solver reports victory, call `LevelResult.summary(...)` and then `record_win(...)`. Only show victory once the board is actually cleared.
3. Render the returned stars on a victory overlay; provide Retry and Next buttons. Next loads level + 1 if `is_unlocked`.
4. In the existing arrow controller, use `ArrowFeedback.animate_exit` for permitted moves and `animate_blocked` for rejected moves. Do not mutate board state for blocked moves. Disable input while animation is running.
5. Run Godot editor/headless validation and Android export once the full project files are available.

Outstanding: locate or upload the actual Godot scene tree, integrate UI, test touch targets and animation frame pacing, and verify persistence across app restarts. The save schema is version 2; migrate any existing save format before release.
