# Test Apps

This directory contains a list fo simple sample apps developed with the purpose to test the [Prefetching Library](Prefetching-Library) and [Plugin for Android Studio](Android-Studio-Plugin).

Currently, there is a total of 1 sample app:

* [Weather and News](Test-App/Weather-and-News-app): Consume two REST APIs about the weather and news and display the content

## Testing the Prefetching Library

The library is imported using Gradle [flat directory repository](https://docs.gradle.org/current/userguide/declaring_repositories.html#sub:flat_dir_resolver), 
configured in the sample app module at `sample-app/app/libs/aar`.

### Using a newer version of the library

Go to the test app root directory and run the update script to update the imported version of the library:

```bash
sh scripts/update-nappa-library.sh
```

Alternatively, you can do the following steps to import a new version:

1. Go to the [Prefetching Library](Prefetching-Library) directory
1. Build the library
   * Via the command line:
      * Run the command `./gradlew build`
   * Via Android Studio
      * Open the project in Android Studio
      * Open the [Gradle tool window](https://www.jetbrains.com/help/idea/jetgradle-tool-window.html)
     * Double click at `Prefetching-Library > Tasks > build > build`
1. Go to `Prefetching-Library/android_prefetching_lib/build/outputs/aar`
1. Rename the file `android_prefetching_lib-debug` to `nappa-prefetching-library.aar`
1. Optionally, 
create a copy of `android_prefetching_lib-debug` and 
name it `android_prefetching_lib-debug-vMAJOR.MINOR.PATCH+BUILD`, 
replacing the values with the version specified in the library Gradle build file
1. Replace the existing library file at `sample-app/app/libs/aar`
1. Open the sample app project in Android Studio

If needed, sync Gradle at `File > Sync project with Gradle files`

### Debugging the library

1. Open the sample app project in Android Studio
1. Open the class finder at `Navigate > Class` (shortcut: double click shift OR `ctrl + N`)
1. Click to locate the source in the pop-up
1. Select the directory [Prefetching Library/android_prefetching_lib/src/main](Prefetching-Library/android_prefetching_lib/src/main)
1. Set breakpoints
1. Run the app in debug mode

**Important**: Changes in the library source code will not take effect in the sample app until updating the imported AAR file 