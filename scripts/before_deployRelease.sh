#! /bin/bash
mkdir release-upload
cd build/plugins
zip -r ../../release-upload/plugins.zip ./
cd -
mv amy-master-node/target/amy-master-node.jar release-upload/amy-master-node.jar
