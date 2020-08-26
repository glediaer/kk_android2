package com.krosskomics.common.inteface;

import androidx.annotation.Nullable;

public interface BaseDataCallBack<T> {
    void onResultForData(@Nullable T data);
}