#!/bin/bash
absPath=$(readlink -f "$0")
absDir=$(dirname "$absPath")

projectDir=$(readlink -f "$absDir/..") 

echo "$projectDir"

find "$projectDir" -name "*.java" -exec "$absDir/fixSingleFileComment.sh" {} \;
