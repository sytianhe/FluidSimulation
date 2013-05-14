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
There are three main programs can be run for different simulations.

smoke.java: ordinary main function runs smoke coupling with rigid body simulation
seedDrop.java: randomly generate shapes and drop them
smokeWithColor: run colorful smoke by running fluid solver on RGB channels

KEY:
'f' : Display the force field of the scene
'v': Display the velocity field of the scene


COMPILE VIDEO:
$ cd frames
$ ffmpeg -y -i "export0-%05d.png" output.mov
