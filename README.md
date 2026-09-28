# Route Master 🛣️🧩

> **"Build farther. Connect smarter."**

**Route Master** is a polished, offline architectural route-building puzzle game built for Android using 100% modern **Jetpack Compose** and **Kotlin**. 

Players place and rotate modular route tiles on a compact 6×6 board to construct the longest continuous connected network. Outsmart static obstacles, manage your tile supply, and maximize your route length!

---

## 🌟 Features

- 🧩 **Modular Route Tiles**: Place and rotate 4 distinct tile types:
  - ➖ **Straight**: Connects opposing sides.
  - ↩️ **Corner**: 90-degree turn connecting adjacent sides.
  - 🔀 **T-Junction**: 3-way split connection.
  - ➕ **Cross**: 4-way full intersection.
- 🚧 **Strategic Obstacles**: 4 fixed stone obstacles on the 6×6 grid challenge your spatial planning on every move.
- 🔄 **Undo & Real-time Route Indicators**: Infinite undo support during active gameplay, along with dynamic overlays when your longest route expands.
- 🎨 **Dual Board Themes**: Choose between **Midnight Cyber Teal** and **Classic Slate** visual themes.
- 🎵 **Rich Audio & Haptics**: Procedural audio feedback via `SoundPool` / `ToneGenerator` and refined tactile haptic responses.
- 📊 **Career Performance Stats**: Persistent tracking of total games played, best route length, move counts, and completion times powered by **Jetpack DataStore**.
- ⚡ **100% Offline & Ad-Free**: No internet required, no background analytics, zero popups or ads.

---

## 🎮 How to Play

1. **Select a Tile**: Pick one of the 3 available tiles in your active tile tray.
2. **Rotate**: Tap the **Rotate** button to rotate the tile 90° clockwise.
3. **Place**: Tap any empty playable grid cell to place the tile on the board.
4. **Connect**: Tiles connect when edge openings directly line up with neighboring openings.
5. **Score**: When your tile deck (18 tiles total) runs out or the grid fills up, your final score is the total number of connected tiles in your single longest continuous network!

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.x
- **UI Framework**: Jetpack Compose with Material 3 & Navigation Compose
- **Graphics**: Custom Compose `Canvas` rendering for crisp vector tiles and board grids
- **State Management**: Clean MVVM architecture with `StateFlow`, `ViewModel`, and immutable UI state
- **Engine / Algorithm**: Breadth-First Search (BFS) graph traversal (`RouteGraph`) for fast connected-component detection and longest route calculation
- **Persistence**: `androidx.datastore.preferences` for user settings and career statistics
- **Minimum SDK**: API 24 (Android 7.0) | **Target SDK**: API 37

---

## 📂 Project Structure

```
com.example.routemaster
├── audio          # SoundController (SoundPool) and HapticController
├── data           # GamePreferencesRepository & DataStore models
├── engine         # RouteGraph (BFS graph evaluation) and TileBag
├── model          # GridPosition, RouteCell, RouteTile, TileKind, Direction
└── ui
    ├── about      # AboutScreen
    ├── components # RouteBoardCanvas, RouteTileCanvas, GameHud, TileTrayView
    ├── game       # GameScreen, GameViewModel, RouteMasterState
    ├── home       # HomeScreen
    ├── howtoplay  # HowToPlayScreen
    ├── navigation # RouteMasterNavHost & Screen routes
    ├── result     # ResultScreen
    ├── settings   # SettingsScreen & SettingsViewModel
    ├── splash     # SplashScreen
    └── theme      # Material 3 Color palette, Typography, Themes
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug (2024.2.1) or newer
- **JDK**: Java 11 or Java 17
- **Android SDK**: API level 37 installed

### Building from Source

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/Route-Master.git
   cd Route-Master
   ```

2. **Build debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

3. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

4. **Install on connected Android device/emulator**:
   ```bash
   ./gradlew installDebug
   ```

---

## 📄 License & Credits

Designed and developed by **[Abdullah Ateeq](https://github.com/abdullah-ateeq)**.  
Built as a modern Kotlin & Jetpack Compose Android showcase.
