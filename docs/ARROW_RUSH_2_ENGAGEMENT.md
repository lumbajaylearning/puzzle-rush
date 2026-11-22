# Arrow Rush 2.0 — Engagement and Level Design Specification

Status: proposed, implementation pending. Engine: Godot 4 / GDScript (per repository README).

## Design principles
- Preserve the original arrow-path escape puzzle and deterministic collision rules.
- Reward skill and experimentation, not compulsive spending or forced waiting.
- Keep gameplay offline-first; events and cloud sync are optional.
- Never serve unwinnable boards. Validate every generated level with a solver.
- No mandatory interstitial ad after each level; optional rewarded hints only.

## Core loop
Select level -> inspect dependencies -> tap arrow -> animate exit or blocked bounce -> clear board -> award 1–3 stars based on mistakes/hints/time -> progress map -> next level.

## Initial 150-level content plan
1–20: teach arrow movement and dependencies; target 15–45 seconds.
21–40: turns, overlaps, multiple paths; target 30–70 seconds.
41–60: locked arrows requiring designated keys; target 45–90 seconds.
61–90: chain reactions, one-way gates; target 60–120 seconds.
91–150: mix mastered mechanics, optional expert objectives; target 60–180 seconds.

Difficulty must alternate challenge with recovery. Do not equate difficulty with arbitrary arrow density.

## Systems and acceptance criteria
### Gameplay polish
- Input accepted anywhere along arrow polyline.
- Blocked move gives <150ms tactile/visual feedback; no unintended state mutation.
- Exit animation remains smooth at 60fps on mid-range Android.
- Undo and restart available; haptics and sound independently toggleable.
- Accessibility: high-contrast mode, reduced motion, large touch targets.

### Progression
- Level result persisted locally with best stars, attempts, hints and completion.
- Three stars are mastery rewards, never required for campaign advancement.
- Map displays current level, completed nodes, and chapter milestones.
- Next level is accessible within two taps after victory.

### Rewards
- Earn coins from level wins and optional objectives; cap repeat farming.
- Hints can be purchased with earned coins; optional rewarded ad later.
- Daily challenge generated from a deterministic seed and solvability-checked.
- Daily participation rewards are cumulative and never reset harshly.

### Content generation
- Level data versioned and seeded; each level has stable ID.
- Generator produces candidate layouts; solver verifies at least one solution.
- Reject trivially empty and excessively branching puzzles.
- Store estimated solution length, dependencies, branching and playtest difficulty.
- Hand-curate first 30 levels and tutorial moments.

### Analytics (privacy-respecting, opt-in where required)
Track: tutorial_start/complete, level_start/complete/fail, hint_used, restart, session_start/end, daily_challenge_start/complete, crash. Avoid collecting personal data unnecessarily.
Monitor D1, D7, median session length, per-level fail rate, retry rate, hint rate and drop-off.

## Implementation order
P0: inspect existing Godot scenes/scripts, add tests for collision/solver and restore reliable Android build.
P1: animation/input polish, progression persistence, results/stars, next-level flow.
P2: curated 30-level difficulty overhaul, chapter map, earned coins/hints.
P3: extend to 150 solver-verified levels, daily challenge, optional quests.
P4: telemetry, balancing playtests, accessibility, monetization after validation.

## Initial release gates
- All 150 levels solvable by automated verification.
- Zero known progress-loss bugs; migration tested.
- Tutorial completion and per-level funnel measurable.
- Test with at least 20 new players before expanding monetization.
- No unskippable ad interruptions during a puzzle.

## Open questions
- Existing scene architecture and current working gameplay must be inspected before implementation.
- Confirm whether Godot project currently builds on Android and whether levels are serialized resources or JSON.
