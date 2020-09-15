package com.krosskomics.settings.activity

import android.content.Intent
import android.graphics.Paint
import android.os.Process
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.krosskomics.KJKomicsApp
import com.krosskomics.KJKomicsApp.Companion.LATEST_APP_VERSION_CODE
import com.krosskomics.R
import com.krosskomics.book.activity.BookActivity
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.common.activity.RecyclerViewBaseActivity
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.model.More
import com.krosskomics.common.model.Version
import com.krosskomics.genre.activity.GenreActivity
import com.krosskomics.library.activity.LibraryActivity
import com.krosskomics.ongoing.adapter.OnGoingAdapter
import com.krosskomics.ongoing.viewmodel.OnGoingViewModel
import com.krosskomics.ranking.activity.RankingActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.waitfree.activity.WaitFreeActivity
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.view_main_tab.*
import kotlinx.android.synthetic.main.view_topbutton.*

class ChangeNickNameActivity : ToolbarTitleActivity() {
    private val TAG = "SettingsActivity"

    override fun getLayoutId(): Int {
        return R.layout.activity_settings
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_settings))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_settings)
        super.initLayout()

        setLoginSignView()
        setAppVersionView()
    }

    private fun setLoginSignView() {
        changeNicknameView.setOnClickListener(this)
        changeEmailView.setOnClickListener(this)
        changePwView.setOnClickListener(this)
        deleteAccountView.setOnClickListener(this)
        if (CommonUtil.read(context, CODE.LOCAL_loginYn, "N").equals("Y", ignoreCase = true)) {

        } else {
            accountLoginView.visibility = View.GONE
            notificationView.visibility = View.GONE
            notificationUnderbar.visibility = View.GONE
            loginSignupView.setOnClickListener(this)
        }
    }

    private fun setAppVersionView() {
        var spannableString = SpannableString(CommonUtil.getAppVersion(context))
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
        latestVerTextView.text = spannableString

        spannableString = SpannableString(CommonUtil.getAppVersion(context))
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
        currentVerTextView.text = spannableString

        if (LATEST_APP_VERSION_CODE > CommonUtil.getVersionCode(context)) {
            latestVerTextView.setOnClickListener {
                // 팝업띄움
                showUpdateAlert()
            }
        }
    }

    /**
     * 버전 팝업
     */
    private fun showUpdateAlert() {
        try {
            val innerView =
                layoutInflater.inflate(R.layout.dialog_app_version, null)
            val dialog = initDialog(innerView)
            val tvMsg1 = innerView.findViewById<TextView>(R.id.currentVerTextView)
            val tvMsg2 = innerView.findViewById<TextView>(R.id.latestVerTextView)
            val btnConfirm =
                innerView.findViewById<TextView>(R.id.downloadTextView)

            tvMsg1.text = CommonUtil.getAppVersion(context)
            tvMsg2.text = CommonUtil.getAppVersion(context)

            btnConfirm.setOnClickListener {
                dialog.dismiss()
                CommonUtil.moveAppMarket(context, CODE.MARKET_URL)
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}