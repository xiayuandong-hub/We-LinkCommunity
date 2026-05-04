#!/bin/bash -e
ps -ef | grep we-link.jar | grep -v grep | cut -c 9-15 | xargs kill
