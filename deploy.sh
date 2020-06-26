#!/bin/bash

if [ $(docker ps -f name=worker -q | wc -l) -eq 1 ];then
	echo "container war already running, stopping container"
        test=$(docker ps -f name=worker -q)
        docker stop $test
	docker rm $test
	echo "container stopped"
fi
docker run --detach --name worker --volume /var/jenkins_home/workspace/SaaSBot_pl:/home/ openjdk:14 java -Djava.library.path=/home/lib -jar /home/target/ProvaBot-1.0-SNAPSHOT-jar-with-dependencies.jar
