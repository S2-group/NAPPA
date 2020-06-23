#!/bin/sh
# This script expects to be run in this project (Weather and News app) root directory

PROJECT_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

# Define paths for this project
IMPORTED_LIBRARY_PATH="app/libs/aars/"
LIBRARY_NAME="nappa-prefetching-library.aar"

# Define paths for the Prefetching Library project
LIBRARY_SOURCE_PROJECT_PATH="../../Prefetching-Library/"
LIBRARY_SOURCE_BUILD_PATH="${LIBRARY_SOURCE_PROJECT_PATH}android_prefetching_lib/build/outputs/aar/"
LIBRARY_DEBUG_NAME="android_prefetching_lib-debug.aar"

# Build the library
cd $LIBRARY_SOURCE_PROJECT_PATH || exit
echo "Building NAPPA Prefetching Library"
./gradlew build
echo "NAPPA Library finished building"

# Move the new AAR file to the library directory
echo "Updating imported library with new version"
cd "$PROJECT_PATH" || exit
[ -f "${IMPORTED_LIBRARY_PATH}${LIBRARY_NAME}" ] && rm "${IMPORTED_LIBRARY_PATH}/${LIBRARY_NAME}"
mv "${LIBRARY_SOURCE_BUILD_PATH}${LIBRARY_DEBUG_NAME}" "${IMPORTED_LIBRARY_PATH}${LIBRARY_NAME}"
echo "Import updated"