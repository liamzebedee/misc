#!/bin/sh
FORREST_CMD="/usr/local/apache-forrest-0.8/bin/forrest"
# build the documentation
# use the location of this script to infer $SCRIPT_OME
whoami=`basename $0`
whereami=`echo $0 | sed -e "s#^[^/]#\`pwd\`/&#"`
whereami=`dirname $whereami`
# Resolve any symlinks of the now absolute path, $whereami
realpath_listing=`ls -l $whereami/$whoami`
case "$realpath_listing" in
    *-\>\ /*)
      realpath=`echo $realpath_listing | sed -e "s#^.*-> ##"`
      ;;
    *-\>*)
      realpath=`echo $realpath_listing | sed -e "s#^.*-> #$whereami/#"`
      ;;
    *)
      realpath=$whereami/$whoami
      ;;
esac
SCRIPT_HOME=`dirname "$realpath"`/

# cp the xml-files dor manual build to the right one
cp ${SCRIPT_HOME}/src/documentation/skinconf-manual.xml  ${SCRIPT_HOME}/src/documentation/skinconf.xml
cp ${SCRIPT_HOME}/src/documentation/content/xdocs/site-manual.xml ${SCRIPT_HOME}/src/documentation/content/xdocs/site.xml
# clean the build/site- dir
rm -rf ${SCRIPT_HOME}/build/site/*
# Build the documentation
$FORREST_CMD
# Copy the documentation to the right place
rm -rf  ${SCRIPT_HOME}/../emonic.base/html/*
cp  -r ${SCRIPT_HOME}/build/site/  ${SCRIPT_HOME}/../emonic.base/html/
