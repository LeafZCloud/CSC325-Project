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
<img width="1873" height="1259" alt="image" src="https://github.com/user-attachments/assets/f1085cf0-b45e-4de5-a6ec-4a9e3dbf2a00" />

<img width="1862" height="1257" alt="image" src="https://github.com/user-attachments/assets/cee159f9-4b52-432c-b087-363da9447d19" />

<img width="327" height="760" alt="image" src="https://github.com/user-attachments/assets/9371b840-0b0e-458c-8eb6-cf622b304fb6" />

<img width="1537" height="995" alt="image" src="https://github.com/user-attachments/assets/c772a082-414f-495b-ac71-c80abff12fc5" />

<!-- Add screenshots here -->

---

*Developed as a group project for CSC325 — Software Engineering, Farmingdale State College, Spring 2026.*
