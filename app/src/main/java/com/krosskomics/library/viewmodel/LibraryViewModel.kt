package com.krosskomics.library.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import com.krosskomics.common.viewmodel.FragmentBaseViewModel
import com.krosskomics.genre.repository.GenreRepository
import com.krosskomics.library.repository.LibraryRepository
import java.io.File
import java.util.*

class LibraryViewModel(context: Context): FragmentBaseViewModel(context) {
    val repository = LibraryRepository()
    // download
    var mSeriesList = ArrayList<String?>()
    var mEpExpireDateList = ArrayList<String>()
    var mPath = ""
    var mFileName = ""
    lateinit var mFile: File
    val ONE_DAY_MIL = (24 * 60 * 60 * 1000).toLong()            // 일 단위

    private val mainResponseLiveData = repository.getMainResponseLiveData()

    override fun requestMain() {
        repository.requestMain(context, page)
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}