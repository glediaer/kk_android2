package com.krosskomics.coin.activity

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.*
import com.krosskomics.R
import com.krosskomics.coin.viewmodel.CoinViewModel
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataCoin
import com.krosskomics.common.model.Coin
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil.read
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CoinActivity : ToolbarTitleActivity(), PurchasesUpdatedListener {
    private val TAG = "CoinActivity"

    override val viewModel: CoinViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CoinViewModel(application) as T
            }
        }).get(CoinViewModel::class.java)
    }

    private val purchaseUpdateListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
        }

    private var skuDetails: MutableList<SkuDetails>? = null

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchaseUpdateListener)
        .enablePendingPurchases()
        .build()

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_coin
        return R.layout.activity_coin
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_coin_shop))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_coin_shop)
        super.initLayout()
        initInApp()
    }

    private fun initInApp() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    querySkuDetails()
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    fun querySkuDetails() {
        val skuList = ArrayList<String>()
        skuList.add("premium_upgrade")
        skuList.add("gas")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
//        val skuDetailsResult = withContext(Dispatchers.IO) {
//
//        }
        billingClient.querySkuDetailsAsync(params.build(), object : SkuDetailsResponseListener {
            override fun onSkuDetailsResponse(result: BillingResult, items: MutableList<SkuDetails>?) {
                skuDetails = items
            }

        })
        // Process the result.
    }

    override fun onChanged(t: Any?) {
        if (t is Coin) {
            if ("00" == t.retcode) {
                setMainContentView(t)
            }
        }
    }

    override fun initRecyclerViewAdapter() {
        recyclerView.adapter = RecyclerViewBaseAdapter(viewModel.items, recyclerViewItemLayoutId)
        (recyclerView.adapter as RecyclerViewBaseAdapter).setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
            override fun onItemClick(item: Any?) {
                if (item is DataCoin) {
                    if (read(context, CODE.LOCAL_loginYn, "N").equals("Y", ignoreCase = true)) {
//                        if (null != arr_coin && 0 < arr_coin.size) {
//                            data = arr_coin.get(position)
//                        }
//                        // 결제 요청 팝업
//                        payment(data)
                        requestPurchase()
                    } else {
                        goLoginAlert(context)
                    }
                }
            }
        })
    }

    private fun requestPurchase() {
        // An activity reference from which the billing flow will be launched.

// Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
//        val flowParams = BillingFlowParams.newBuilder()
//            .setSkuDetails(skuDetails)
//            .build()
//        val responseCode = billingClient.launchBillingFlow(this@CoinActivity, flowParams).responseCode
//        when (responseCode) {
//            OK -> {} // Success
//            BILLING_UNAVAILABLE -> {} // Billing API version is not supported for the type requested.
//            DEVELOPER_ERROR -> {}   //I nvalid arguments provided to the API.
//            ERROR -> {} // Fatal error during the API action.
//            FEATURE_NOT_SUPPORTED -> {} // Requested feature is not supported by Play Store on the current device.
//            ITEM_ALREADY_OWNED -> {} // Failure to purchase since item is already owned.
//            ITEM_NOT_OWNED -> {} // Failure to consume since item is not owned.
//            ITEM_UNAVAILABLE -> {} // Requested product is not available for purchase.
//            SERVICE_DISCONNECTED -> {} // Play Store service is not connected now - potentially transient state.
//            SERVICE_TIMEOUT -> {} // The request has reached the maximum timeout before Google Play responds.
//            SERVICE_UNAVAILABLE -> {} // Network connection is down.
//            USER_CANCELED -> {} // User pressed back or canceled a dialog.
//        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }

    fun handlePurchase(purchase: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build()

        billingClient.consumeAsync(consumeParams, { billingResult, outToken ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Handle the success of the consume operation.
            }
        })
    }
}