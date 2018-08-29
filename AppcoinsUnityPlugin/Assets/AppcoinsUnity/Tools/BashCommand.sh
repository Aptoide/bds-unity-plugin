#!/bin/sh
osascript -e 'activate application "/Applications/Utilities/Terminal.app"'
cd '/Users/nunomonteiro/Documents/GitHub/appcoins-unity-plugin/AppcoinsUnityPlugin/and/TrivialTest/TrivialTest'
if [ "$('/Users/nunomonteiro/Library/Android/sdk/platform-tools/adb' get-state)" == "device" ]
then
'/Users/nunomonteiro/Library/Android/sdk/platform-tools/adb' shell am start -n 'com.appcoins.trivialdrivesample/com.appcoins.trivialdrivesample.test.UnityPlayerActivity' 2>&1 2>'/Users/nunomonteiro/Documents/GitHub/appcoins-unity-plugin/AppcoinsUnityPlugin/Assets/AppcoinsUnity/Tools/ProcessError.out'
else
echo error: no usb device found > '/Users/nunomonteiro/Documents/GitHub/appcoins-unity-plugin/AppcoinsUnityPlugin/Assets/AppcoinsUnity/Tools/ProcessError.out'
fi
echo 'done' > '/Users/nunomonteiro/Documents/GitHub/appcoins-unity-plugin/AppcoinsUnityPlugin/Assets/AppcoinsUnity/Tools/ProcessCompleted.out'
exit
