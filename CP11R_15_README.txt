CP11R.15 — Head Deco + Animal Auto-Fit Hotfix

APPLY
- Extract over C:\Dev\ChaoCraft
- Replace files
- Gradle Build + Run in IntelliJ

IMPLEMENTED
- Fixes CP14 disappearing heads:
  Face/Forehead/Horns/Ears Animal Parts never SizeDown the whole Head.
- Preserves CP14 corrected FACE / FOREHEAD adaptive anchors.
- Viewer-accurate SizeDown replacement logic for Arms/Legs/Tail/Wings.
- Head SizeDown occurs ONLY when independent HeadDeco != None.
- Adds all 15 Viewer Head Deco:
  Eggshell, Cooking Pot, Wool1/2/3, Apple, Paper Bag, Cardboard,
  Bucket, Pumpkin, Pot, Can, Melon, Tree and Skull.
- Exact CHAORIP meshes converted to .cmesh.
- Exact source texture/submesh material assignments.
- Adult Head Deco follows interpolated Palette.HatPosition.
- HeadDeco suppresses Face/Forehead/Horns/Ears Animal Parts like Viewer.
- Wool1/2/3 suppress the normal Emotion Ball like Viewer.
- HeadDeco persists through DataTracker/NBT/network packets.
- F8 Parts tab gets a Head Deco selector.
- Clipboard state -> CCVL4; CCVL3/2/1 remain readable.

PASS
1. Animal head parts no longer delete the Chao head.
2. Cycle all 15 Head Deco in F8 and summon several.
3. Head Deco replaces the base Head cleanly.
4. Wool1/2/3 show no separate normal emotion ball.
5. Other Head Deco retain the normal emotion ball.
6. With HeadDeco active, facial Animal Parts disappear; limb/tail/wing parts remain.
7. Adult sliders: Head Deco follows the changing head via HatPosition.
8. Save/Quit/Rejoin preserves selected Head Deco.
