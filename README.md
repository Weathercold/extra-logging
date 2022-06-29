# MindustryClojureModTemplate

Example Mindustry mod written in Clojure.

## What to do after creating your repo

1. Rename `src/{clojure,java}/example`
2. Modify `assets/mod.hjson`
3. Modify `project.clj`
4. Set `MINDUSTRY_JAR` environment variable for the launch task

## Tasks

- JDK 8+
- Leiningen 2.0+ (if you don't have it the script will install it for you)

### Build for desktop

    $ ./lein uberjar

The output should be `target/example-<version>-desktop.jar`.\
**NOTE:** You most likely wouldn't want to use
`target/example-<version>-nodeps.jar` since it doesn't include the
dependencies.

### Dexify for Android

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

### Copy jar to mods folder

A convenient task `copy` is provided to copy *the most recently built jar* to
mindustry mods folder:

    $ ./lein copy

### Launch game for testing

Set the environment variable `MINDUSTRY_JAR` to the path to the Mindustry jar,
then run:

    $ ./lein launch

**TIP:** If you use IntelliJ IDEA, you can set environment variables in run
configurations (see below).

### Aliases

- `uberdex` = `do uberjar, dex`
- `run-jar` = `do uberjar, copy, launch`
- `run-dex` = `do uberjar, dex, copy, launch`

## IntelliJ IDEA Run Configurations

Two run configurations are provided:
- Desktop `./lein run-jar`
- Android `./lein run-dex`

## GitHub Workflow

A workflow automatically builds your project on commit or on pull request.
A dexified jar and a plain one are generated.[1]

## Credits

iarkn for the dex task, GitHub workflow and various other things

---

[1] Due to GitHub's limitation, jar files cannot be uploaded directly. I worked
around this issue by extracting and uploading the files inside the jar, however
the upload speed for a large amount of small files is extremely slow. For this
reason a plain jar for testing is also uploaded, but it comes wrapped in a zip.