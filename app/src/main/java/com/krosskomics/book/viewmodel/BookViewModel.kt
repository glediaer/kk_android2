package com.krosskomics.book.viewmodel

import android.app.Application
import com.krosskomics.book.repository.BookRepository
import com.krosskomics.common.viewmodel.BaseViewModel

class BookViewModel(application: Application): BaseViewModel(application) {
    private val repository = BookRepository()
}