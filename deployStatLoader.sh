#!/bin/sh

cd deepfij-model

mvn clean install dependency:copy-dependencies

cd target

mv deepfij-model-*.jar deepfij-model.jar
