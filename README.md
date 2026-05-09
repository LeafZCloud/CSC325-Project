# 🌍 Planet Sandbox

A JavaFX-based planet simulation game where you build and manage your own world. Trigger global events, track civilization stats, and watch your planet evolve — or collapse.

---

## Features

- **Custom Planet Creation** — Name your planet, choose its type (Terran, Arid, Oceanic, Volcanic), and configure the number of continents and moons
- **Real-Time Simulation** — Your planet progresses year by year with a live world map and atmospheric background music
- **Event System** — Trigger disasters, conflicts, technological breakthroughs, and societal events across categorized tabs (All, Disasters, Conflicts, Technology, Society)
- **World Console** — Monitor global stats in real time:
  - Population (in billions)
  - Stress, Economic Health, and Exposure to Events (by percentage)
  - Per-region stability breakdowns
- **Event Log & Social Feed** — Review triggered events with detailed impact breakdowns, and read citizen/analyst reactions in a live world feed
- **Save & Load System** — Save your game to one of 3 slots at any time using the Save Game button, or auto-save to Slot 1 on End Simulation. Load previous saves from the planet creation screen
- **Secure Authentication** — Firebase-backed login and sign-up with encrypted token-based auth
- **End Game Summary** — View a full recap of your simulation, then choose to start a new planet or load a saved game

---

## Tech Stack

- **Language:** Java 21
- **UI Framework:** JavaFX
- **Backend/Auth:** Firebase Authentication (REST API)
- **Database:** Firebase Firestore (REST API)
- **HTTP Client:** OkHttp
- **JSON Parsing:** Gson
- **Build Tool:** Maven

---

## Setup & Running

### Prerequisites
- JDK 21 (Temurin recommended)
- Maven
- Internet connection (required for Firebase auth and save/load)

### Firebase Configuration
To run your own instance, replace the API keys in `FirebaseAuthService.java` 
and `DatabaseController.java` with your own Firebase project credentials.

Replace (YOUR_API_KEY):
```bash
private static final String SIGN_IN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=YOUR_API_KEY";
private static final String SIGN_UP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=YOUR_API_KEY";
```

Replace (YOUR_PROJECT_ID):
```bash
private static final String FIRESTORE_URL = "https://firestore.googleapis.com/v1/projects/YOUR_PROJECT_ID/databases/(default)/documents/gameStates/";
```

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/LeafZCloud/CSC325-Project.git
   cd CSC325-Project
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn javafx:run
   ```

> **Note for Linux users:** Resource filenames are case-sensitive. All image assets use lowercase extensions (`.png`, `.jpg`).

---

## Contributors

| Name | Role |
|------|------|
| Hamdi Tekci | Firebase Authentication, Firestore Save/Load System, Database Controller |
| Brandon | Simulation Logic, Event System, UI Components |
| Maximus | Simulation Model, Region & Stat Systems |
| Alif | UI Design, Event Cards, Interface Buttons |
| Rochelle | Planet Animations, Event Visual Effects, Map Biomes |

---

## Screenshots

<img width="2554" height="1439" alt="image" src="https://github.com/user-attachments/assets/7fd52941-0a17-4b30-8c05-854ee3632219" />

<img width="235" height="212" alt="image" src="https://github.com/user-attachments/assets/65a32491-5bfc-403c-bdbf-24853849405b" />

<img width="1537" height="995" alt="image" src="https://github.com/user-attachments/assets/c772a082-414f-495b-ac71-c80abff12fc5" />

<!-- Add screenshots here -->

---

*Developed as a group project for CSC325 — Software Engineering, Farmingdale State College, Spring 2026.*
