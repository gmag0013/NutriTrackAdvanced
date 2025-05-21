package com.fit2081.a1_grace_35721383.ui.StoreGenAI


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fit2081.a1_grace_35721383.ui.StoreGenAI.NutriTipRepository

class NutriTipViewModelFactory(
    private val repository: NutriTipRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NutriTipViewModel::class.java)) {
            return NutriTipViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
