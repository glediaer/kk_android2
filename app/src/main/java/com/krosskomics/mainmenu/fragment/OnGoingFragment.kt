package com.krosskomics.mainmenu.fragment

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.fragment.RecyclerViewBaseFragment
import com.krosskomics.genre.activity.GenreDetailActivity
import com.krosskomics.mainmenu.viewmodel.MainMenuViewModel
import com.krosskomics.util.CommonUtil
import kotlinx.android.synthetic.main.view_ongoing_date.*

class OnGoingFragment : RecyclerViewBaseFragment() {

    override val viewModel: MainMenuViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainMenuViewModel(requireContext()) as T
            }
        }).get(MainMenuViewModel::class.java)
    }

    lateinit var dateViewItems: ArrayList<View>
    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_ongoing
        return R.layout.fragment_ongoing
    }

    override fun initModel() {
        arguments?.let {
            viewModel.listType = it.getString("listType").toString()
            val position = it.getInt("position")
            viewModel.param2 = (context as GenreDetailActivity).lazyTabItems[position].p_genre
        }
        super.initModel()
    }

    override fun initLayout() {
        viewModel.tabIndex = 1
        viewModel.param1 = "ongoing"
        super.initLayout()
        initDateView()
    }

    private fun initDateView() {
        dateViewItems = arrayListOf(monView, tueView, wedView, thuView, friView, satView, sunView)
        // 오늘 요일 얻어오기
        val selectIndex = if (CommonUtil.getDayWeek() == 0) {
            6
        } else {
            CommonUtil.getDayWeek() - 2
        }
        resetDateViewItems(selectIndex)
        dateViewItems.forEachIndexed { index, dateView ->
            dateView.setOnClickListener {
                resetDateViewItems()
                it.isSelected = true
                viewModel.isRefresh = true
                viewModel.param2 = it.tag as String
//                convertDayToString(index)
                requestServer()
            }
        }
    }

    private fun resetDateViewItems(selectIndex: Int = -1) {
        dateViewItems.forEachIndexed { index, _ ->
            val item = dateViewItems[index]
            item.isSelected = selectIndex == index
        }
    }
}