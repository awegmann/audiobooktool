audiobooktool
=============

Tool for creating and converting audiobooks.

Contents
--------

This project contains a few wrappers written in Groovy around the native tools mp4v2 and ffmpeg. 
With this tools it's possible to recode audio (ffmpeg) and modify meta info on mp4 files such as
chapter info, embedded cover art, tags and much more.

Native Tool Versions
--------------------

The native tools versions currently used by myself are ffmpeg 2.0.1 and mp4v2 version 2.0.0.


Building
--------

Audiobooktools uses gradle as build system. To completely build the tool just run *gradle build* which 
starts downloading of libraries, compile and test.

