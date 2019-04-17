INSTRUCTIONS

Build plugin with

./gradlew buildPlugin

The plugin binaries can be found in 

build/distributions

Install Plugin in Android Studio:

File -> Settings -> Plugins -> Install plugin from disk

Restart Android Studio.

In the main bar you will found a new Menu called "Prefetching"

Use "Spot intent" to instrument intents in order to get Intent arguments
Use "Spot OkHttp" to instrument OkHttp Builder in order to track HTTP/GET requests
use "Add prefetching" to instrument Activities in orer to get the navigation graph
