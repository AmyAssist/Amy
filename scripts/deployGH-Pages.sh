#!/bin/bash
function doDependencies {
    mkdir -p "gh-pages/dependencies/$1"
    pushd "$1"
    mvn project-info-reports:dependencies -DskipTests=true
    popd
    mv -T "$1/target/site" "gh-pages/dependencies/$1"
    pushd "gh-pages/dependencies/$1"
    mv dependencies.html index.html
    popd
}

doDependencies "core"

for D in plugins/*; do
    if [ -d "${D}" ]; then
        doDependencies "${D}"
    fi
done

