# AppCoins Unity Plugin

![Logo](Screenshots/logos.png)

This is the official Unity plugin for the AppCoins Protocol that allows you to integrate AppCoins In-App Billing into your Unity Android game.

## About AppCoins Unity Plugin
This plugin is developed to support the  [AppCoins](https://appcoins.io/) protocol.
 Integrating this plugin allows developers to integrate the AppCoins In-App Billing into their game.

## Pre requisites to run the project
To successfully run the project you need to:
1. Download and install AppCoins Wallet app (you can get it on [Aptoide](https://appcoins-wallet.en.aptoide.com/?store-name=asf-store) or [GooglePlay](https://play.google.com/store/apps/details?id=com.appcoins.wallet))

2. Open AppCoins Wallet and create or restore a wallet

## Integrating the plugin into your game

1. Register on [BDS](https://blockchainds.com/)

2. Open your project on Unity and make sure that you have Unity In-App Purchasing service enabled. If you do, skip the "Enabling Unity In-App Purchasing service for your game" section.

## Enabling Unity In-App Purchasing service for your game
1. Press the cloud icon on the top right corner of the Unity window. This will open the Services panel.

![cloudIcon](Screenshots/cloudIcon.png)

2. If you don't have a Project ID pick an organization and click "Create".

![projectID](Screenshots/projectID.png)

3. Now scroll down until you find In-App Purchasing and click it.

![In-App Purchasing](Screenshots/iap.png)

4. On the In-App Purchasing panel, click the black toggle button or the enable button to enable the service.

![iapPanel](Screenshots/iapPanel.png)

5. Answer the COPPA compliance question accordingly.

6. On the welcome screen press "Import". This will start Unity's IAP package import process. Press "Import" on the new window that pops up. **NOTE:** During this process you'll be notified of an API Update. If you have sensitive data, backup your code and then click "I've made a backup, go ahead".

![packageImport](Screenshots/packageImport.png)

7. On the welcome screen, the "Import" button should've changed to "Reimport" and a message saying "Unity IAP is up to date" should be displaying. If this isn't showing, try reloading the Services window again or pressing "Import" again and then cancelling.

![reimport](Screenshots/reimport.png)

## Integrating the plugin into your game (Unity IAP enabled)
1. Download the plugin package [BDS_AppCoins_Unity_Plugin.unitypackage](https://github.com/Aptoide/appcoins-unity-plugin/blob/develop/BDS_AppCoins_Unity_Plugin.unitypackage) file and open the package in your Unity project (double click the file or in Unity go to Assets -> Import Package -> Custom Package.... and find the file you just downloaded). Make sure to import the example folder.

2. Open the example scene.

3. Change the package name to: "com.appcoins.sample"

![packageName](Screenshots/packageName.png)

4. Build and run for Android.

3. If everything goes well you should see the message:
"OnInitialized: PASS" on the top of the phone screen. This means that you have everything correctly setup. You can try the consumable and non-consumable purchases to also see that they work.

  **NOTE:** this will only work on the main network (Ethereum)

![message](Screenshots/message.png)

4. Insert these snippets into your logic class

4. Add these imports
```
        using Appcoins.Purchasing;
        using UnityEngine.Purchasing;
```

4. Define product ids
```
        public static string kYourProductID = "yourSkuID";
        public static string kOtherProductID = "otherSkuID";
```
5. Define outlet for Purchaser class
```
        [SerializeField]
        private Purchaser _purchaser;
```
5. Define OnPurchaseSuccess to process the purchase result
```
        private void OnPurchaseSuccess(AppcoinsProduct product)
        {
            Debug.Log("On purchase success called with product: \n skuID: " + product.skuID + " \n type: " + product.productType);

            if (product.skuID == kYourProductID) {
                GiveYourProduct();
            } else if(product.skuID == kOtherProductID) {
                GiveOtherProduct();
            }
        }
```
6. Setup purchaser with defined product ids and correct product types
```
        void SetupPurchaser() {
            _purchaser.onInitializeSuccess.AddListener(OnInitializeSuccess);
            _purchaser.onPurchaseSuccess.AddListener(OnPurchaseSuccess);

            _purchaser.AddProduct(kYourProductID, ProductType.Consumable);
            _purchaser.AddProduct(kOtherProductID, ProductType.NonConsumable);

            //Only call initialize after adding all products!
            _purchaser.InitializePurchasing();
       }
```
6. Call the function you just created when you want to initialize the Purchasing system. We want it as soon as possible so we add it to the Start method of our logic class.

```
        void Start () {
          //... your code ...
          SetupPurchaser();
        }
```

7. Create an instance of the prefab _AppcoinsPurchasing_ located on the Prefabs folder.

8. Fill it in with the appropriate values for _Developer Wallet Address_ and _Developer BDS Public Key_. (The default ones are working for the sample app)

9. Make sure to drag _AppcoinsPurchasing_ game object to the purchaser outlet your created in your logic object.

**NOTE:** If you want to easily debug the interactions with the BDS Purchasing system, you can attach a Unity.Text label to the Purchaser Status text outlet.

# Good practices you should follow

Disable purchase buttons if purchasing is not initialized yet.

Disable purchase buttons for already owned non-consumable buttons.

## To build the project

Go to the build menu (File -> Build Settings)
1. Check that the build system is set to "Gradle"
(if the import was done correctly this should've changed automatically).

![picture](Screenshots/buildSystemGradle.png)

Now click _Player Settings_.

On the _Player Settings_ window:
1. Click the other settings panel

2. Make sure you change the package name to your liking.

3. Make sure that you have min sdk version set to 21 (if the import was done correctly this should've changed automatically).

**Unity 2018.2b (and above)**

1. Connect the phone to your machine and click _Build and Run_

You should have your game running on the phone!

**Unity 2018.1.X and below (till Unity 5.X)**

1. Close the _Player Settings_ window

2. On the top bar click _AppCoins_

3. Click _Custom Android Build_

4. This popup will show up

![customBuild](Screenshots/customBuild.png)

5. The gradle path should be picked from the path to your Android Studio installation

5. Gradle and Dex heap size are just to be changed if you have issues building.

6. The adb path will be picked by you (assuming you have Android SDK installed)

7. Pick the scenes you want to include. The ones added to the build settings, as well as the scene you're currently added, will automatically be selected for you

8. When you click _Confirm_ a pop up will show up asking you to pick a folder to generate the _Android_ project to. Pick a folder of your liking preferably inside the project root (it can't be the project root itself).

  **NOTE:** The final build will be located here:
  FolderYouChoseToBuildTo/ProjectName/build/outputs/apk/
  in a subfolder called debug or release, depending on build settings)

9. When you pick the folder the build process will start. The normal build process will happen and then the custom build process will kick in opening a terminal window. Unity might seem to be not responding but worry not! This is normal because it's waiting for the terminal processes to finish.

10. If you ticked _Install build when done?_ make sure you have your phone connected to the computer and that you unlock it to allow ADB to install

10. If you ticked _Run build when done?_ make sure you have your phone connected to the computer and that you unlock it to allow ADB to run

11. The build process completed. You can run the app on your phone!

## To make a signed build
1. Go to Edit -> Project Settings -> Player

2. Open the Publishing Settings tab

If you already have a keystore:
3. Tick "Use Existing Keystore" and then click "Browse keystore" to fetch it.

4. You have to provide the keystore password to allow Unity to read the key aliases.

5. Pick the correct alias and provide its password as well

 ![picture](Screenshots/keyStore.png)

6. You're done!

If you don't have a key already:
3. Tick "Create new keystore..." and then click "Browse Keystore"

4. Now pick the path where the key will be created

5. Now pick a password and write it again to confirm

6. Click the alias dropdown and then chose "Create a new key"

![picture](Screenshots/pickAlias.png)

7. Fill in all the details and click "Create Key"

![picture](Screenshots/createAlias.png)

8. Now go back to the alias dropdown and pick the alias you just created

9. You're done!

You're DONE! Congrats!
