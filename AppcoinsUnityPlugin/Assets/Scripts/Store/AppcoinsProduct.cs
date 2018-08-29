using System.Collections;
using System.Collections.Generic;

using UnityEngine;
using UnityEngine.Purchasing;
using UnityEngine.Purchasing.Extension;

namespace Appcoins.Purchasing
{    
    public class AppcoinsProduct
    {
        // public string name;
        public string skuID;
        public bool hasReceipt;
        public string receipt;
        public string transactionID;
        public ProductType productType;

        public AppcoinsProduct (ProductDefinition def)
        {
            skuID = def.id;
            productType = def.type;
        }

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