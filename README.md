# Connect X — Java Swing Desktop Application

A complete, object-oriented conversion of the C++ terminal project (`final.cpp`) into a graphical desktop game using Java Swing (Standard JDK without any external dependencies).

---

## 🏛️ Architecture (Model-View-Controller)

The project strictly separates business logic, graphical rendering, controller actions, and persistent I/O:

```
java/
├── leaderboard.txt             # File persistence for player rankings
├── run.sh                      # Shell script for Linux / macOS
├── run.bat                     # Batch script for Windows
├── README.md                   # Complete documentation & compile guide
└── src/
    └── com/connectx/
        ├── Main.java           # Program entry point (EDT launcher & Nimbus L&F)
        ├── model/
        │   ├── ConnectXGame.java     # Pure business logic (2D dynamic board, gravity, win/draw detection)
        │   └── PlayerEntry.java      # Encapsulated player record with score & comparator
        ├── persistence/
        │   └── LeaderboardManager.java # Thread-safe leaderboard I/O (+10/-10 score rules, Top 10 sort)
        └── view/
            ├── GameFrame.java        # Main window, CardLayout coordinator, MenuBar, Status indicators
            ├── BoardPanel.java       # Custom Swing painting (grid, tokens, gravity slots, win highlight)
            ├── MainMenuPanel.java    # Classic arcade menu screen matching C++ terminal choices
            ├── PlayerLoginDialog.java# Validation modal (alphabetic requirement & distinct names)
            ├── SettingsPanel.java    # Dynamic settings (Rows 4-20, Cols 4-20, Connect 3 to min(r,c))
            ├── RulesPanel.java       # Rules & controls explanation panel
            └── LeaderboardPanel.java # JTable + JScrollPane with live scores and reset capability
```

---

## 🎮 Features & C++ Parity

| Feature | C++ Terminal (`final.cpp`) | Java Swing GUI (`com.connectx`) |
| :--- | :--- | :--- |
| **Grid Dimensions** | 4 to 20 rows, 4 to 20 columns | Configurable via interactive JSpinners in `SettingsPanel` |
| **Connect-to-Win** | 3 to `min(rows, cols)` | Dynamically clamped spinner with automatic maximum updates |
| **Player Login** | Validates letters & prevents identical names | `PlayerLoginDialog` with regex validation & friendly alerts |
| **Scoring Rules** | Winner +10 pts, Loser -10 pts | Preserved exactly in `LeaderboardManager.java` |
| **Persistence** | Top 10 saved in `leaderboard.txt` | Read/write via `BufferedReader`/`BufferedWriter`, displayed in `JTable` |
| **Reset Leaderboard** | Choice `'C'` clears file | Dedicated "Reset Leaderboard" button with confirmation prompt |
| **Game Flow** | "Play Again" or "Main Menu" | Native dialog options ("Play Again" / "Main Menu") without restarting app |
| **Visuals** | ANSI color codes in terminal | Custom Anti-Aliased 2D Graphics with 3D gradient tokens & golden win line highlights |

---

## 🚀 How to Compile and Run

### Prerequisites
- Any Java Development Kit (JDK 11 or higher, e.g., OpenJDK, Oracle JDK, Temurin).

### Option 1: Quick Command Line (Terminal / CMD)

**On macOS / Linux:**
```bash
cd java
chmod +x run.sh
./run.sh
```

Or manually:
```bash
cd java
mkdir -p bin
javac -d bin -sourcepath src src/com/connectx/Main.java
java -cp bin com.connectx.Main
```

**On Windows:**
```bat
cd java
run.bat
```

Or manually:
```bat
cd java
mkdir bin
javac -d bin -sourcepath src src\com\connectx\Main.java
java -cp bin com.connectx.Main
```

---

### Option 2: IntelliJ IDEA
1. Open IntelliJ IDEA -> **File** -> **Open...**
2. Select the `java` folder.
3. If not marked automatically, right-click `src` folder -> **Mark Directory as** -> **Sources Root**.
4. Navigate to `src/com/connectx/Main.java`.
5. Click the green **Run** arrow next to `public static void main`.

---

### Option 3: Visual Studio Code
1. Open the `java` folder in VS Code.
2. Install the **Extension Pack for Java** (if not already installed).
3. Open `src/com/connectx/Main.java`.
4. Click **Run** above the `main` method (or press `F5`).

---

### Option 4: Eclipse IDE
1. Open Eclipse -> **File** -> **New** -> **Java Project**.
2. Uncheck "Use default location" and browse to the `java` directory.
3. Click **Finish**.
4. Right-click `Main.java` -> **Run As** -> **Java Application**.

---

## 🔒 Thread Safety Note
All Swing UI components are instantiated and updated on the **Event Dispatch Thread (EDT)** using `SwingUtilities.invokeLater()`, preventing cross-thread race conditions.
