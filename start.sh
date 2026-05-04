#!/bin/bash -e
java -jar we-link.jar --spring.profiles.active=prod > log.file 2>&1 &
