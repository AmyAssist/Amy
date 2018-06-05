#!/bin/bash
mkdir gh-pages
mvn install
mvn project-info-reports:dependencies
mv target/site gh-pages/dependencies
mv gh-pages/dependencies/dependencies.html gh-pages/dependencies/index.html
