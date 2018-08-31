using System;
using System.Collections;
using System.Collections.Generic;

using UnityEngine;

namespace Appcoins.Purchasing
{
    public class AppcoinsStoreController
    {
        // Reference to AppcoinsPurchasing
        private AppcoinsPurchasing _appcoinsIAB;

        // Products container
        public AppcoinsProductCollection products;

        public AppcoinsStoreController (AppcoinsPurchasing appc, AppcoinsConfigurationBuilder builder)
        {
            _appcoinsIAB = appc;
            products = new AppcoinsProductCollection(builder.products);
        }

        // Add aditional prodcuts to AppcoinsPurchasing. If a product is successful
        // added call successCallback if not call failCallback
        public void FetchAdditionalProducts (HashSet<AppcoinsProduct> prodDefs, 
                                             Action successCallback, 
                                             Action<AppcoinsInitializationFailureReason> failCallback
                                            ) 
        {
            foreach (AppcoinsProduct prodDef in prodDefs)
            {
                if(products.AddProduct (prodDef))
                {
                    successCallback();
                }

                else
                {
                    failCallback(AppcoinsInitializationFailureReason.NoProductsAvailable);
                }
            }
        }

        public void InitiatePurchase (string productId)
        {
            InitiatePurchase (productId, "");
        }
        
        public void InitiatePurchase (string productId, string payload)
        {
            AppcoinsProduct prod = products.WithID(productId);
            InitiatePurchase (prod, "");
        }

        public void InitiatePurchase (AppcoinsProduct prod)
        {
            InitiatePurchase (prod, "");
        }

        public void InitiatePurchase (AppcoinsProduct prod, string payload)
        {
            _appcoinsIAB.InitiatePurchase (prod, payload);
        }

        public void ConfirmPendingPurchase (AppcoinsProduct product)
        {
            // TODO call this method when the AppcoinsStore receives 
            // PurchaseProcessingResult.Pending after trying buy some
            // product to inform AppcoinsPurchasing that the store
            // made a record of the purchase
        }
    }
} 
