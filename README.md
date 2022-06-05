# MindustryClojureModTemplate

Example Mindustry mod written in Clojure.

## Building

- JDK 8+
- Leiningen 2.0+ (if you don't have it the script will install it for you)

### Desktop

    $ ./lein uberjar

The output should be in `target/example-<version>-standalone.jar`.

### Desktop + Android

**NOTE:** This mod still doesn't work on Android.

- Android SDK 26+

Make sure the `ANDROID_SDK_ROOT` environment variable is set to the parent
directory of where you unzipped the Android SDK command-line tools. Set it up
like below:

    android/ <-- value of ANDROID_SDK_ROOT
      cmdline-tools/
        latest/
          bin/
          lib/
          NOTICE.txt
          source.properties

Open the terminal and change directory to the Android SDK root directory. Get
the Android SDK platform and build tools (replace the version with the latest):

    $ cd $ANDROID_SDK_ROOT/cmdline-tools/latest/bin
    $ ./sdkmanager "build-tools;32.0.0" "platforms;android-32"

Add `$ANDROID_SDK_ROOT/build-tools/32.0.0` to your PATH. On Windows, replace
`$ANDROID_SDK_ROOT` with `%ANDROID_SDK_ROOT%`. Change directory to where the
project is and do the following:

    $ ./lein do uberjar, dex

Build output should be in `target/example-<version>-android.jar`.

### Copy jar (TODO)

A convenient task `cp` is provided to copy jar to mindustry mods folder:

    $ ./lein do uberjar, dex, cp