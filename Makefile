# This should be the Android SDK root directory
ANDROID ?= ../android-sdk-linux_86/

# SDK version
ANDROID_VERSION=1.5

TARGET = TubeJourney
PACKAGE = net.tevp.tubejourney
KEYSTORE=.debug.keystore
R_PATH = src/net/tevp/tubejourney/R.java

SOURCE_FILES=$(wildcard src/net/tevp/*/*.java)

include Makefile.common
