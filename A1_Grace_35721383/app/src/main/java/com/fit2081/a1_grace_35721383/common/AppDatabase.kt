package com.fit2081.a1_grace_35721383.common

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fit2081.a1_grace_35721383.data.Patient
import com.fit2081.a1_grace_35721383.data.PatientDao
import com.fit2081.a1_grace_35721383.ui.StoreGenAI.NutriTip
import com.fit2081.a1_grace_35721383.ui.StoreGenAI.NutriTipDao
import com.fit2081.a1_grace_35721383.ui.food.FoodIntake
import com.fit2081.a1_grace_35721383.ui.food.FoodIntakeDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Patient::class, FoodIntake::class, NutriTip::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun foodIntakeDao(): FoodIntakeDao
    abstract fun nutriTipDao(): NutriTipDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database_v2"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                                val alreadyLoaded = sharedPref.getBoolean("csv_loaded", false)

                                if (!alreadyLoaded) {
                                    val patients = DataManager.loadPatientDataFromCsv(context)
                                    INSTANCE?.let { database ->
                                        database.patientDao().insertAll(patients)
                                        sharedPref.edit().putBoolean("csv_loaded", true).apply()
                                        //log, remove once done
                                        Log.d("CSV", "Loading CSV data: ${patients.size} patients")
                                    }
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()  // ✅ Rebuilds DB if schema changes
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}