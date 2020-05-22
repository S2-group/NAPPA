# NAPPA Android Prefetch Library

This project contains a library for Android to support prefetching of network requests.

## Getting started

### Installation

#### Import library

* Go to this repository [releases](https://github.com/S2-group/NAPPA/releases) page.
* Download the latest version of the library from `NAPPA Prefetching Library v1.x`.
* Open Android Studio.
* Click on `File > New > New Module`.
* Click on `Import .JAR/.AAR Package` then click `Next`.
* Enter the location of the compiled AAR or JAR file then click `Finish`.

#### Configure Gradle

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

Import the library:

```java
import nl.vu.cs.s2group.PrefetchingLib;
``` 

Encapsulate the creation of [OkHttpClient](https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/) instances:

```java
// Default usage
OkHttpClient client = PrefetchingLib.getOkHttp(new OkHttpClient());

// Using builder
OkHttpClient.Builder builder = new OkHttpClient.Builder();
// add build configurations
OkHttpClient client = PrefetchingLib.getOkHttp(builder.build());
```

Inform the library about the usage of Android [Intent Extras](https://developer.android.com/reference/android/content/Intent):

```java
PrefetchingLib.notifyExtras(intent.getExtras());
startActivity(intent); 
```

Inform the library when navigating between Android [Activity](https://developer.android.com/reference/android/app/Activity):

```java
@Override
protected void onResume() {
    super.onResume();
    PrefetchingLib.setCurrentActivity(this);
}
```

For existing projects, use the [NAPPA Plugin for Android Studio](../Android-Studio-Plugin) to automatically make these changes and enable NAPPA in the project.


### Available prefetching strategies

NAPPA does not impose a specific prefetching strategy, but rather allows for a modular specification of prefetching strategies.
The default strategy uses a greedy-based algorithm.

#### Greedy

The Greedy appraoch determines which activity successors can benefit the most from prefetching by traversing the ENG tree using a "weight" factor.
It recursively consider all successors that can "add value" by prefetching it.
The weight is defined as the score that has been calculated in the previous iteration.
Therefore, with each recursive iteration, the weight score decreases. 
There is a fixed threshold to limit the number of candidate URLs to prefetch.

## Modifying the library

### Required Tools

* [Gradle](https://gradle.org/) v5.6.4
* [Android Studio](https://developer.android.com/studio)

### Implementing a custom prefetching strategy

The prefetching strategies are implemented in the [nl.vu.cs.s2group.prefetch](android_prefetching_lib/src/main/java/nl/vu/cs/s2group/prefetch) package.
These can be used as references on how to implement a new strategy.

To define a new strategy, you must implement the [PrefetchStrategy](android_prefetching_lib/src/main/java/nl/vu/cs/s2group/prefetch/PrefetchStrategy.java) interface and override the method `getTopNUrlToPrefetchForNode`.

After implementing the strategy, it cam be enabled by ...
TODO &mdash; analyse prefetch library to understand how a prefetching solution is selected and the describe the steps here

After implementing a strategy, please consider sharing it by creating a PR to the official [NAPPA](https://github.com/S2-group/NAPPA) repository.

### Create a new build 

To create a new build with the modifications implemneted in the library, click on `Build > Make Project`.
Android Studio places new builds in the directory [android_prefetching_lib/build/outputs/aar/](android_prefetching_lib/build/outputs/aar).
To use this build, repeat the `Import library` instructions in the `Installation` section.