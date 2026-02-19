#!/bin/bash

if docker pull kvalitetsit/medcom-video-api-documentation:latest; then
    echo "Copy from old documentation image."
    docker cp $(docker create kvalitetsit/medcom-video-api-documentation:latest):/usr/share/nginx/html target/old
fi
