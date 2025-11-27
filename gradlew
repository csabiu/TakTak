#!/bin/sh
# Gradle wrapper script
APP_NAME="Gradle"
APP_BASE_NAME=${0##*/}
MAX_FD=maximum

warn () {
    echo "$*"
} >&2

die () {
    echo "$*"
    exit 1
} >&2

if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD=$JAVA_HOME/jre/sh/java
    else
        JAVACMD=$JAVA_HOME/bin/java
    fi
else
    JAVACMD=java
fi

set -- \
    "-Dorg.gradle.appname=$APP_BASE_NAME" \
    -classpath "$(dirname "$0")/gradle/wrapper/gradle-wrapper.jar" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"

exec "$JAVACMD" "$@"
