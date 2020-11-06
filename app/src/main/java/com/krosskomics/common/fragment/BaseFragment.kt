package com.krosskomics.common.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.coin.activity.CoinActivity
import com.krosskomics.common.viewmodel.FragmentBaseViewModel
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.splash.SplashActivity
import com.krosskomics.util.CommonUtil
import kotlinx.android.synthetic.main.activity_series.*
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.android.synthetic.main.fragment_library.loadingView
import kotlinx.android.synthetic.main.view_empty_library.*

abstract class BaseFragment : Fragment(), Observer<Any> {
    protected var recyclerViewItemLayoutId = 0

    protected open val viewModel: FragmentBaseViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return FragmentBaseViewModel(requireContext()) as T
            }
        }).get(FragmentBaseViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(),container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initModel()
        initLayout()
        requestServer()
    }

    abstract fun getLayoutId(): Int
    abstract fun initModel()
    abstract fun initLayout()
    abstract fun requestServer()

    open fun getCurrentItem(recyclerView: RecyclerView): Int {
        return (recyclerView.layoutManager as LinearLayoutManager)
            .findFirstVisibleItemPosition()
    }

    open fun showMainView() {
        mainView?.visibility = View.VISIBLE
        emptyView?.visibility = View.GONE
    }

    open fun showEmptyView() {
        mainView?.visibility = View.GONE
        emptyView?.visibility = View.VISIBLE
        goSeriesButton?.setOnClickListener {
            val intent = Intent(context, SeriesActivity::class.java)
            val b = Bundle()
//            b.putString("cid", item.sid)
//            b.putString("title", item.title)
            intent.putExtras(b)
            requireContext().startActivity(intent)
        }
    }

    open fun showProgress(context: Context) {
        loadingView?.visibility = View.VISIBLE
    }

    open fun hideProgress() {
        loadingView?.visibility = View.GONE
    }

    open fun goCoinAlert(context: Context?) {
        val intent = Intent(context, CoinActivity::class.java)
        startActivity(intent)
    }
}