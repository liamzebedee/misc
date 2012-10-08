#!/bin/sh
# This file compiles against .net Vers.1 and 2 and moves the results in the distribution
if test -r bin/emonicinformator-nant.exe ; then
  rm bin/emonicinformator-nant.exe
fi
nant -buildfile:build-nant.xml
cp bin/emonicinformator.exe  ~/workspace_new/emonic.base/net/emonicinformator.exe
cp bin/emonicinformator.exe.mdb  ~/workspace_new/emonic.base/net/emonicinformator.exe.mdb
cp bin/emonicinformator-2.0.exe  ~/workspace_new/emonic.base/net/emonicinformator-2.0.exe
cp bin/emonicinformator-2.0.exe.mdb  ~/workspace_new/emonic.base/net/emonicinformator-2.0.exe.mdb
