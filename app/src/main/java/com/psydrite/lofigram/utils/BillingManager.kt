package com.psydrite.lofigram.utils

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.QueryProductDetailsParams


class BillingManager (
    private val context: Activity
){
    private val billingclient = BillingClient.newBuilder(context)
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null){
                for (purchase in purchases) {
                    val productList = purchase.products
                    productList.forEach { productId ->
                        val orderId: String = purchase.orderId.toString()
                        val productid: String = productId
                        PurchasesList = PurchasesList + PurchaseDataType(orderId, productid)
                    }
                    CheckForPurchase = !CheckForPurchase
                }
            }
        }.enablePendingPurchases()
        .build()

    init {
        startBillingConnection()
    }

    fun startBillingConnection(){
        billingclient.startConnection(object : BillingClientStateListener{
            override fun onBillingServiceDisconnected() {
                // Retry if needed
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("purchases", "billing response is OK")
                }
            }
        })
    }

    fun launchPurchaseFlow(productId: String) {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingclient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                val productDetails = productDetailsList[0]

                val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .build()

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(listOf(productDetailsParams))
                    .build()

                billingclient.launchBillingFlow(context, billingFlowParams)
            }
        }
    }
}