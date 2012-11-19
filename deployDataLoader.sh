#!/bin/sh

cd deepfij-scrape

mvn clean install dependency:copy-dependencies

cd target

mv deepfij-scrape-*.jar deepfij-scrape.jar

ftp -inv fijimf.com <<!
user ${FTP_USER} ${FTP_PASS}
binary
LITERAL PASV
cd scraper/lib
mdel *.jar
put deepfij-scrape.jar
mput dependency/*.jar
quit
!
