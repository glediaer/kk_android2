package com.krosskomics.book.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.book.repository.BookRepository
import com.krosskomics.common.model.InitSet
import com.krosskomics.common.model.Main
import com.krosskomics.common.viewmodel.BaseViewModel

class BookViewModel(application: Application): BaseViewModel(application) {
    private val repository = BookRepository()
}