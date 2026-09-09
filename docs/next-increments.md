# Next increments, in order

1. **Runtime acceptance:** run the existing Godot suites, fix all parser/type/runtime failures, export APK and complete the device checklist. Do not add mechanics before this gate.
2. **Feel and onboarding:** playtest with 5–10 people. Observe first correct tap, comprehension of blocking, accidental taps, points of exit and whether combo feedback distracts from reading the board. Tune board spacing, easing, audio envelope and haptics on actual phones.
3. **Level quality:** use completion observations to tune the 30-level curve. Keep authored tutorial levels; expand generator constraints to avoid repetitive layouts and improve spatial variety. Validate every published seed offline.
4. **Retention modes:** daily seeded challenge, Zen and a distinct opt-in Rush mode. Decide offline clock behavior before streaks. Add statistics and achievements after the local run records can represent mode/version/seed.
5. **First mechanic:** frozen arrows with explicit transition rules and updated solver. Then separately linked arrows, portals, bombs, rotating arrows; combined mechanics only after each has tests and a taught progression.
6. **Analytics:** no tracking is shipped now. Later add an adapter behind a small event interface: `level_started`, `move_blocked`, `combo_peak`, `level_completed`, `level_restarted`, `session_ended`. Proposed fields: schema version, content version, level/seed, mode, attempt, elapsed active time, mistakes, score, stars, max combo. Avoid recording personal text or raw touch streams. Document consent/privacy decisions before adding an SDK.
7. **Monetization:** test rewarded hints/retries only once players demonstrate retention; preserve an uninterrupted core loop. Remove-ads purchase follows a deliberate ad design. No ad placement or payment SDK is included in this prototype.
8. **Launch:** release AAB, stable package/signing, current Play requirements, physical-device QA, original store assets, store listing experiments and controlled launch. Measure learning, level completion and repeat play before increasing acquisition spend.

The prototype makes no chart-ranking, retention or revenue promise. Its job is to establish a testable core and a distinctive, measurable feedback loop.
