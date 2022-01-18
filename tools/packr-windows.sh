#!/usr/bin/env sh

if [ "$#" -ne 3 ]; then
	echo "Usage: $0 [path to packr.jar] [path to JDK 8 archive] [path to desktop.jar]"
	exit
fi

INITIAL_DIR=$(dirname $(readlink -f "$0"))

PACKR_PATH=$1
JDK_PATH=$2
DESKTOP_JAR_PATH=$3

java -jar "$PACKR_PATH" \
     --platform windows64 \
     --jdk "$JDK_PATH" \
     --classpath "$DESKTOP_JAR_PATH" \
     --output "$INITIAL_DIR/../dist/windows/" \
     "$INITIAL_DIR/libgdx-packr.json"
