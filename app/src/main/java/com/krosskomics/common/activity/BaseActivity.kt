package com.krosskomics.common.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.analytics.HitBuilders
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.coin.activity.CoinActivity
import com.krosskomics.login.activity.LoginIntroActivity
import com.krosskomics.util.CommonUtil.getNetworkInfo
import com.krosskomics.util.CommonUtil.showErrorView
import com.krosskomics.util.CommonUtil.showToast
import kotlinx.android.synthetic.main.view_toolbar.*

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var context: Context
    protected var recyclerViewItemLayoutId = 0
    lateinit var toolbarTitleString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this@BaseActivity
        setContentView(getLayoutId())
        initModel()
        initLayout()
        requestServer()
        initTracker()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    // fragment 추가
//    fun addFragment(fragment: Fragment) {
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//
//        fragmentTransaction.add(R.id.fragment_container, fragment)
//        fragmentTransaction.commitAllowingStateLoss()
//    }

    abstract fun getLayoutId(): Int
    abstract fun initModel()
    abstract fun initLayout()
    abstract fun requestServer()
    abstract fun initTracker()

    open fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.icon_back)
        }
    }

    open fun goLoginAlert(context: Context?) {
        val intent = Intent(this, LoginIntroActivity::class.java)
        startActivity(intent)
    }

    open fun goCoinAlert(context: Context?) {
        val intent = Intent(this, CoinActivity::class.java)
        startActivity(intent)
    }

    open fun getCurrentItem(recyclerView: RecyclerView): Int {
        return (recyclerView.layoutManager as LinearLayoutManager)
            .findFirstVisibleItemPosition()
    }

    open fun setTracker(screenName: String) {
        // Get tracker.
        val tracker = (application as KJKomicsApp).getTracker(KJKomicsApp.TrackerName.APP_TRACKER)
        tracker?.setScreenName(screenName)
        tracker?.send(HitBuilders.ScreenViewBuilder().build())
    }

    open fun shareUrl(shareUrl: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, shareUrl)
        startActivity(Intent.createChooser(intent, "Share"))
    }

    open fun checkNetworkConnection(
        context: Context?,
        @Nullable throwable: Throwable?,
        errorView: View?
    ) {
        if (errorView != null) {
            if (getNetworkInfo(context!!) == null) {   // 네트워크 연결 안된상태
                showErrorView(errorView)
            }
        } else {
            if (null != throwable) {
                showToast(
                    getString(R.string.msg_error_server) + " : " + throwable.message,
                    context
                )
            } else {
                showToast(getString(R.string.msg_error_server), context)
            }
        }
    }

    protected fun initDialog(view: View): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        val params: ViewGroup.LayoutParams = dialog.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params as WindowManager.LayoutParams
        dialog.show()
        return dialog
    }
}