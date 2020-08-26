package com.krosskomics.common.repository

import androidx.lifecycle.LiveData

open class CommonRepository {
    lateinit var contacts: LiveData<List<*>>
}