#!/usr/bin/env bash
PID=$(ps aux | grep era.jar | grep -v grep | awk '{print $2}')
kill -9 $PID
java -jar era.jar --app-port 8080 \
     --db-name dev --db-port 3306 \
     --db-user s002716 --db-password fakepassword \
     --cas-enabled true &
disown -h %1
