# Arrow Rush · Android prototype source

An original, offline arrow-escape puzzle game in **Godot 4.5.2 / GDScript**. Three chapters—Drift, Spark, Surge—contain 30 data-driven levels. A dark, pastel visual direction and escalating combo feedback distinguish this prototype's presentation.

**Status: source implementation, not an APK delivery.** Godot and the Android SDK were absent in the authoring environment, and the dependency download was blocked. The project has not been imported, compiled, visually inspected in Godot, or run on Android. The first APK milestone is **not yet accepted**. The independent Python checks below passed; they do not establish GDScript runtime correctness.

## Open the project

1. Install [Godot 4.5.2 Standard](https://godotengine.org/download/archive/4.5.2-stable/), not the .NET edition.
2. Extract this project, open Godot, choose **Import**, and select `project.godot`.
3. Let resource import finish, then press **F5**. Start at Home → Play.
4. Tap/click a piece. It travels toward its arrowhead if every cell between the head and the board edge is free. Long pieces occupy every cell along their body.
5. First level solution: top horizontal right arrow → mint upward arrow → peach left arrow → blue upward arrow.

The source has no external runtime dependencies, plugins, accounts, server, ads, purchases, analytics SDK, or third-party audio assets. The debug export preset targets ARMv7 and ARM64. `com.navloka.arrowrush` is a provisional application ID; change it before any public release.

## Implemented in source

- Home, tutorial/help, chapter-based level selection and locked progression.
- Straight arrows occupying one or two cells; directional escape, occupancy, blockers, legal moves and completion.
- Animated exits, blocker feedback, combo trails, radial particles and synthesized audio with rising pitch.
- Android vibration calls, sound/haptic toggles, reduced-motion setting.
- Restart confirmation, pause/resume, Android Back handling and automatic pause on focus loss.
- 30 JSON levels, best stars/score/combo per level, local versioned saves and backup recovery.
- Three-star scoring, result overlay, replay and next-level flow, including the final campaign state.
- Pure solver, bounded deterministic generator and a structural difficulty evaluator for authoring.

No star is lost just for thinking slowly. Three stars require zero blocked taps; two stars allow up to `max(1, floor(arrows_cleared × 0.12))`; otherwise clearing earns one. Consecutive correct moves at most **2.4 seconds** apart increase combo. A blocked tap, inactivity or pause resets the current combo. Each escape awards `100 + min(combo − 1, 10) × 25` points. Saved best stars never decrease after a replay.

Only completed levels are persisted. An unfinished attempt is not restored after process termination. Uninstalling the app removes its local progress; Android automatic data backup is disabled in the preset.

## Verification

From the project directory, with Python 3 available:

```sh
python -m unittest discover -s tests -p 'test_*.py' -v
python tools/campaign.py verify
```

**Already passed here:** seven independent authoring tests, exhaustive head-ray versus swept-piece comparisons on a 4×4 grid, every legal branch of 20 small generated boards, validation of 30 distinct campaign levels and their stored solutions, and 3,000 randomized legal solution runs. Details are in `docs/validation.md` and `docs/campaign-validation.json`.

**Still required:** execute the actual GDScript and app suites on Linux or Windows:

```sh
python tools/run_checks.py --godot /path/to/godot
```

On Windows pass the full path to the Godot console executable, e.g. `--godot "C:\Tools\Godot\Godot_v4.5.2-stable_win64_console.exe"`. The runner imports the project, runs all four Godot suites, isolates test save directories, and fails on script errors even if Godot exits with code zero. Do not directly run persistence/app tests against your normal user-data directory.

The Godot suites cover the real engine, all 30 level witnesses, generation, score/combo timing, save/reload/recovery and a full campaign scene smoke test. They are **written but unexecuted**. They cannot replace visual/device testing.

## Build a debug APK

The repository includes `.github/workflows/android.yml`. Push to a GitHub repository with Actions enabled and run **Test and build Android APK** (or push to `main`/`master`). It downloads the official pinned Godot binary/templates, checks the published SHA-512 sums, installs Android build tools, runs runtime tests, exports, verifies signing, and uploads `ArrowRush-debug-APK`. This workflow is configured but has not been executed here. Fix any failing runtime gate before treating its result as playable.

Each CI run creates a disposable debug signing key. APKs from different runs may require uninstall/reinstall and will then lose local progress. Use a stable private debug key for sustained external testing. No release signing key belongs in this repository.

For local export:

1. Install the matching **4.5.2 export templates** through Godot's export-template manager.
2. Install OpenJDK 17 and Android SDK components following the [Godot 4.5 Android export guide](https://docs.godotengine.org/en/4.5/tutorials/export/exporting_for_android.html).
3. In **Editor Settings → Export → Android**, set Java SDK Path and Android SDK Path. Create/configure a debug keystore if Godot has not created one.
4. Run all tests, create a `build` directory, then use **Project → Export → Android → Export Project**, with debug enabled, to write `build/ArrowRush-debug.apk`. Or run:

```sh
godot --headless --path . --export-debug Android build/ArrowRush-debug.apk
adb install -r build/ArrowRush-debug.apk
```

The debug preset uses the Godot 4.5 SDK baseline (target 35), not a claim of current Play submission compliance. A public Play release is separate work: review the then-current target API requirements, produce a signed release AAB, and complete store/device acceptance. No store publication is configured.

## Project map

| Directory | Responsibility |
| --- | --- |
| `engine/` | Models, validation, movement, solver, generator, difficulty; no scene or device dependencies |
| `presentation/` | Screen flow, board rendering/input, animation/particles, audio/haptics |
| `services/` | Per-attempt scoring and local progress/settings persistence |
| `levels/` | Manifest, 30 level documents, generation seeds, solution witnesses, difficulty estimates |
| `scenes/` | Main Godot scene |
| `tests/` | Independent Python checks and actual Godot runtime suites |
| `tools/` | Campaign authoring, test runner, reproducible original icon drawing |
| `docs/` | Architecture, verification evidence, device acceptance and next increments |

`tools/campaign.py build` regenerates the deterministic campaign while keeping the authored first-level layout. Run validation and playtest the difficulty curve after regeneration. Python and Godot generators use different random-number implementations; seeds are reproducible within their own generator, not across languages.

The ZIP also contains `arrow-rush-history.bundle` with the incremental Git commits. Use `git clone arrow-rush-history.bundle arrow-rush-history` if you want history; importing `project.godot` directly does not require Git.
