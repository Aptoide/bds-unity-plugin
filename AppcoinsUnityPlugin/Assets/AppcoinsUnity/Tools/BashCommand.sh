#!/bin/sh
osascript -e 'activate application "/Applications/Utilities/Terminal.app"'
cd '/Users/aptoide/Desktop/build/TrivialTest'
'/Applications/Android Studio.app/Contents/gradle/gradle-4.4/bin/gradle' assembleRelease 2>&1 2>'/Users/aptoide/Documents/GitHub/bds-unity-plugin/AppcoinsUnityPlugin/Assets/AppcoinsUnity/Tools/ProcessError.out'
echo 'done' > '/Users/aptoide/Documents/GitHub/bds-unity-plugin/AppcoinsUnityPlugin/Assets/AppcoinsUnity/Tools/ProcessCompleted.out'
exit
