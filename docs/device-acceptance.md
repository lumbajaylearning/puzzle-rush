# First APK acceptance

Run this after the Godot suites and APK signature checks pass. Record device/OS, result and any defect; all items below are currently **pending**.

1. Cold launch without connectivity; home and all local assets load without requests.
2. At 16:9, 19.5:9 and 20:9 phone ratios, verify no clipped titles, overlays, board edges or system-bar overlap. Check smallest intended phone and a tablet.
3. Solve level 1 using touch. Tap blocked, boundary and long pieces. Tap gaps. Rapidly double-tap one piece; it must score once. Verify only one event per physical tap.
4. Restart halfway through a level. Decline and accept confirmation. Ensure the board resets cleanly without delayed animations, stale callbacks or scoring changes to stored bests.
5. Test combos at slow and fast cadence; blocked taps reset combo, thinking slowly never reduces stars. Verify tiers increase pitch/trails/particle density/haptic strength as intended.
6. Independently disable sound/haptics; enable reduced motion; relaunch and confirm settings persist. Verify reduced motion removes movement/particles, while puzzle readability stays intact.
7. Pause, use Android Back, lock/unlock, receive an interruption and background/resume. No board changes, resumed surprise audio or elapsed combo accumulation while paused.
8. Complete a level, force-stop and reopen. Verify unlock and stars. Replay with poorer accuracy: best stars stay. Interrupt around the last tap and verify completed progress survives.
9. Walk through all 30 levels using the stored solution witnesses for smoke coverage. Level 30 ends at campaign completion, never a missing level 31. Repeat selected levels manually for pacing.
10. Check blocked arrow highlights actually clarify the next dependency. Test comfortable tapping on the 7×8 boards and enlarge interactions if needed.
11. Profile real frames and memory on a modest Android device, especially rapid combos and repeated restarts. Target stable 60 FPS, but no performance target is currently measured.
12. Review app name/package, install/update behavior and manifest permissions. Confirm vibration only among requested application permissions; no Internet requirement. Decide a stable test signing key before sharing repeat builds.

A debug APK is for device review. Play publication, release signing, current API policy review, AAB generation and store listing are a later milestone.
