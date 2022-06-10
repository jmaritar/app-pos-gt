package com.polar502.posgt.customers

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Customers(val id: String? = null, val name: String? = null, val nit: String? = null, val phone: String? = null, val email: String? = null, @Exclude val key: String? = null)