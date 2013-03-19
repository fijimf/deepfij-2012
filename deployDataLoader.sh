#!/bin/sh

cd deepfij-scrape

mvn clean install dependency:copy-dependencies

cd target

mv deepfij-scrape-*.jar deepfij-scrape.jar

ssh fijimf@fijimf.com "rm ~/scraper/lib/*.jar"
ssh fijimf@fijimf.com "rm ~/scraper/bin/*"

scp ../bin/* fijimf@fijimf.com:~/scraper/bin
scp deepfij-scrape.jar fijimf@fijimf.com:~/scraper/lib
scp dependency/*.jar fijimf@fijimf.com:~/scraper/lib
