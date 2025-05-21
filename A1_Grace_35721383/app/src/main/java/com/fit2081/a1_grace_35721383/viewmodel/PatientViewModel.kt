package com.fit2081.a1_grace_35721383.viewmodel

import androidx.lifecycle.*
import com.fit2081.a1_grace_35721383.data.Patient
import com.fit2081.a1_grace_35721383.repository.PatientRepository
import kotlinx.coroutines.launch

class PatientViewModel(private val repository: PatientRepository) : ViewModel() {

    private val _userId = MutableLiveData<Int>()

    // Exposes LiveData<Patient?> from Room through the repository
    val patient: LiveData<Patient?> = _userId.switchMap { id ->
        repository.getPatientById(id)
    }

    // Sets the current user ID and triggers the switchMap
    fun loadPatient(userId: Int) {
        _userId.value = userId
    }

    // One-time fetch for suspend-only contexts
    suspend fun getPatientOnce(userId: Int): Patient? {
        return repository.getPatientOnce(userId)
    }

    fun insertPatient(patient: Patient) = viewModelScope.launch {
        repository.insertPatient(patient)
    }

    fun updatePatient(patient: Patient) = viewModelScope.launch {
        repository.updatePatient(patient)
    }

    fun deletePatient(patient: Patient) = viewModelScope.launch {
        repository.deletePatient(patient)
    }
}