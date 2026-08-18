# ChaoCraft

## Setup

For setup instructions, please see the [Fabric Documentation page](https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up) related to the IDE that you are using.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.

## CP07.6 development notes

ChaoCraft now creates `config/chaocraft-server.properties` with separate server-authoritative Chao simulation and view/tracking distances. The Visual Lab remains client-only and uses the production Chao renderer for its rotating preview. See `CP07_6_VIEWER_FINAL.md`.


## CP07.7 Visual Lab workflow

The F8 Visual Lab is now an isolated virtual Chao editor/preview. It never edits existing world Chao. Use **Summon Chao** to create the current draft server-side, or the matrix buttons for QA. Auto-rotate can be disabled to rotate the preview manually by dragging.
