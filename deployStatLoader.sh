#!/bin/sh

cd deepfij-model

mvn clean install dependency:copy-dependencies

cd target

mv deepfij-model-*.jar deepfij-model.jar

ssh fijimf@fijimf.com "rm ~/statter/lib/*.jar"
ssh fijimf@fijimf.com "rm ~/statter/bin/*"

scp ../bin/stat-loader.sh fijimf@fijimf.com:~/statter/bin
scp deepfij-model.jar fijimf@fijimf.com:~/statter/lib
scp dependency/*.jar fijimf@fijimf.com:~/statter/lib
