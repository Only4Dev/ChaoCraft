CP11R.17 — Chaos Animal Part Anchors

APPLY
- Extract over C:\Dev\ChaoCraft
- Replace files
- Gradle Build + Run

CAUSE
- All ordinary Adult Animal Parts already use interpolated PaletteGroup anchors.
- Chaos Chao have adultFamily=null in ChaoCraft.
- resolveAnimalAnchor() therefore fell back to (0,0,0).
- Viewer does something different: ChangeNeutralChaos/HeroChaos/DarkChaos call
  SetDecoLoc(NChaos/HChaos/DChaos), which assigns FIXED Palette anchors.

IMPLEMENTED
- Exact NChaos/HChaos/DChaos Arms/Tail/Wings/mouth/Face/Ears positions.
- Neutral/Hero share the same anchors exactly as Viewer.
- Dark uses its own ArmsPosition.
- Chaos FACE = mouthPosition, FOREHEAD = FacePosition, Horns/Ears = EarsPosition.
- Chaos HeadDeco now also uses exact NChaos/HChaos/DChaos HatPosition.
- Child and ordinary Adult anchor logic is untouched.
- Cooking Pot, reflection, lighting, caches and performance fixes are untouched.

TEST
1. Compare the same Animal Part on Neutral/Hero/Dark Chaos against Viewer.
2. Especially Face, Forehead, Ears and Horns.
3. Test Wings/Arms/Tail too.
4. Test a HeadDeco on each Chaos alignment; it should follow HatPosition.
5. Ordinary Adult and Child parts must remain unchanged.
