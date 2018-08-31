using System;
using UnityEngine;

namespace Appcoins.Purchasing
{
    public enum AppcoinsPurchaseProcessingResult
    {
        Complete,
        Pending
    }

    public enum AppcoinsInitializationFailureReason
    {
        PurchasingUnavailable,
        NoProductsAvailable,
        AppNotKnown
    }

    public enum AppcoinsPurchaseFailureReason
    {
        PurchasingUnavailable,
        ExistingPurchasePending,
        ProductUnavailable,
        SignatureInvalid,
        UserCancelled,
        PaymentDeclined,
        DuplicateTransaction,
        Unknown
    }
 
    public class AppcoinsPurchasing : MonoBehaviour
    {
        public const string APPCOINS_PREFAB = "AppcoinsPurchasing";
        
        [SerializeField]
        private string _developerWalletAddress;

        [SerializeField]
        private string _developerBDSPublicKey;

        [SerializeField]
        private bool _shouldLog;

        private AndroidJavaClass _class;
        private AndroidJavaObject instance { get { return _class.GetStatic<AndroidJavaObject>("instance"); } }

        private const string JAVA_CLASS_NAME = "com.aptoide.appcoinsunity.UnityAppcoins";

        // Only class that can comunicate with AppcoinsPurchasing
        private AppcoinsStoreController _controller;

        // AppcoinsStore listener (call specific methods regarding some output purchase)
        private IAppcoinsStoreListener _listener;

        // Store to be able to provide more context to purchase failed errors.
        // We can only get one argument from java function calls so we return the error instead of the skuID
        private AppcoinsProduct _currentPurchaseProduct;

        //  Create an instance of this class. Add an AppcoinsStore listener and
        //  get products definitions from ConfigurationBuilder
        public void Initialize(IAppcoinsStoreListener listener, AppcoinsConfigurationBuilder builder)
        {
            try
            {
                CreateAndSetupJavaBind();

                _listener = listener;
                _controller = new AppcoinsStoreController(this, builder);
            }
            // Catch specific errors and then call OnInitializesFail
            catch (System.Exception e)
            {
                Debug.LogError("Failed with exception " + e);
                OnInitializeFail(e.ToString());
            }
        }

        private void CreateAndSetupJavaBind()
        {
            //get refference to java class
            _class = new AndroidJavaClass(JAVA_CLASS_NAME);

            //Setup sdk
            _class.CallStatic("setDeveloperAddress",_developerWalletAddress);
            _class.CallStatic("setDeveloperBDSPublicKey", _developerBDSPublicKey);
            _class.CallStatic("setLogging", _shouldLog);

            //start sdk
            _class.CallStatic("start");
        }

        // After successful initialization create controler and add listener
        public void OnInitializeSuccess()
        {
            _listener.OnInitialized(_controller);
        }

        public void OnInitializeFail(string error)
        {
            Debug.Log("Called OnInitializeFail with reaason " + error);

            AppcoinsInitializationFailureReason reason = InitializationFailureReasoFromString(error);

            _listener.OnInitializeFailed(reason);
        }

        AppcoinsInitializationFailureReason InitializationFailureReasoFromString(string errorStr) {
            AppcoinsInitializationFailureReason reason = AppcoinsInitializationFailureReason.PurchasingUnavailable;

            if (errorStr.Contains("Problem setting up in-app billing"))
            {
                reason = AppcoinsInitializationFailureReason.PurchasingUnavailable;
            }

            return reason;
        }

        public void InitiatePurchase(AppcoinsProduct prod, string payload)
        {
            _currentPurchaseProduct = prod;
            instance.Call("makePurchase", prod.skuID, payload);
        }

        //callback on successful purchases (called by java class)
        public void OnProcessPurchase(string skuID)
        {
            Debug.Log("Purchase successful for skuid: " + skuID);
            bool success = true;
            if (success)
            {
                if (_listener == null)
                {
                    Debug.LogError("No IStoreListener set up!");
                }

                AppcoinsProduct product = _controller.products.WithID(skuID);
                _currentPurchaseProduct = product;

                switch (product.productType) {
                    case AppcoinsProductType.Consumable:
                        instance.Call("consumePurchase", skuID);
                        break;
                    case AppcoinsProductType.NonConsumable:
                        //Won't consume so skip right through to success
                        OnPurchaseSuccess(skuID);
                        break;
                    case AppcoinsProductType.Subscription:
                        Debug.LogError("We still don't support Subscriptions! Sorry about that...");
                        break;
                }
            }
        }

        public void OnPurchaseSuccess(string skuID)
        {
            AppcoinsProduct product = _controller.products.WithID(skuID);
            _listener.ProcessPurchase(product);

            _currentPurchaseProduct = null;
        }

        //callback on failed purchases
        public void OnPurchaseFailure(string error)
        {
            Debug.Log("Purchase failed with error " + error);
            if (_listener == null)
            {
                Debug.LogError("No IStoreListener set up!");
            }

            if (_controller == null)
            {
                Debug.LogError("No IStoreController set up!");
            }

            AppcoinsPurchaseFailureReason failureReason = PurchaseFailureReasonFromString(error);
            _listener.OnPurchaseFailed(_currentPurchaseProduct, failureReason);

            _currentPurchaseProduct = null;
        }

        AppcoinsPurchaseFailureReason PurchaseFailureReasonFromString(string error) {
            AppcoinsPurchaseFailureReason reason = AppcoinsPurchaseFailureReason.Unknown;

    //        String[] iab_msgs = ("0:OK/1:User Canceled/2:Unknown/"
    //+ "3:Billing Unavailable/4:Item unavailable/"
    //+ "5:Developer Error/6:Error/7:Item Already Owned/"
    //+ "8:Item not owned").split("/");
            //String[] iabhelper_msgs = ("0:OK/-1001:Remote exception during initialization/"
                //+ "-1002:Bad response received/"
                //+ "-1003:Purchase signature verification failed/"
                //+ "-1004:Send intent failed/"
                //+ "-1005:User cancelled/"
                //+ "-1006:Unknown purchase response/"
                //+ "-1007:Missing token/"
                //+ "-1008:Unknown error/"
                //+ "-1009:Subscriptions not available/"
                //+ "-1010:Invalid consumption attempt").split("/");

            if (error.Contains("User cancelled", StringComparison.OrdinalIgnoreCase)) 
            {
                reason = AppcoinsPurchaseFailureReason.UserCancelled;
            } 
            else if (error.Contains("Unknown error", StringComparison.OrdinalIgnoreCase))
            {
                reason = AppcoinsPurchaseFailureReason.Unknown;
            }
            else if (error.Contains("Purchase signature verification failed", StringComparison.OrdinalIgnoreCase))
            {
                reason = AppcoinsPurchaseFailureReason.SignatureInvalid;
            }
            else if (error.Contains("Unable to buy item", StringComparison.OrdinalIgnoreCase))
            {
                reason = AppcoinsPurchaseFailureReason.ProductUnavailable;
            }
            else if (error.Contains("Unknown error", StringComparison.OrdinalIgnoreCase))
            {
                reason = AppcoinsPurchaseFailureReason.Unknown;
            }

            return reason;
        }

        bool IsValidPayload(string payload) {
            Debug.Log("Validating payload " + payload);
            return true;
        }

        public void AskForPayloadValidation(string payload) {
            bool result = IsValidPayload(payload);

            instance.Call("setPayloadValidationStatus", result);
        }

        public bool OwnsProduct(string skuID) {
            return instance.Call<bool>("OwnsProduct", skuID);
        }
    }
}

