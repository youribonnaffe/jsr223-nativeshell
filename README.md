jsr223-nativeshell
===========

Native shell script engines for Java implementing JSR 223

Supported native shells
---------

* Bash: using the name bash or the extensions .bash/.sh
* Cmd.exe: using the name cmd or the extension .bat

Usage
-----

Simply add the JAR to your classpath and follow the [Java Scripting Programmer's guide](http://docs.oracle.com/javase/6/docs/technotes/guides/scripting/programmer_guide/index.html)

Build
-----

Run gradlew script, it will produce a JAR file in build/libs

How it works
------------

It simply runs the shell as a native process and pass it your script (stored in a temporary file).
Script bindings should be accessible as environment variables.
