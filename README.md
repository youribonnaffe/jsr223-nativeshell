jsr223-bash
===========

A Bash Script Engine for Java implementing JSR 223

Usage
-----

Simply add the JAR to your classpath and follow the [Java Scripting Programmer's guide](http://docs.oracle.com/javase/6/docs/technotes/guides/scripting/programmer_guide/index.html)

Build
-----

Run gradlew script, it will produce a JAR file in build/libs

How it works
------------

It simply runs a Bash process with your script (bash -c).
Script bindings should be accessible as environment variables.
