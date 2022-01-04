package com.mvi.sample.login.cqrs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mvi.sample.FeatureBuilder
import com.mvi.sample.StreamData

class LoginViewModel(
    override val commands: MutableLiveData<StreamData> = MutableLiveData(),
    override val queries: MutableLiveData<StreamData> = MutableLiveData()
) : ViewModel(), FeatureBuilder.ViewModelFilter


