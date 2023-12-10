# Extra Logging 2

[![Build Project](https://github.com/Weathercold/extra-logging/actions/workflows/build.yml/badge.svg)](https://github.com/Weathercold/extra-logging/actions/workflows/build.yml)
[![Total Downloads](https://img.shields.io/github/downloads/Weathercold/extra-logging/total?color=success&labelColor=gray&label=Downloads&logo=docusign&logoColor=white)](https://github.com/Weathercold/extra-logging/releases)
[![Language: Clojure](https://img.shields.io/badge/Language-Clojure-db5855)](https://clojure.org)\
Mindustry mod that adds more logging features and improvements.\
Successor to the original ExtraLogging, now rewritten in Clojure.

## Features

- **Fully customizable log messages /w timestamp** <sup>[1]</sup>;
- **Translate chat messages to your locale**;
- Change log level <sup>[1]</sup>;
- Force colored terminal output, colorize `[name]` in log messages <sup>[2]</sup>;
- Print event triggers to the console <sup>[1]</sup>;
- Fix log display order (vanilla issue);
- Various other quality of life improvements.

## Android Support

Android support was dropped from version 2.1.0 onwards because I had to upgrade
Clojure to 1.9.0+ which is broken on Android. You can still try to use an older
version, though it's not guaranteed to work.

---

[1]: Only after the mod is created though\
[2]: For Windows and Android, you need a modern terminal that supports ANSI
color codes. ~~This proves Linux is superior~~
