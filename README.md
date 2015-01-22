# jsr223-nativeshell

Native shell script engines for Java implementing JSR 223

## Supported native shells

* Bash: using the name bash or the extensions .bash/.sh
* Cmd.exe: using the name cmd or the extension .bat
* Executable: using the name exec

## Usage

Simply add the JAR to your classpath and follow the [Java Scripting Programmer's guide](http://docs.oracle.com/javase/6/docs/technotes/guides/scripting/programmer_guide/index.html)

## Build

Run gradlew script, it produces a JAR file in build/libs

## How it works

It simply runs the shell as a native process and pass it your script (stored in a temporary file).
For the Executable engine it simply takes the script and run it as a single command using Java ProcessBuilder.

### Bindings

Script bindings are exported as environment variables using their [toString()](http://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#toString())
 representation and accessible as such in the shells.

For the Executable engine, bindings will also be replaced in the command line (i.e the script).
For instance, running a script like "echo $var" will execute "echo value" if the binding var=value is defined.

[Collections](http://docs.oracle.com/javase/7/docs/api/java/util/Collection.html) and arrays elements are exported with
the name of the binding suffixed with an underscore and the index of the element.

[Maps](http://docs.oracle.com/javase/7/docs/api/java/util/Map.html) entries are exported with the name
of the binding suffixed with an underscore and the entry's key.

**Binding Examples:**

| Binding                                         | Usage                      | Output    |
| ----------------------------------------------- |--------------------------- | --------- |
| String aString = "blurp"                        | echo $aString              | blurp     |
| int aInt = 42                                   | echo $aInt                 | 42        |
| float aFloat = 42.0                             | echo $aFloat               | 42.0      |
| String[] anArray = new String[]{"hello", "bob"} | echo $anArray_0 $anArray_1 | hello bob |
| List aList = asList("hello", "bob")             | echo $aList_0 $aList_1     | hello bob |
| Map aMap = singletonMap("hello", "bob")         | echo $aMap_hello           | bob       |

_Of course you noticed that this approach comes with many flaws (nested lists, variables names with underscores...), but
at least it should work for simple cases_