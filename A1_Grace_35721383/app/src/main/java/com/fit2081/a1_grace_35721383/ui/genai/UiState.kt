package com.fit2081.a1_grace_35721383.ui.genai

//Represents the different UI states for the GenAI screen.
sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    data class Success(val outputText: String) : UiState()
    data class Error(val errorMessage: String) : UiState()
}