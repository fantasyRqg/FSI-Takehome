package com.example.hairsalonappointments.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class FilterCriteria(
    val stylistId: String? = null,
    val serviceTypeId: String? = null,
    val status: String? = null
) : Parcelable