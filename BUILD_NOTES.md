# Setup notes

1. Android Studio -> New Project -> "Empty Views Activity", Language: Java, package `com.example.smartpantry`, min SDK 24.
2. Delete the generated MainActivity + activity_main.xml and copy these files over the matching folders
   (`java/com/example/smartpantry`, `res/layout`, `res/menu`, and replace `AndroidManifest.xml`).
3. In `app/build.gradle` dependencies make sure you have:
       implementation 'androidx.appcompat:appcompat:1.6.1'
       implementation 'androidx.recyclerview:recyclerview:1.3.2'
       implementation 'com.google.android.material:material:1.11.0'
4. If the manifest theme errors, replace it with your project's own theme (@style/Theme.SmartPantry or similar).
5. Sync Gradle, run on an emulator.
