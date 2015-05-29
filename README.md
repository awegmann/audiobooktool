audiobooktool
=============

[![Join the chat at https://gitter.im/awegmann/audiobooktool](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/awegmann/audiobooktool?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Tool for creating and converting audiobooks.

Contents
--------

This project contains a few wrappers written in Groovy around the native tools mp4v2 and ffmpeg. 
With this tools it's possible to recode audio (ffmpeg) and modify meta info on mp4 files such as
chapter info, embedded cover art, tags and much more.

Native Tool Versions
--------------------

The native tools versions currently used by myself are ffmpeg 2.0.1 and mp4v2 version 2.0.0.

* mp4v2 can be found here: http://code.google.com/p/mp4v2/
* ffmpeg can be found here: http://www.ffmpeg.org/



Building
--------

Audiobooktools uses gradle as build system. To completely build the tool just run *gradle build* which 
starts downloading of libraries, compile and test.

