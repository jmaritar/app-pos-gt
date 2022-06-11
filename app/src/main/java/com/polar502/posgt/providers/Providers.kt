package com.polar502.posgt.providers

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Providers(val id: String? = null, val name: String? = null, val nit: String? = null, val phone: String? = null, val email: String? = null, val address: String? = null, val url: String? = null, @Exclude val key: String? = null)