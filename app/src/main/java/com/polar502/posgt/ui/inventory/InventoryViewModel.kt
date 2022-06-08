package com.polar502.posgt.ui.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class InventoryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Inventori Fragment..."
    }
    val text: LiveData<String> = _text

}