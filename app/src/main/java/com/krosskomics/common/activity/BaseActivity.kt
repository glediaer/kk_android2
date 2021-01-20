package com.krosskomics.common.activity

import android.app.ActivityOptions
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.transition.Slide
import android.view.*
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.analytics.HitBuilders
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.coin.activity.CoinActivity
import com.krosskomics.comment.activity.CommentActivity
import com.krosskomics.library.activity.LibraryActivity
import com.krosskomics.login.activity.LoginActivity
import com.krosskomics.login.activity.LoginIntroActivity
import com.krosskomics.mainmenu.activity.*
import com.krosskomics.search.activity.SearchActivity
import com.krosskomics.splash.SplashActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.getNetworkInfo
import com.krosskomics.util.CommonUtil.hideErrorView
import com.krosskomics.util.CommonUtil.showErrorView
import com.krosskomics.util.CommonUtil.showToast
import kotlinx.android.synthetic.main.activity_series.*
import kotlinx.android.synthetic.main.view_main_action_item.*
import kotlinx.android.synthetic.main.view_toolbar.*

abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {

    protected lateinit var context: Context
    protected var recyclerViewItemLayoutId = 0
    lateinit var toolbarTitleString: String
    private var mProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            with(window) {
//                requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
//                // set an slide transition
//                enterTransition = Slide(Gravity.END)
//                exitTransition = Slide(Gravity.START)
//            }
//        }
//        overridePendingTransition(R.anim.slide_in_left, R.anim.stay)
        super.onCreate(savedInstanceState)
        context = this@BaseActivity
        setContentView(getLayoutId())
        initModel()
        initLayout()
        requestServer()
        initTracker()
        initErrorView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
            mProgressDialog = null
        }
        super.onDestroy()
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
//    }

    // fragment 추가
    fun addFragment(fragment: Fragment, fragmentId: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(fragmentId, fragment)
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun replaceFragment(fragment: Fragment, fragmentId: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(fragmentId, fragment)
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.stay)
        fragmentTransaction.commitAllowingStateLoss()
    }

    abstract fun getLayoutId(): Int
    abstract fun initModel()
    abstract fun initLayout()
    abstract fun requestServer()
    abstract fun initTracker()
    abstract fun initErrorView()

    open fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.icon_back)
        }
        giftboxImageView?.setOnClickListener(this)
        searchImageView?.setOnClickListener(this)
    }

    open fun goLoginAlert(context: Context?) {
        val intent = if ("0" == CommonUtil.read(context, CODE.IS_RUN_FIRST_LOGIN, "0")) {
            CommonUtil.write(context, CODE.IS_RUN_FIRST_LOGIN, "1")
            Intent(this, LoginIntroActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
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
            } else {
                hideErrorView(errorView)
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

    open fun commonAlert(context: Context?, msg: String?) {
        if (!this.isFinishing) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    open fun moveSignUp(context: Context?) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("pageType", "signup")
        startActivity(intent)
    }

    open fun showProgress(context: Context) {
        try {
            if (KJKomicsApp.IS_ENTER_OFFLINE && getNetworkInfo(context) != null) {
                KJKomicsApp.IS_ENTER_OFFLINE = false
                showToast(context.getString(R.string.msg_reloading_init_data), context)
                val intent = Intent(context, SplashActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                return
            }
            loadingView?.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun hideProgress() {
        loadingView?.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.searchImageView -> startActivity(Intent(context, SearchActivity::class.java))
            R.id.giftboxImageView -> {
                if (CommonUtil.read(context, CODE.LOCAL_loginYn, "N").equals("Y", ignoreCase = true)) {
                    intent = Intent(context, LibraryActivity::class.java)
                    startActivity(intent)
                } else {
                    goLoginAlert(context)
                }
            }
            // tabview
            R.id.homeButton -> finish()
//            R.id.onGoingButton -> {
//                val intent = Intent(context, MainMenuActivity::class.java).apply {
//                    putExtra("tabIndex", 1)
//                }
//                startActivity(intent)
//                finish()
//            }
//            R.id.waitButton -> {
//                val intent = Intent(context, MainMenuActivity::class.java).apply {
//                    putExtra("tabIndex", 2)
//                }
//                startActivity(intent)
//                finish()
//            }
//            R.id.rankingButton -> {
//                val intent = Intent(context, MainMenuActivity::class.java).apply {
//                    putExtra("tabIndex", 3)
//                }
//                startActivity(intent)
//                finish()
//            }
//            R.id.genreButton -> {
//                val intent = Intent(context, MainMenuActivity::class.java).apply {
//                    putExtra("tabIndex", 4)
//                }
//                startActivity(intent)
//                finish()
//            }
        }
    }
}