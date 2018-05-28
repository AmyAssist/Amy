#!/bin/bash
cd $(dirname $0)
mvn project-info-reports:dependencies
mv target/site dependencies
mv dependencies/dependencies.html dependencies/index.html
