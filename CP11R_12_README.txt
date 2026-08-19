CP11R.12 — F8 Forced Full-Bright

Built directly on CP11R.11 current state.

Apply:
- Extract over C:\Dev\ChaoCraft
- Replace files
- Gradle Build + Run in IntelliJ

Changes:
- ONLY the F8 viewer is now forced full-bright.
- Preview bypasses dim GUI/lightmap state completely.
- In-world rendering remains unchanged from CP11R.11.
- Performance/cache/VBO/torch fixes remain untouched.
- Reflection preview is also forced fully lit.

Expected:
- Gameplay looks exactly like CP11R.11.
- F8 viewer is bright and readable even at night/in dark worlds.
- F8 no longer appears murky or underlit.
