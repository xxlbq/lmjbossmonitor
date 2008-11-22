#!/bin/sh

# ---------------------------------------------------
# a product of Xinyang PAN
# ---------------------------------------------------

JAVA_HOME=/usr/local/jdk1.5
if [ "x$JAVA_HOME" = "x" ]; then
    echo "[ERROR]:JAVA_HOME is not Found"
fi

PATH=${JAVA_HOME}/bin:/usr/bin/:/bin

DIRNAME=`dirname $0`
JBOSS_MONITOR_HOME=${DIRNAME}/..

#echo "================================================================================================================"
#echo "   JAVA_HOME = ${JAVA_HOME}"
#echo "   DIRNAME = ${DIRNAME}"
#echo "   JBOSS_MONITOR_HOME = ${JBOSS_MONITOR_HOME}"
#echo "   JBOSS_MONITOR_HOME: ${JBOSS_MONITOR_HOME}"
#echo "================================================================================================================"

LIBPATH=${JBOSS_MONITOR_HOME}/lib/
CLASSPATH=${JAVA_HOME}/jre/lib/:${JAVA_HOME}/lib/
CLASSPATH=${CLASSPATH}:${JBOSS_MONITOR_HOME}/jboss_monitor.jar
CLASSPATH=${CLASSPATH}:${LIBPATH}activation.jar
CLASSPATH=${CLASSPATH}:${LIBPATH}commons-collections.jar
CLASSPATH=${CLASSPATH}:${LIBPATH}commons-configuration-1.3.jar
CLASSPATH=${CLASSPATH}:${LIBPATH}commons-lang.jar
CLASSPATH=${CLASSPATH}:${LIBPATH}commons-logging.jar
CLASSPATH=${CLASSPATH}:${LIBPATH}jbossall-client.jar
CLASSPATH=${CLASSPATH}:${LIBPATH}mail.jar
CLASSPATH=${CLASSPATH}:${LIBPATH}spring.jar
CLASSPATH=${CLASSPATH}:${LIBPATH}log4j-1.2.13.jar

configPath=${JBOSS_MONITOR_HOME}/conf/

LANG=en



echo "================================================================================================================"

date +'%F %T'

java -DconfigPath=${configPath} -classpath ${CLASSPATH} cn.bestwiz.tools.jboss.mqclear.main.QueueClearMain $1 $2
