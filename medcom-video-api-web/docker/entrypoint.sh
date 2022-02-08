#! /bin/bash
if [ "$CONTAINER_TIMEZONE" = "" ]
then
   echo "Using default timezone"
else
	TZFILE="/usr/share/zoneinfo/$CONTAINER_TIMEZONE"
	if [ ! -e "$TZFILE" ]
	then 
    	echo "requested timezone $CONTAINER_TIMEZONE doesn't exist"
	else
		cp /usr/share/zoneinfo/$CONTAINER_TIMEZONE /etc/localtime
		echo "$CONTAINER_TIMEZONE" > /etc/timezone
		echo "using timezone $CONTAINER_TIMEZONE"
	fi
fi

if [[ -z $CONTEXT ]]; then
	echo "Using default context: /"
	export SERVER_SERVLET_CONTEXT_PATH=/
else
	echo "Using context: $CONTEXT"
	export SERVER_CONTEXT_PATH=$CONTEXT
	export SERVER_SERVLET_CONTEXT_PATH=$CONTEXT
fi

if [[ -z $SERVER_PORT ]]; then
	echo "Using default port (8080)"
else
	export server_port=$SERVER_PORT
fi

if [[ -z $LOG_LEVEL ]]; then
  echo "Default LOG_LEVEL = INFO"
  export LOG_LEVEL=INFO
fi

if [[ -z $LOG_LEVEL_FRAMEWORK ]]; then
  echo "Default LOG_LEVEL_FRAMEWORK = INFO"
  export LOG_LEVEL_FRAMEWORK=INFO
fi

if [[ -z $LOG_LEVEL_PERFORMANCE ]]; then
  echo "Default LOG_LEVEL_PERFORMANCE = WARNING"
  export LOG_LEVEL_PERFORMANCE=WARNING
fi


if [[ -z $CORRELATION_ID ]]; then
  echo "Default CORRELATION_ID = correlation-id"
  export CORRELATION_ID=correlation-id
fi

if [[ -z $logging_config ]]; then
  echo "Default logging_config=/app/logback-spring.xml"
  export logging_config="/home/appuser/logback-spring.xml"
fi

envsubst < /home/appuser/configtemplates/logback.xml > /home/appuser/logback-spring.xml

java $JVM_OPTS -jar medcom-video-api-web.jar
