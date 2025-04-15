#!/bin/sh

# Build inside docker container
docker run -v /var/run/docker.sock:/var/run/docker.sock -v $(pwd):/src -v $HOME/.m2:/root/.m2  maven:3-ibm-semeru-21-jammy /src/build/maven.sh
