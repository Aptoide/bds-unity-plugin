using System.Collections;
using System.Collections.Generic;

using UnityEngine;
namespace Appcoins.Purchasing
{    
    public enum AppcoinsProductType
    {
        Consumable,
        NonConsumable,
        Subscription
    }

    public class AppcoinsProduct
    {
        // public string name;
        public string skuID;
        public bool hasReceipt;
        public string receipt;
        public string transactionID;
        public AppcoinsProductType productType;

        public bool Equals(AppcoinsProduct p)
        {
            if(skuID.Equals(p.skuID))
            {
                return true;
            }

            return false;
        }
    }
}