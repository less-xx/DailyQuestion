#!/bin/sh
###########################################################################
# Startup/Shutdown script for the Daily Challenge
###########################################################################

#
# This script can be used as a Linux boot script in init.d.
# You'll need to configure APPLICATION_HOME directly.
#
# APPLICATION_HOME=/usr/local/daily-challenge
# export APPLICATION_HOME

DIRNAME=`dirname $0`
APPLICATION_HOME=`cd $DIRNAME/..; pwd`
echo "APPLICATION_HOME: "$APPLICATION_HOME

if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
		JAVA="$JAVA_HOME/bin/java"
    else
		JAVA="java"
    fi
fi

if [ "x$QUESTION_TYPE_HOME" = "x" ]; then
	QUESTION_TYPE_HOME="$APPLICATION_HOME/question-types"
	if [ ! -f $QUESTION_TYPE_HOME ]; then
		mkdir $QUESTION_TYPE_HOME
    	echo "Using default question type home: $QUESTION_TYPE_HOME"
	fi
fi
echo "QUESTION_TYPE_HOME: "$QUESTION_TYPE_HOME

JVM_OPT="-Xms16m -Xmx64m"
#DEBUG_OPT="-Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"

PROP_OPT="-DAPPLICATION_HOME=$APPLICATION_HOME -DQUESTION_TYPE_HOME=$QUESTION_TYPE_HOME -Dlogback.configurationFile=$APPLICATION_HOME/conf/logback.xml"
GC_OPT="-XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=80"
GC_LOG_OPT="-Xloggc:$APPLICATION_HOME/logs/gc.log"
CLASSPATH=$APPLICATION_HOME/lib/teapotech/teapotech-bootstrap.jar

#
# pid file, default $APPLICATION_HOME/logs/daily-challenge.pid
#
pid_file=$APPLICATION_HOME/logs/daily-challenge.pid

start()
{
        echo "Starting Daily Challenge ......"
        if [ -s $pid_file ]
        then
                pid=`/bin/cat $pid_file`

                if [ $pid ]
                then
                        fpid=`ps -ef |grep java |tr -s ' '|cut -d ' ' -f 2 |grep $pid`
                        if [ "$pid" = "$fpid" ]
                        then
                                echo "The process $pid is still alive, make sure you had started the server before."
                                echo "Server start failed , to restart the server, please use: $0 restart "
                                exit 1
                        fi
                fi
        fi

        nohup $JAVA -server $JVM_OPT $GC_OPT $GC_LOG_OPT $PROP_OPT -classpath $CLASSPATH org.teapotech.service.bootstrap.AppLoader org.teapotech.utils.jetty.JettyBootstrap startup > /dev/null &
        echo $! > $pid_file

}
debug()
{
	echo "Starting Daily Challenge in DEBUG mood......"
	$JAVA $JVM_OPT $DEBUG_OPT $GC_OPT $GC_LOG_OPT $PROP_OPT -classpath $CLASSPATH org.teapotech.service.bootstrap.AppLoader org.teapotech.utils.jetty.JettyBootstrap startup
}
stop()
{
        echo "Stopping Daily Challenge ......"
        pid=`cat $pid_file`

        if [ $pid ]
        then
                fpid=`ps -ef |grep java |tr -s ' '|cut -d ' ' -f 2 |grep $pid`
                if [ "$pid" = "$fpid" ]
                then
                        kill $pid
                        echo "Daily Challenge stoped."
                else
                        echo "Daily Challenge is not running."
                fi
        else
                echo "The server had not started"
        fi

        if [ -f $pid_file ]
        then
                if [ -w $pid_file ]
                then
                        rm $pid_file
                fi
        fi
}

restart()
{
        stop
        sleep 10
        start
}

case "$1" in
start)
        start
        ;;
debug)
		debug
		;;
stop)
        stop
        ;;
restart)
        restart
        ;;        
*)
        echo $"Usage: $0 {start|stop|restart|debug}"
        exit 1
esac
