#!/bin/sh

# This file compiles the web service and moves all needed stuff in the
# emonic.debugger/services/ directory.

# Configure the full path where your emonic.debugger folder is:
TARGET=/home/hkrapfenbauer/workspace/emonic.debugger

rm -f bin/EmonicWebservice.dll
nant -buildfile:DebugFrontend.build
mkdir $TARGET/services
mkdir $TARGET/services/bin
cp bin/EmonicWebservice.dll $TARGET/services/bin/
cp bin/mdb.exe $TARGET/services/bin/
cp EmonicDebuggingService.asmx $TARGET/services/
cp README.license $TARGET/services/
echo -e "\n*** Building for release: DONE. ***\n"
