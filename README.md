# Paintball+

Paintball+ is a LabyMod 4 addon for Minecraft 1.8.9 that disables third-person view (F5) while the addon is enabled.

## Compatibility

Paintball+ is currently a LabyMod addon only. It is not a standalone Forge, Fabric, or vanilla Minecraft mod.

The current code depends on LabyMod's addon APIs, resource namespace system, event listeners, and addon build tooling. A standalone version would need to be built as a separate Forge/Fabric-style mod or another loader-specific port that recreates the same perspective-blocking behavior without LabyMod.

## Features

- **F5 block** - disables third-person perspective while the addon is enabled
- **First-person enforcement** - keeps the camera in first person during gameplay input
- **Tab indicator** - planned feature for marking players running Paintball+ in the tab list

## Installing

### Requirements

- Minecraft 1.8.9
- LabyMod 4 for Minecraft 1.8.9
- A built Paintball+ addon JAR, either downloaded from Releases or built from source

### Install from a release

1. Download the latest `paintball-plus-core-*.jar` file from this repository's Releases page.
2. Close Minecraft if it is running.
3. Place the JAR in your LabyMod addons folder:

```text
Windows: %appdata%\.minecraft\LabyMod\addons\
macOS:   ~/Library/Application Support/minecraft/LabyMod/addons/
Linux:   ~/.minecraft/LabyMod/addons/
```

4. Launch Minecraft 1.8.9 with LabyMod 4.
5. Open the LabyMod addon/settings menu and confirm Paintball+ is enabled.

### Build from source

If there is no release JAR yet, you can build one locally.

#### Requirements

- JDK 21
- The Gradle wrapper included in this repository

#### Steps

```bash
# macOS/Linux
./gradlew build

# Windows
.\gradlew.bat build
```

The built addon JAR will be created at:

```text
core/build/libs/paintball-plus-core-1.0.0.jar
```

Copy that JAR into your LabyMod addons folder, then launch Minecraft 1.8.9 with LabyMod 4.

> Note: If the Gradle daemon fails to start, point Gradle at JDK 21 by setting `org.gradle.java.home=<path-to-jdk-21>` in your user Gradle properties file.

## How it works

When Paintball+ is enabled, it blocks the perspective toggle globally during gameplay. The addon does not currently limit F5 blocking to Mineplex or to active Paintball games.

`MinecraftMixin` injects into `Minecraft.runTick()` immediately before vanilla reads `keyBindTogglePerspective`, clears the queued perspective toggle, and keeps the camera in first person before `thirdPersonView` can be incremented. `F5BlockListener` remains as a fallback around key, render, and camera events.

### Tab indicator

The tab indicator is planned but not implemented yet.

Planned flow:

- On Mineplex game join, POST `{ uuid, username }` to a small verification API
- Poll `GET /players?server=<serverAddress>` to get all UUIDs currently using Paintball+ in this session
- Render a `[P+]` badge next to those names in the tab list through LabyMod's tab renderer hook

The API is planned as a stateless Cloudflare Worker with a KV store and a short TTL.

## Project structure

```text
api/                                      # LabyMod addon API module
core/src/main/java/dev/paintballplus/
|-- PaintballPlus.java                    # LabyMod addon entry point
|-- PaintballPlusConfig.java              # Addon settings
|-- GameDetector.java                     # Mineplex/Paintball detection helper, not currently used for F5 blocking
|-- listener/
|   `-- F5BlockListener.java              # Blocks third-person view before key/render handling
|-- mixin/
|   `-- MinecraftMixin.java               # Hooks Minecraft perspective toggle handling
`-- tab/
    `-- ModIndicator.java                 # Planned tab indicator support
```

## Contributing

Pull requests are welcome. Please open an issue first for anything beyond a small fix so the approach can be discussed.

## License

MIT