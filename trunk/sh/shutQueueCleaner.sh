#!/bin/sh

# ---------------------------------------------------
# a product of Xinyang PAN
# ---------------------------------------------------

PATH=/bin:/usr/bin/:/bin


ps axu|grep java|grep cn.bestwiz.tools.jboss.mqclear.main.QueueClearMain |awk '{print $2}' |xargs kill -9