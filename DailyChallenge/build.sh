#!/bin/sh

DIRNAME=`dirname $0`
export APPLICATION_HOME=`cd $DIRNAME; pwd`
echo "APPLICATION_HOME: "$APPLICATION_HOME

mvn -DforkMode=never -DQUESTION_TYPE_HOME="$APPLICATION_HOME/question_types" clean install assembly:assembly -Pdist
