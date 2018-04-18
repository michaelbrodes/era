#!/usr/bin/env bash
scp -r deploy s002716@my-assignments.isg.siue.edu:~/
ssh s002716@my-assignments.isg.siue.edu './setup.sh'