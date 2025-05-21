package com.fit2081.a1_grace_35721383.ui.StoreGenAI


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.a1_grace_35721383.ui.StoreGenAI.NutriTip
import com.fit2081.a1_grace_35721383.ui.StoreGenAI.NutriTipRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NutriTipViewModel(private val repository: NutriTipRepository) : ViewModel() {

    val allTips: StateFlow<List<NutriTip>> = repository
        .getAllTips()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTip(tipText: String) {
        val tip = NutriTip(tip = tipText)
        viewModelScope.launch {
            repository.insertTip(tip)
        }
    }
}