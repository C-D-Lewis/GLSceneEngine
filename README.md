# GLSceneEngine

Scene-based OpenGL Java game engine. `src/core/BSEDemoMain.java` demonstrates a basic instantiation for 'Hello World!' display.


## Features

- Single threaded update, render, repeat game loop.
- `EventBus` for simplified inter-object messaging, registering, broadcasting etc.
- LWJGL-based OpenGL helpers for images, rects, etc.
- `FontRenderer` for bitmap-based string rendering in OpenGL.
- `TileSheetParser` to load tiles/sprites/font characters as equally-sized bitmaps from a larger sheet. Parsed tiles are uploaded to OpenGL.
- `INIParser` class to read simple `.ini` files.
