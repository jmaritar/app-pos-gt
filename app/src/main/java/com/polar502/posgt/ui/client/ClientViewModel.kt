package com.polar502.posgt.ui.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ClientViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Client Fragment"
    }
    val text: LiveData<String> = _text
}