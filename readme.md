Slideshow
=========

A very basic photo slideshow application.

Features
--------
* Will search a directory recursively for images
* Resizes images on the fly to fit on the screen
* Configurable delay between images
* Overlays each image with timestamp, caption, and Windows descriptive tags (if present)
* Can hide manufacturer default captions
* Allows you to skip forward and back

To run
------
* Install a JRE if you don't already have one
* Download Slideshow.zip (https://bitbucket.org/jmetcher/slideshow/downloads/Slideshow.zip) and unzip into a location of your choice
* Edit config.properties
* Run `run.bat` (Windows) or `run.sh` (Linux)

The run scripts assume you have java on the path.  If not, edit the run scripts to have the location of your java executable.

Configuration options
---------------------
Edit config.properties to set options as follows:

**source** 
The path to the folder containing your photos.  Can be absolute or relative to the location of Slideshow.jar.
You do not need to quote the path even if it contains spaces.
On Windows, use / as the path separator.
The path is case sensitive or not, according to the native behaviour of the OS.
Slideshow will recurse down into subfolders.
At present, you can not specify multiple folders

**extensions**
A comma-separated list of file extensions.  Any files in `source` that don't have one of these extensions will be ignored.
You can specify extensions in upper or lower case, and Slideshow will recognise the opposite case as well.  Mixed case extensions won't be recognized.  For example, if you specify `jpeg`, Slideshow will load both `.jpeg` and `.JPEG` files, but not `.Jpeg`.  Yes, this is a bug.

**delay**
The time in seconds between photos

**shuffle**
`true` or `false`

**exclude_captions**
A comma-separated list of photo captions that will not be displayed.  That is, the photo will still be displayed, but the caption won't.  This is to get around the habit of camera manufacturers of supplying a default caption of THEIR OWN BRAND IN UPPERCASE.

Configuration options are not hot-reloaded.  If you edit the config file, close and restart Slideshow.

Commands
--------
Click anywhere = exit

esc key = exit

left arrow (non-numpad) = previous photo

right arrow (non-numpad) = next photo

To build
---------
* Clone this repo
* Run `gradlew distZip`
* A zipped distribution will be placed in build/distributions

To build in Eclipse
-------------------
* Run `gradlew eclipse`
* Import the project into Eclipse
* Add src/main/dist to the Eclipse classpath

Note that the Eclipse build is only for edit/run/debug within Eclipse.  To create the distribution file, you'll need to use gradle.

Known issues
------------

* Mixed case file extensions aren't recognized
* Occasionally Slideshow fails to get the current screensize and gets a 1x1 window instead.  If you don't see any images within a few seconds, Esc out and rerun.  This happens fairly regularly when running from Eclipse
* Errors are not handled gracefully.  If anything is not where Slideshow expects it to be (e.g. the config file), you'll get at least a stack trace and probably an abrupt exit as well.  Do send me the stack trace if this happens :)
* Slideshow keeps an in-memory list of the full paths to all images in the specified folder.  If you give it a filesystem with millions of files, it will probably run out of memory.
* Likewise, Slideshow does not do any sanity checks on the size of images.  It works fine with my 12MP images, but you could give it arbitrarily large images and cause problems.

Disclaimer
----------
This is an exploratory coding project and as such knowingly ignores whole slabs of established best practices (such as unit testing).

Slideshow is not production code and has not been tested on a wide range of JREs, operating systems and images.  I have run it on Ubuntu 14.04, Windows 7 and Windows 8 with the latest Java 7 JRE.

Acknowledgements
----------------
Thanks to Drew Noakes for his excellent [Metadata Extractor](https://drewnoakes.com/code/exif/)

Thanks to Sun and Oracle for doing literally _all_ the hard bits.  Nearly everything I thought might be tricky was already there in the Java standard libraries.

Licensing
---------
You're free to use this code with the terms of the Apache License 2. Please send me a short email to let me know if you find it useful. If you make interesting changes, either mail me or create a pull request and I'll review them for inclusion. 

Copyright Jaime Metcher 2015