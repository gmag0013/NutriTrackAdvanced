package com.fit2081.a1_grace_35721383.ui.StoreGenAI


import com.fit2081.a1_grace_35721383.ui.StoreGenAI.NutriTip
import com.fit2081.a1_grace_35721383.ui.StoreGenAI.NutriTipDao
import kotlinx.coroutines.flow.Flow

class NutriTipRepository(private val tipDao: NutriTipDao) {

    suspend fun insertTip(tip: NutriTip) {
        tipDao.insertTip(tip)
    }

    fun getAllTips(): Flow<List<NutriTip>> {
        return tipDao.getAllTips()
    }
}
