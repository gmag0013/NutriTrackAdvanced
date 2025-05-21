package com.fit2081.a1_grace_35721383.ui.genai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GenAiViewModel : ViewModel() {

    // Backing state for UI
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Generative AI model instance (consider externalizing key in production)
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyB6kTtZkEVqUDQ0fiue0lfHgpszL0u-YCk" // WARNING: Do not commit keys to source control
    )

    fun sendPrompt(prompt: String) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(content { text(prompt) })
                val outputText = response.text ?: "No response from Gemini model."
                _uiState.value = UiState.Success(outputText)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "An unexpected error occurred.")
            }
        }
    }
}