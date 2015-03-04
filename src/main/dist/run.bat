pushd %cd%
cd %~dp0
java -Djava.util.logging.config.file=./logging.properties -cp .;*;lib/* au.id.lagod.slideshow.Runner
popd