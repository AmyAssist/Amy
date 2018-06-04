#!/bin/bash
SEPERATOR="package de.unistuttgart.iaas.amyassist.amy"

absPath=$(readlink -f "$0")
absDir=$(dirname "$absPath")

search="*$SEPERATOR"
replace=$(cat "$absDir/correctFileComment.txt")
replace=$(printf "$replace\n\n$SEPERATOR")

buffer=$(cat "$1")

buffer="${buffer##$search}"

buffer="$replace$buffer"

echo "$buffer" > "$1"
