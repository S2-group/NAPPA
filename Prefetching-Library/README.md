![Prefetching Library](https://github.com/VU-Thesis-2019-2020-Wesley-Shann/NAPPA/workflows/Prefetching%20Library/badge.svg)

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

Make sure the library is listed at the top of your `settings.gradle` file, as shown here for a library named "nappa-prefetching-library": 

```gradle
include ':app', :nappa-prefetching-library
```

Open the app module's `build.gradle` file and add a new line to the dependencies block as shown in the following snippet: 

```gradle
dependencies {
    implementation project(":nappa-prefetching-library")
}
```

Click `Sync Project with Gradle Files`.

#### Import library dependencies

Import the following libraries to your project

```gradle
implementation "org.bitbucket.cowwoc:diff-match-patch:1.1"
implementation "com.squareup.okhttp3:okhttp:3.14.8"
implementation "androidx.room:room-runtime:2.2.5"
implementation "androidx.lifecycle:lifecycle-extensions:2.2.0"
```

### Usage

Import the library:

```java
import nl.vu.cs.s2group.nappa.PrefetchingLib;
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

The Greedy approach determines which activity successors can benefit the most from prefetching by traversing the ENG tree using a "weight" factor.
It recursively considers all successors that can "add value" by prefetching it.
The weight is defined as the score that has been calculated in the previous iteration.
Therefore, with each recursive iteration, the weight score decreases. 
There is a fixed threshold to limit the number of candidate URLs to prefetch.

## Modifying the library

### Required Tools

* [Gradle](https://gradle.org/) v5.6.4
* [Android Studio](https://developer.android.com/studio)

### Implementing a custom prefetching strategy

The prefetching strategies are implemented in the [nl.vu.cs.s2group.nappa.prefetch](android_prefetching_lib/src/main/java/nl/vu/cs/s2group/prefetch) package.
These can be used as references on how to implement a new strategy.

To define a new strategy, you must implement the [PrefetchStrategy](android_prefetching_lib/src/main/java/nl/vu/cs/s2group/prefetch/PrefetchStrategy.java) interface and override the method `getTopNUrlToPrefetchForNode`.

After implementing the strategy, it can be enabled by ...
TODO &mdash; analyse prefetch library to understand how a prefetching solution is selected and the describe the steps here

After implementing a strategy, please consider sharing it by creating a PR to the official [NAPPA](https://github.com/S2-group/NAPPA) repository.

### Create a new build 

To create a new build with the modifications implemented in the library, click on `Build > Make Project`.
Android Studio places new builds in the directory [android_prefetching_lib/build/outputs/aar/](android_prefetching_lib/build/outputs/aar).
To use this build, repeat the `Import Library` instructions in the `Installation` section.

### Debug the application

#### Debug unhandled exceptions outside the main thread

Weird behaviour can be caused by running into an exception in the secondary threads.
By default, these don't seem to be logged in the console, which makes difficult to identify them.

To make sure that all unhandled exceptions outside the main thread are logged do the following steps: 

* Open the Breakpoints dialog (`Run > View Breakpoints` or `Ctrl + Shift + F8`)
* In the left panel click on `Java Exception Breakpoint > Any Exception`
* Check the option `Enabled`
* Uncheck the option `Suspend` (unless you want to debug values there)
* Uncheck the option `Caught exception` (the launcher and class loader exceptions are caught and handled internally by the JVM.)
* Run the application in debug mode 
* Any unhandled exception will be logged in the `Debug` window

#### Debug the database data

##### Using DB Browser for SQLite

This is an external client for reading database files.
Download it at [sqlitebrowser](https://sqlitebrowser.org/dl/).

In Android Studio, go to:

```
View > Tool Windows > Device File Explorer
```

This will open a panel with the emulator files. 
To access this panel the emulator must be first launched. 
In this panel, go to:

```
data > data > [application package name] > databases 
```

You should see 3 files:

* pf_db
* pf_db-shm
* pf_db-wal

Select all 3, right click and `save as`.
Rename `pf_db` to `pf_db.db`
Open DB Browser for SQLite.
Click on `Open Database`, go to the directory you saved the files and select `pf_db.db`.
Click in the `Browser Data` tab.
You can now verify the data saved by the NAPPA Library. 

##### Using Room

You can use the defined DAO Room classes or create your own functions
