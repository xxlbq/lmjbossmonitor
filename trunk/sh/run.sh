#!/bin/sh

if [ "$OS" = "Windows_NT" ]; then
        ARGS=""
else
        MIN_MEMORY=64m
        MAX_MEMORY=64m
        MIN_NEW_SIZE=8m
        MAX_NEW_SIZE=8m
        ARGS="-Xms${MIN_MEMORY} -Xmx${MAX_MEMORY} -XX:NewSize=${MIN_NEW_SIZE} -XX:MaxNewSize=${MAX_NEW_SIZE}"
fi

tcsh -c "java -DconfigPath=/jhfapp/app/mqMonitorHa/conf ${ARGS} cn.bestwiz.tools.jboss.mqtest.main.MqMonitor&"
