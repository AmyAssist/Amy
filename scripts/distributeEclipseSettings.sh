#!/bin/bash
cd $(dirname $0)/..
find . -name "*.settings" -exec cp -rT .settings {} \;
