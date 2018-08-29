using System;
using System.Collections;
using System.Collections.Generic;

using UnityEngine;
using UnityEngine.UI;

using Appcoins.Purchasing;
using UnityEngine.Purchasing;

public class Logic : MonoBehaviour {

    [SerializeField]
    private Text _gasTxt;

    [SerializeField]
    private Purchaser _purchaser;

    [SerializeField]
    private GameObject _adsBar;

    [SerializeField]
    private Button _btnBuyGas;

    [SerializeField]
    private Button _btnBuyNoAds;

    public const string GAS_KEY = "gas";
    public static string kProductIDConsumable = "gas";
    public static string kProductIDNonConsumable = "premium";

    private int _gasAmount;
    private bool _disabledAds;

	// Use this for initialization
	void Start () {
        DisablePurchaseButtons();

        _gasAmount = PlayerPrefs.GetInt(GAS_KEY, 0);
        UpdateGasLabel();

        SetupPurchaser();
	}

    void SetupPurchaser() {
        _purchaser.onInitializeSuccess.AddListener(OnInitializeSuccess);
        _purchaser.onPurchaseSuccess.AddListener(OnPurchaseSuccess);

        _purchaser.AddProduct(kProductIDConsumable, ProductType.Consumable);
        _purchaser.AddProduct(kProductIDNonConsumable, ProductType.NonConsumable);

        _purchaser.InitializePurchasing();
    }

    private void OnInitializeSuccess() {
        EnablePurchaseButtons();
    }

    private void OnPurchaseSuccess(AppcoinsProduct product)
    {
        Debug.Log("On purchase success called with product: \n skuID: " + product.skuID + " \n type: " + product.productType);

        if (product.skuID == kProductIDConsumable) {
            IncrementGas();
            PlayerPrefs.SetInt(GAS_KEY, _gasAmount);
            PlayerPrefs.Save();
        } else if(product.skuID == kProductIDNonConsumable) {
            DisableAds();
        }
    }

    void IncrementGas() {
        _gasAmount += 1;
        UpdateGasLabel();
    }

    void UpdateGasLabel() {
        _gasTxt.text = "Gas: " + _gasAmount;
    }

    void DisableAds() {
        _btnBuyNoAds.enabled = false;
        _adsBar.SetActive(false);
    }

    public void BuyGas() {
        _purchaser.BuyProductID(kProductIDConsumable);
    }

    public void BuyNoAds()
    {
        _purchaser.BuyProductID(kProductIDNonConsumable);
    }

    void EnablePurchaseButtons() {
        _btnBuyGas.enabled = true;
        _btnBuyNoAds.enabled = true;
    }

    void DisablePurchaseButtons()
    {
        _btnBuyGas.enabled = false;
        _btnBuyNoAds.enabled = false;
    }

}
