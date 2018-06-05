#!/bin/bash
mkdir gh-pages
mvn project-info-reports:dependencies -DskipTests=true
mv target/site gh-pages/dependencies
mv gh-pages/dependencies/dependencies.html gh-pages/dependencies/index.html
