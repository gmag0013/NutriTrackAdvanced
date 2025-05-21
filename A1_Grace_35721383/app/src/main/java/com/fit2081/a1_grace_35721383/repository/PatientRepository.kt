package com.fit2081.a1_grace_35721383.repository

import androidx.lifecycle.LiveData
import com.fit2081.a1_grace_35721383.data.Patient
import com.fit2081.a1_grace_35721383.data.PatientDao

class PatientRepository(private val patientDao: PatientDao) {

    fun getPatientById(userId: Int): LiveData<Patient?> {
        return patientDao.getPatientLiveDataById(userId)
    }

    suspend fun getPatientOnce(userId: Int): Patient? {
        return patientDao.getPatientOnce(userId)
    }

    suspend fun insertPatient(patient: Patient) {
        patientDao.insertPatient(patient)
    }

    suspend fun updatePatient(patient: Patient) {
        patientDao.update(patient)
    }

    suspend fun deletePatient(patient: Patient) {
        patientDao.delete(patient)
    }
}