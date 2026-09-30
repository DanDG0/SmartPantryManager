# Smart Pantry Manager

Java Android app that tracks the ingredients you already have and suggests recipes you can cook
using ONLY those ingredients (strict matching - no missing items, no shopping trip).

## Database choice
SQLite via SQLiteOpenHelper. <WRITE YOUR OWN 2-3 SENTENCE JUSTIFICATION HERE>

## Features
- Pantry CRUD (add, edit, delete) with input validation
- 18 pre-loaded recipes seeded on first run
- Strict-matching Suggested Recipes screen (name/plural normalisation and unit conversion)
- Recipe detail screen, Settings screen (expiry highlighting toggle)
- Empty-state messages

## Setup / Run
1. Clone the repo and open it in Android Studio.
2. Let Gradle sync.
3. Run on an emulator or device (min SDK 24).

## Not used
No Google Maps, mapping SDK or location services.
