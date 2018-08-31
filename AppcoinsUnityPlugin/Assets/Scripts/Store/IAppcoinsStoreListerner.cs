namespace Appcoins.Purchasing
{
    public interface IAppcoinsStoreListener
    {
        void OnInitializeFailed(AppcoinsInitializationFailureReason error);

        AppcoinsPurchaseProcessingResult ProcessPurchase(AppcoinsProduct p);

        void OnPurchaseFailed(AppcoinsProduct product, AppcoinsPurchaseFailureReason reason);

        void OnInitialized(AppcoinsStoreController controller);
    }
}

