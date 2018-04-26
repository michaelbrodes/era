#!/usr/bin/env bash
if [ "$1" == 'wipe' ]
then
    echo 'Wiping the database and re-running the web application'
    scp -r deploy s002716@my-assignments.isg.siue.edu:~/
    ssh s002716@my-assignments.isg.siue.edu 'chmod +x deploy/setup.sh'
    ssh s002716@my-assignments.isg.siue.edu './setup.sh'
elif [ "$1" == 'deploy' ] 
then 
    echo 'Re-running the web application'
    scp -r deploy s002716@my-assignments.isg.siue.edu:~/
    ssh s002716@my-assignments.isg.siue.edu 'chmod +x deploy/start.sh'
    ssh s002716@my-assignments.isg.siue.edu './deploy/start.sh'
else
    echo 'Please enter another parameter. For example, "./deploy.sh deploy" will start running the website.'
fi