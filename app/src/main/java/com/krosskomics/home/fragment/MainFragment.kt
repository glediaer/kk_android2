package com.krosskomics.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.fragment.BaseFragment
import com.krosskomics.databinding.FragmentMainBinding
import com.krosskomics.home.viewmodel.MainViewModel

class MainFragment : BaseFragment(), Observer<Any> {

//    lateinit var viewModel: MainViewModel
//    private val viewModel: MainViewModel by lazy {
//        ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                return MainViewModel() as T
//            }
//        }).get(MainViewModel::class.java)
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val binding: FragmentMainBinding = DataBindingUtil.
        inflate(inflater,R.layout.fragment_main, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        viewModel.updateActivityCount.observe(this, this)
    }

    override fun onChanged(t: Any?) {

    }
}