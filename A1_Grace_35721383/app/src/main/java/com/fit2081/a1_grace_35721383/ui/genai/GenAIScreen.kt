package com.fit2081.a1_grace_35721383.ui.genai

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.a1_grace_35721383.R
import com.fit2081.a1_grace_35721383.common.AppDatabase
import com.fit2081.a1_grace_35721383.viewmodel.PatientViewModel
import com.fit2081.a1_grace_35721383.viewmodel.PatientViewModelFactory
import com.fit2081.a1_grace_35721383.repository.PatientRepository
import com.fit2081.a1_grace_35721383.ui.StoreGenAI.NutriTipViewModel
import com.fit2081.a1_grace_35721383.ui.StoreGenAI.NutriTipViewModelFactory
import com.fit2081.a1_grace_35721383.ui.StoreGenAI.NutriTipRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GenAIScreen(
    defaultPrompt: String = "Generate a short encouraging message to help someone improve their fruit intake.",
    genAiViewModel: GenAiViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by genAiViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Room DAOs + ViewModels
    val db = remember { AppDatabase.getInstance(context) }

    val nutriTipViewModel: NutriTipViewModel = viewModel(
        factory = NutriTipViewModelFactory(NutriTipRepository(db.nutriTipDao()))
    )

    val patientViewModel: PatientViewModel = viewModel(
        factory = PatientViewModelFactory(PatientRepository(db.patientDao()))
    )

    // Session handling
    val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    val userId = sharedPref.getInt("userId", -1)

    val placeholderResult = stringResource(R.string.results_placeholder)
    var prompt by rememberSaveable { mutableStateOf(defaultPrompt) }
    var result by rememberSaveable { mutableStateOf(placeholderResult) }
    var showSaveButton by remember { mutableStateOf(false) }

    // Personalized prompt from patient data
    LaunchedEffect(userId) {
        if (userId != -1) {
            val patient = withContext(Dispatchers.IO) { patientViewModel.getPatientOnce(userId) }
            patient?.let {
                val name = it.name.ifBlank { "this person" }
                val gender = it.sex.lowercase().trim()
                val score = if (gender == "male") it.FruitHEIFAscoreMale else it.FruitHEIFAscoreFemale
                val roundedScore = score?.let { s -> "%.1f".format(s) } ?: "unknown"
                prompt = "$name (a $gender with a fruit HEIFA score of $roundedScore): " +
                        "Give a short and encouraging message with a fruit habit, tip, or snack idea."
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("NutriCoach AI", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Prompt") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            Button(
                onClick = {
                    showSaveButton = false
                    genAiViewModel.sendPrompt(prompt)
                },
                enabled = prompt.isNotBlank(),
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Ask Gemini")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            is UiState.Success -> {
                result = (uiState as UiState.Success).outputText
                showSaveButton = true
            }
            is UiState.Error -> {
                result = (uiState as UiState.Error).errorMessage
                showSaveButton = false
            }
            else -> {}
        }

        Text(
            text = result,
            color = if (uiState is UiState.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(top = 16.dp)
                .verticalScroll(scrollState)
                .fillMaxHeight(0.4f)
        )

        if (showSaveButton && result.isNotBlank() && uiState is UiState.Success) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    nutriTipViewModel.addTip(result)
                    showSaveButton = false
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Save Tip")
            }
        }
    }
}