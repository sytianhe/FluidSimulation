By 
Michael Flashman, mtf53
and 
Tianhe Zhang, tz249

Feb 2013

Starter Code provided by Doug James 

DEPENDENCIES:
lib/gluegen-rt.jar
lib/jogl-all.jar
lib/vecmath.jar

RUN:

KEY:
'f' : Display the force field of the scene
'v': Display the velocity field of the scene


COMPILE VIDEO:
$ cd frames
$ ffmpeg -y -i "export0-%05d.png" output.mov
