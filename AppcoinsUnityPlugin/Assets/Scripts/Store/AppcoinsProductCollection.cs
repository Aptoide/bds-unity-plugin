using System;
using System.Collections;
using System.Collections.Generic;

using UnityEngine;
using UnityEngine.Purchasing;
using UnityEngine.Purchasing.Extension;

namespace Appcoins.Purchasing
{
    public class AppcoinsProductCollection
    {
        public List<AppcoinsProduct> all;

        public AppcoinsProductCollection(HashSet<ProductDefinition> prods)
        {
            all = new List<AppcoinsProduct>();

            foreach (ProductDefinition prodDef in prods)
            {
                AppcoinsProduct prod = new AppcoinsProduct(prodDef);
                all.Add(prod);
            }
        }

        public AppcoinsProductCollection(HashSet<AppcoinsProduct> prods)
        {
            all = new List<AppcoinsProduct>(prods);
        }

        public bool AddProduct(AppcoinsProduct prod)
        {
            foreach(AppcoinsProduct p in all) {
                if (p.skuID.Equals(prod.skuID))
                    return false;
            }

            all.Add(prod);
            return true;
        }

        public AppcoinsProduct WithID(string id)
        {
            foreach(AppcoinsProduct p in all) {
                if (p.skuID.Equals(id))
                    return p;
            }

            return null;
        }

        public AppcoinsProduct WithStoreSpecificID(string id)
        {
            return WithID(id);
        }
    }

    class AppcoinsProductComparer : IEqualityComparer<AppcoinsProduct>
    {
        public bool Equals(AppcoinsProduct p1, AppcoinsProduct p2)
        {
            if (p2 == null && p1 == null)
            return true;
            else if (p1 == null | p2 == null)
            return false;
            else if(p1.skuID.Equals(p2.skuID))
                return true;
            else
                return false;
        }

        public int GetHashCode(AppcoinsProduct p)
        {
            return p.skuID.GetHashCode();
        }
    }
}
