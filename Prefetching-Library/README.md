# NAPPA Android Prefetch Library

This project contains a library for Android to support prefetching of network requests.

## Getting started

### Installation

#### Import library

* Go to this repository [releases](https://github.com/S2-group/NAPPA/releases) page.
* Download the latest version of the library from `Prefetching Library v1.x`.
* Open Android Studio.
* Click on `File > New > New Module`.
* Click on `Import .JAR/.AAR Package` then click `Next`.
* Enter the location of the compiled AAR or JAR file then click `Finish`.

#### Configur Gradle

Make sure the library is listed at the top of your `settings.gradle` file, as shown here for a library named "android_prefetching_lib": 

```gradle
include ':app', :android_prefetching_lib
```

Open the app module's `build.gradle` file and add a new line to the dependencies block as shown in the following snippet: 

```gradle
dependencies {
    implementation project(":my-library-module")
}
```

Click Sync Project with Gradle Files.

### Usage

## Implementing a custom prefetching strategy