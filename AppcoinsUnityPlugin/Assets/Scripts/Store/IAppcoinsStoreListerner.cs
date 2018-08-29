using UnityEngine.Purchasing;

namespace Appcoins.Purchasing
{
    public interface IAppcoinsStoreListener
    {
        void OnInitializeFailed(InitializationFailureReason error);

        PurchaseProcessingResult ProcessPurchase(AppcoinsProduct p);

        void OnPurchaseFailed(AppcoinsProduct product, PurchaseFailureReason reason);

        void OnInitialized(AppcoinsStoreController controller);
    }
}

