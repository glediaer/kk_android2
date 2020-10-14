package com.krosskomics.coin.activity

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.*
import com.android.billingclient.api.Purchase.PurchasesResult
import com.krosskomics.R
import com.krosskomics.coin.adapter.CoinAdapter
import com.krosskomics.coin.viewmodel.CoinViewModel
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataCoin
import com.krosskomics.common.model.Coin
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil.read
import kotlinx.android.synthetic.main.activity_coin.*
import kotlinx.android.synthetic.main.view_toolbar_black.*
import kotlinx.android.synthetic.main.view_toolbar_black.view.*


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

    private var skuDetailItems = ArrayList<SkuDetails>()
    private lateinit var billingClient: BillingClient

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_coin
        return R.layout.activity_coin
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_keyshop))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_keyshop)
        super.initLayout()
        initHeaderView()
    }

    override fun initMainView() {
        super.initMainView()

        initInfoView()
        buyButton.setOnClickListener {
            recyclerView.visibility = View.VISIBLE
            historyWebView.visibility = View.GONE
        }

        historyButton.setOnClickListener {
            historyWebView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }

    }

    private fun initInfoView() {
        closeInfoImageView.setOnClickListener {
            coinInfoView.visibility = View.GONE
        }
        coinInfoView.setOnClickListener { coinInfoView.visibility = View.GONE }
    }

    private fun initHeaderView() {
        keyTextView.text = read(context, CODE.LOCAL_coin, "0")
    }

    override fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.kk_icon_back_white)
        }
        toolbarTitle.text = toolbarTitleString
        toolbar.toolbarTrash.visibility = View.GONE
        toolbar.toolbarInfo.visibility = View.VISIBLE
        toolbar.toolbarInfo.setOnClickListener {
            if (coinInfoView.isShown) {
                coinInfoView.visibility = View.GONE
            } else {
                coinInfoView.visibility = View.VISIBLE
            }
        }
    }

    private fun initInApp() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchaseUpdateListener)
            .enablePendingPurchases()
            .build()
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
        (viewModel.items as ArrayList<DataCoin>).forEach {
            skuList.add(it.product_id)
        }
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(params.build(), object : SkuDetailsResponseListener {
            override fun onSkuDetailsResponse(result: BillingResult, items: MutableList<SkuDetails>?) {
                skuDetailItems.addAll(items!!)
            }
        })
        // Process the result.
    }

    override fun onChanged(t: Any?) {
        if (t is Coin) {
            if ("00" == t.retcode) {
                setMainContentView(t)
                initInApp()
            }
        }
    }

    override fun initRecyclerViewAdapter() {
        recyclerView.adapter = CoinAdapter(viewModel.items, recyclerViewItemLayoutId)
        (recyclerView.adapter as RecyclerViewBaseAdapter).setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
            override fun onItemClick(item: Any?) {
                if (item is DataCoin) {
                    if (read(context, CODE.LOCAL_loginYn, "N").equals("Y", ignoreCase = true)) {
                        skuDetailItems.forEach {
                            if (item.product_id == it.sku) {
                                requestPurchase(it)
                                return
                            }
                        }
                    } else {
                        goLoginAlert(context)
                    }
                }
            }
        })
    }

    private fun requestPurchase(skuDetails: SkuDetails) {
        // An activity reference from which the billing flow will be launched.

// Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        val responseCode = billingClient.launchBillingFlow(this@CoinActivity, flowParams).responseCode
        when (responseCode) {
            OK -> {} // Success
            BILLING_UNAVAILABLE -> {} // Billing API version is not supported for the type requested.
            DEVELOPER_ERROR -> {}   //I nvalid arguments provided to the API.
            ERROR -> {} // Fatal error during the API action.
            FEATURE_NOT_SUPPORTED -> {} // Requested feature is not supported by Play Store on the current device.
            ITEM_ALREADY_OWNED -> {
//                val purchasesResult: PurchasesResult =
//                    billingClient.queryPurchases(BillingClient.SkuType.INAPP)
//                onPurchasesUpdated(
//                    BillingClient.BillingResponseCode.OK,
//                    purchasesResult.purchasesList
//                )
            } // Failure to purchase since item is already owned.
            ITEM_NOT_OWNED -> {} // Failure to consume since item is not owned.
            ITEM_UNAVAILABLE -> {} // Requested product is not available for purchase.
            SERVICE_DISCONNECTED -> {} // Play Store service is not connected now - potentially transient state.
            SERVICE_TIMEOUT -> {} // The request has reached the maximum timeout before Google Play responds.
            SERVICE_UNAVAILABLE -> {} // Network connection is down.
            USER_CANCELED -> {} // User pressed back or canceled a dialog.
        }
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