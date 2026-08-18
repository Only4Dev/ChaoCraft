# CP09 — Hero Fly ear pass + preview zoom

## Included
- Hero Fly material-slot adjustment for `HFBody.mat` to better match Chao Viewer ear band layering.
- Two preview zoom buttons in the F8 Visual Lab (`Zoom -` / `Zoom +`) with hard min/max limits.
- Preview header now shows current zoom percentage.

## Not included yet
- Reflection/shiny/jewel pipeline: yes, this is a real Chao appearance concept and should be implemented in the appearance model rather than as a fake-only debug tint.
- Animal parts: deferred until we export/port the animal-part meshes/attachments into the renderer. The current repo does not yet contain those assets.

## Validation notes
- Runtime build was not executed in this container because the Gradle wrapper needs to download `gradle-9.5.1-bin.zip` and outbound network is unavailable here.
