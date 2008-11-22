#!/bin/sh

# ---------------------------------------------------
# a product of Xinyang PAN
# ---------------------------------------------------

JAVA_HOME=/usr/local/jdk1.5
if [ "x$JAVA_HOME" = "x" ]; then
    echo "[ERROR]:JAVA_HOME is not Found"
fi

PATH=${JAVA_HOME}/bin:/usr/bin/:/bin

PANXY=pp

echo "config.sh ${PANXY}"

export PANXY
