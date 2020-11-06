package com.krosskomics.coin.activity

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.*
import com.krosskomics.R
import com.krosskomics.coin.adapter.CoinAdapter
import com.krosskomics.coin.viewmodel.CoinViewModel
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataCoin
import com.krosskomics.common.model.Coin
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.CommonUtil.toNumFormat
import com.krosskomics.util.CommonUtil.write
import com.krosskomics.util.ServerUtil.service
import kotlinx.android.synthetic.main.activity_coin.*
import kotlinx.android.synthetic.main.view_toolbar_trans.*
import kotlinx.android.synthetic.main.view_toolbar_trans.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
    var productID: String = ""
    private var mPaymentRetryCount = 1

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
        initFooterView()
    }

    private fun initFooterView() {
        emailTextView.setOnClickListener {
            CommonUtil.sendEmail(context)
        }
    }

    override fun initMainView() {
        super.initMainView()

        initInfoView()
        buyButton.isSelected = true
        buyButton.setOnClickListener {
            it.isSelected = true
            historyButton.isSelected = false
            recyclerView.visibility = View.VISIBLE
            historyWebView.visibility = View.GONE
        }

        historyButton.setOnClickListener {
            it.isSelected = true
            buyButton.isSelected = false
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
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
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
            override fun onSkuDetailsResponse(
                result: BillingResult,
                items: MutableList<SkuDetails>?
            ) {
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
        (recyclerView.adapter as RecyclerViewBaseAdapter).setOnItemClickListener(object :
            RecyclerViewBaseAdapter.OnItemClickListener {
            override fun onItemClick(item: Any?, position: Int) {
                if (item is DataCoin) {
                    if (read(context, CODE.LOCAL_loginYn, "N").equals("Y", ignoreCase = true)) {
                        skuDetailItems.forEach {
                            if (item.product_id == it.sku) {
                                requestPurchase(it)
                                productID = it.sku
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
            OK -> {
            } // Success
            BILLING_UNAVAILABLE -> {
            } // Billing API version is not supported for the type requested.
            DEVELOPER_ERROR -> {
            }   //I nvalid arguments provided to the API.
            ERROR -> {
            } // Fatal error during the API action.
            FEATURE_NOT_SUPPORTED -> {
            } // Requested feature is not supported by Play Store on the current device.
            ITEM_ALREADY_OWNED -> {
//                val purchasesResult: PurchasesResult =
//                    billingClient.queryPurchases(BillingClient.SkuType.INAPP)
//                onPurchasesUpdated(
//                    BillingClient.BillingResponseCode.OK,
//                    purchasesResult.purchasesList
//                )
            } // Failure to purchase since item is already owned.
            ITEM_NOT_OWNED -> {
            } // Failure to consume since item is not owned.
            ITEM_UNAVAILABLE -> {
            } // Requested product is not available for purchase.
            SERVICE_DISCONNECTED -> {
            } // Play Store service is not connected now - potentially transient state.
            SERVICE_TIMEOUT -> {
            } // The request has reached the maximum timeout before Google Play responds.
            SERVICE_UNAVAILABLE -> {
            } // Network connection is down.
            USER_CANCELED -> {
            } // User pressed back or canceled a dialog.
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            commonAlert(this@CoinActivity, getString(R.string.msg_cancel_pay))
        } else {
            // Handle any other error codes.
            commonAlert(
                this@CoinActivity,
                billingResult.debugMessage
            )
        }
    }

    fun handlePurchase(purchase: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build()

        billingClient.consumeAsync(consumeParams, { billingResult, outToken ->
            if (billingResult.responseCode == OK) {
                // Handle the success of the consume operation.
                savePayment(purchase)
            }
        })
    }

    private fun savePayment(purchase: Purchase?) {
        if (!this@CoinActivity.isFinishing) {
//            showProgress(context)
            if (purchase == null) {
//                commonAlert(CoinActivity.this, "구매결과정보가 없어서 코인 충전 요청을 하지 않습니다.");
                return
            }

//            logger.info("결제 성공(purchase 있음), 코인 충전 request 시작");
            val api = service.getInappPurchase(
                productID, purchase.orderId,
                purchase.purchaseTime, purchase.purchaseState, ""
//                , purchase.getToken()
            )
            api.enqueue(object : Callback<Coin?> {
                override fun onResponse(call: Call<Coin?>, response: Response<Coin?>) {
                    try {
                        if (response.isSuccessful) {
                            val body = response.body()
                            if ("00" == body!!.retcode) {
//                                logger.debug("구매, 코인 충전 성공했으며 현재 " + body.user_coin + " 코인이 있습니다.");
                                val coin = java.lang.String.valueOf(body.user_coin)
                                if ("" != coin) {
//                                    Intent intent = new Intent(CODE.LB_CHARGE_COIN);
//                                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                    write(context, CODE.LOCAL_coin, coin)
                                    val numFormatCoin = toNumFormat(coin.toInt())
                                    keyTextView.text =
                                        numFormatCoin + " " + getString(R.string.str_coins)
                                }
                                //                                requestServer();
                            } else {
//                                logger.error("구매 성공, 코인 충전 실패했습니다. 리턴코드는 : " + body.retcode + "입니다.");
                            }
                            if ("" != body.msg) {
                                commonAlert(this@CoinActivity, body.msg)
                            }
                        } else {
//                            logger.error("코인 충전 서버와의 통신은 성공했지만 응답이 실패했습니다.");
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        //                        logger.error("코인 충전중 예외상황발생 msg : " + e.getMessage());
                        commonAlert(this@CoinActivity, getString(R.string.msg_fail_inapp_give_coin))
                    }
                }

                override fun onFailure(call: Call<Coin?>, t: Throwable) {
                    t.printStackTrace()
                    if (mPaymentRetryCount <= 3) {
//                        logger.error(mPaymentRetryCount + "번째 결제 실패했습니다. 다시 코인 충전 요청합니다. 에러메시지 : " + t.getMessage());
                        savePayment(purchase)
                        mPaymentRetryCount++
                    } else {
                        try {
                            checkNetworkConnection(this@CoinActivity, t, errorView)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        }
    }
}