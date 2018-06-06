#!/bin/bash
mkdir gh-pages
cd dependency-export
mvn project-info-reports:dependencies -DskipTests=true
mv target/site ../gh-pages/dependencies
cd ..
mv gh-pages/dependencies/dependencies.html gh-pages/dependencies/index.html
