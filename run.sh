#!/bin/sh

CP=${CP:-src/main/resources}

for l in build/libs/*.jar 
do 
	CP=$CP:$l 
done 

for l in libs/* 
do 
	CP=$CP:$l 
done 

for l in external-libs/jlapack-0.8/*
do 
	CP=$CP:$l 
done 


JAVA_ARGS="com.rc.Main"

java -cp $CP \
	-Xmx2g \
	-XX:+UseG1GC \
	-XX:+UseStringDeduplication \
	-XX:MaxGCPauseMillis=200 \
	${JAVA_ARGS} $@
