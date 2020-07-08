#! /bin/sh

export DOC_FILES=/etc/nginx/html/*.yaml

echo "Running set version"
/kit/setVersion.sh
echo "Running set servers"
/kit/setServers.sh

echo "Sets env URLS to list of versions"
export URLS=$(cat /kit/env)

echo "Restarting nginx"
/usr/share/nginx/run.sh