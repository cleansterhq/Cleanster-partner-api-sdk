package com.cleanster.android.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("status")  val status: Int    = 0,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data")    val data: T?        = null,
)
