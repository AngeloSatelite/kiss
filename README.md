# kiss java API

## kiss Java

The goal is to make the launching and testing of java programs for beginners
easy: no explainations of static methods, simplified IO, and a simple
test framework beginners can use to write and validate their code.

In order to keep it simple, I have incorporated all this into one library, `kiss`, so students do not have to download other library dependencies and have a single import statement to start using it.  Here is the hello world program:

```java
import static kiss.API.*;

public class App {
    public void run() {
        println("Hello, World!");
    }
}
```

To see the classic message, you need the (kiss-1.0-SNAPSHOT.jar kiss) library in your project and use "kiss.util.Run" as your main class.  If you use maven (supported by most common IDE's) there are pre-built `pom.xml` files in the example projects.

## Test Hello World

So it is not hard to say hello.  Now let's test it.

```java
import static kiss.API.*;

public class App {
    public void testRun() {
        // create a file with the output we expect
        outOpen("testRun.verify");
        println("Hello, World!");
        outClose();

        // call run() to check that it matches
        outVerify("testRun.verify");
        run();
        outClose();
    }

    public void run()
    {
        println("Hello, World!");
    }
}
```
Any `testXXX` method is automatically called before the `run` method.  Want to write a test?  Just write the test!

## Randomness a kindergardener understands

`int die=random(1,6)` is a die roll and `seed(1)` resets the pseudo-random sequence for testability.  The random-number sequence is reset with `seed(1)` before each `textXXX()` and reset to a cryptographically strong PRNG with `seed()` before invoking `run()`.

## Just math

The java.Math static methods are effectively also imported, so you just use `sqrt`, not `Math.sqrt`.  Peppering 'Math.' in all your formulas does not make you smarter or the code easier to read.

## File IO sucks less.

Here is a java program that makes an CSV file with 100 die rolls pairs:

```java
import static kiss.API.*;

public class App {
    public void run() {
       outOpen("rolls.csv");
       println("i,d1,d2,sum");
       for (int i=1; i<=100; ++i) {
          int d1=random(1,6);
          int d2=random(1,6);
          int sum=d1+d2;
          println(i,d1,d2,sum);
       }
       outClose();
    }
}
```

## Goodness by section

### Run

The `Run` part of the kiss API simplifies starting and controlling the execution of your application.  It contains a `main()` method to launch your application which does the following

1. Run looks for the application class.  This is `App` in the default (no declaration) package.  If you want to use some other class, just pass the `--app <classname>` argument on the command line, or specifiy it with the `JAVA_APP` environment variable.

2. Run constructs an instance of the application class using the default constructor.  The constructor (or anywhere else) can use APP_ARGS to access command line arguments and APP_NAME contains the class name of the application object.  After the instance is constructed (at the end of this step), APP is a global reference to the application object.

3. Run invokes all testXXX methods in order of declaration.  The random
number generator is reset with seed(1) before each test to help make the
tests reproducable.

4. Run invokes the run method.  The kiss random number generator is set to
produce a strongly random sequence before this.

5. Run invokes the close method, even if there was an error in a test or run.

All the steps after the construction are optional.

* `sleep(double duration)` --- Pauses the application for the given number of seconds (without wasting CPU time).

* `double time()` --- Returns the seconds since midnight, January 1, 1970 UTC.  Calling this twice and taking the difference is useful for interval timing.

* `String APP_NAME` --- The name of the application class (just `"App"` by default)

* `String[] APP_ARGS` --- The command line arguments.

* `Object APP` --- Once the application object is constructed, this is a global reference to the application object.

### IO

* `outOpen(String filename)` --- redirects print/ln() output to the specified
  file.  These can be nested and each thread is independent.  There must be a
  matching `outClose()` to close the file.

* `inOpen(String filename)` --- redirects readXXX() to use this input file.  Thse can be nested and each thread is independent.  There must be a matching `inClose()` to close the file.

* `outVerify(String filename)` --- compares print/ln() output to the contents of this file.  `readXXX()` are also matched so you should be able to copy your console dialog into a verify file to create an expected contract.  Like outOpen, this can be nested and each thread is independent, and must be matched by outClose() to stop the verification.  Verification failures throw an error at the point a mismatch is detected.

* `format(...)` --- a way to convert objects to printable strings similar to the print() statements below.  They do a better job than the java PrintStream methods on structured objects like arrays and lists.

* `print/ln(...)` --- like format, but sends the formatted strings to the current output stream (`System.out` by default).  You can change the default with `outOpen` and `outVerify`.

* `readXXX(...)` --- uses a `java.util.Scanner` on the current input stream (`System.in` by default).  You can change the default with `inOpen`.

### Math

All the math functions and constants are imported, with the exception of `Math.random()` and `Math.random(int n)` which is improved.

### RNG

A fast (almost as fast as the standard random number generator) but crytographically strong pseudo-random number generator is provided by default.  Calling `seed()` strongly sets the seed, while `seed(double value)` sets it to a reproducable sequence.

* `int random(int a, int b)` --- generates a uniformly random integer in the interval, including the endpoints.  If b<=a, this always returns a.

* `double random()` --- returns a uniformly random double in the interval [0,1).

* `seed()` --- seeds the PRNG with a 128-bit strong random seed.

* `seed(double value)` --- seeds the PRNG for a reproducable sequence.

  
