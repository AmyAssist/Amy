#!/bin/bash
if [ $# -le 0 ] ;then echo "Need at least one argument." ;exit ;fi
cd $(dirname $0)/..
find . -name "*.settings" -exec cp "${@}" {} \;
