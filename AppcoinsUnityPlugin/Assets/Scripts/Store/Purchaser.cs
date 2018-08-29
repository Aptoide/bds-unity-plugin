using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Purchasing;
using Appcoins.Purchasing;
using UnityEngine.Events;

public class PurchaseEvent : UnityEvent<AppcoinsProduct> {
    
}

// Deriving the Purchaser class from IStoreListener enables it to receive messages from Unity Purchasing.
[RequireComponent(typeof(AppcoinsPurchasing))]
public class Purchaser : MonoBehaviour, IAppcoinsStoreListener
{
    public UnityEvent onInitializeSuccess;
    public UnityEvent onInitializeFailed;

    public PurchaseEvent onPurchaseSuccess;
    public PurchaseEvent onPurchaseFailed;

    [SerializeField]
    private Text _statusText;

    private static AppcoinsStoreController m_StoreController;          // The AppCoins Purchasing system.

    private AppcoinsPurchasing _appcoinsPurchasing;
    private ConfigurationBuilder _builder;

    void Awake()
    {
        onInitializeSuccess = new UnityEvent();
        onInitializeFailed = new UnityEvent();

        onPurchaseSuccess = new PurchaseEvent();
        onPurchaseFailed = new PurchaseEvent();

        StandardPurchasingModule stdModule = StandardPurchasingModule.Instance();
        Debug.Log("StandardPurchasingModule " + stdModule);
        // Create a builder, first passing in a suite of Unity provided stores.
        _builder = ConfigurationBuilder.Instance(stdModule);
    }

    public void AddProduct(string skuID, ProductType type) {
        _builder.AddProduct(skuID, type);
    }

    public void InitializePurchasing()
    {
        _appcoinsPurchasing = GetComponent<AppcoinsPurchasing>();

        // If we have already connected to Purchasing ...
        if (IsInitialized())
        {
            // ... we are done here.
            return;
        }

        _appcoinsPurchasing.Initialize(this, _builder);
        SetStatus("UnityPurchasing initializing.");
    }

    void SetStatus(string status) {
        if (_statusText != null)
            _statusText.text = status;
        Debug.Log(status);
    }

    private bool IsInitialized()
    {
        // Only say we are initialized if both the Purchasing references are set.
        return m_StoreController != null;
    }

    public void BuyProductID(string productId)
    {
        // If Purchasing has been initialized ...
        if (IsInitialized())
        {
            // ... look up the Product reference with the general product identifier and the Purchasing
            // system's products collection.
            AppcoinsProduct product = m_StoreController.products.WithID(productId);

             //If the look up found a product for this device's store and that product is ready to be sold ...
            if (product != null)
            {
                if (product.productType == ProductType.NonConsumable && OwnsProduct(productId)) {
                    OnPurchaseFailed(product, PurchaseFailureReason.DuplicateTransaction);
                    SetStatus("BuyProductID: FAIL. Not purchasing product, non-consumable is already owned!");
                    return;
                }

                SetStatus(string.Format("Purchasing product asychronously: '{0}'", product.skuID));
                // ... buy the product. Expect a response either through ProcessPurchase or OnPurchaseFailed
                // asynchronously.


                /* TODO: for security, generate your payload here for verification. See the comments on
                 *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
                 *        an empty string, but on a production app you should carefully generate this.
                 * TODO: On this payload the developer's wallet address must be added, or the purchase does NOT work.
                 */
                string payload = "";
                m_StoreController.InitiatePurchase(product, payload);
            }
            // Otherwise ...
            else
            {
                // ... report the product look-up failure situation
                SetStatus("BuyProductID: FAIL. Not purchasing product, either is not found or is not available for purchase");
            }
        }
        // Otherwise ...
        else
        {
            // ... report the fact Purchasing has not succeeded initializing yet. Consider waiting longer or
            // retrying initiailization.
            SetStatus("BuyProductID FAIL. Not initialized.");
        }
    }

    bool OwnsProduct(string skuID) {
        return _appcoinsPurchasing.OwnsProduct(skuID);
    }

    // Restore purchases previously made by this customer. Some platforms automatically restore purchases, like Google.
    // Apple currently requires explicit purchase restoration for IAP, conditionally displaying a password prompt.
    public void RestorePurchases()
    {
    }


    //
    // --- IAppcoinsStoreListener
    //

    public void OnInitialized(AppcoinsStoreController controller)
    {
        onInitializeSuccess.Invoke();

        // Purchasing has succeeded initializing. Collect our Purchasing references.
        SetStatus("OnInitialized: PASS");

        // Overall Purchasing system, configured with products for this application.
        m_StoreController = controller;
    }


    public void OnInitializeFailed(InitializationFailureReason error)
    {
        onInitializeFailed.Invoke();

        SetStatus("OnInitialized: FAILED: " + error);
    }


    public PurchaseProcessingResult ProcessPurchase(AppcoinsProduct p)
    {
        SetStatus("Processed purchase " + p.skuID);

        onPurchaseSuccess.Invoke(p);

        return PurchaseProcessingResult.Complete;
    }


    public void OnPurchaseFailed(AppcoinsProduct product, PurchaseFailureReason failureReason)
    {
        onPurchaseFailed.Invoke(product);

        // A product purchase attempt did not succeed. Check failureReason for more detail. Consider sharing
        // this reason with the user to guide their troubleshooting actions.
        SetStatus(string.Format("OnPurchaseFailed: FAIL.\nProduct: '{0}',\nPurchaseFailureReason: {1}", (product != null ? product.skuID : "none"), failureReason));
    }
}
