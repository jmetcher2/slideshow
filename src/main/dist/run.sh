#!/bin/bash
cd "${BASH_SOURCE%/*}" || return
java -Djava.util.logging.config.file=./logging.properties -cp .:./*:lib/* au.id.lagod.slideshow.Runner
