﻿using System;
using System.Collections;
using System.Collections.Generic;

using UnityEngine;
using UnityEngine.UI;

using Appcoins.Purchasing;

public class Logic : MonoBehaviour, IPayloadValidator {

    [SerializeField]
    private Text _statusTxt;

    [SerializeField]
    private Text _gasTxt;

    [SerializeField]
    private GameObject _adsBar;

    [SerializeField]
    private Button _btnBuyGas;

    [SerializeField]
    private Button _btnBuyNoAds;

    [SerializeField]
    private Text _priceTxt;
    [SerializeField]
    private Text _priceFiatTxt;
    [SerializeField]
    private Text _currencyCodeTxt;

    public const string GAS_KEY = "gas";
    public static string kProductIDConsumable = "gas";
    public static string kProductIDNonConsumable = "premium";

    private AppcoinsPurchasing _appcoins;
    private Purchaser _purchaser;
    private int _gasAmount;
    private bool _disabledAds;

	// Use this for initialization
	void Start () {
        //DisablePurchaseButtons();

        _gasAmount = PlayerPrefs.GetInt(GAS_KEY, 0);
        UpdateGasLabel();

        SetupPurchaser();
	}

    void SetupPurchaser() {

        _purchaser = Purchaser.Create();

        _purchaser.SetStatusLabel(_statusTxt);

        _purchaser.onInitializeSuccess.AddListener(OnInitializeSuccess);
        _purchaser.onPurchaseSuccess.AddListener(OnPurchaseSuccess);

        _purchaser.AddProduct(kProductIDConsumable, AppcoinsProductType.Consumable);
        _purchaser.AddProduct(kProductIDNonConsumable, AppcoinsProductType.NonConsumable);

        _purchaser.InitializePurchasing();

        _purchaser.SetupCustomValidator(this);
    }

    private void OnInitializeSuccess() {
        EnablePurchaseButtons();

        _priceTxt.text = _purchaser.GetAPPCPriceStringForSKU("gas");
        _priceFiatTxt.text = _purchaser.GetFiatPriceStringForSKU("gas");
        _currencyCodeTxt.text = _purchaser.GetFiatCurrencyCodeForSKU("gas");
            
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

    public bool IsValidPayload(string payload) {
        Debug.Log("Custom payload validation in place! Payload is " + payload);
        return true;
    }

}
