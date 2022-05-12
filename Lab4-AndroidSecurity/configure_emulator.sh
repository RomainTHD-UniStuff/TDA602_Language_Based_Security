#!/bin/sh
cd ~/Android/Sdk/platform-tools

./adb root
./adb remount

wget https://busybox.net/downloads/binaries/1.27.1-i686/busybox
./adb push busybox /data/data/busybox
./adb shell "mv /data/data/busybox /system/bin/busybox && chmod 755 /system/bin/busybox && /system/bin/busybox --install /system/bin"

rm busybox

echo "The testing apparatus has been set up."
echo "Install the completed MACLocation application."
echo "Then run test_emulator.sh to test the application."

