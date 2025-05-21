package com.fit2081.a1_grace_35721383.ui.login


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.fit2081.a1_grace_35721383.common.AppDatabase
import com.fit2081.a1_grace_35721383.ui.food.FoodIntakeQuestions
import com.fit2081.a1_grace_35721383.ui.theme.A1_Grace_35721383Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PasswordLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("userId", -1)
        enableEdgeToEdge()

        setContent {
            A1_Grace_35721383Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = Color.White
                    ) {
                        PasswordLoginScreen(context = this@PasswordLoginActivity, userId = userId)
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordLoginScreen(context: Context, userId: Int) {
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter Password",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                loginError = false
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getInstance(context)
                    val patient = db.patientDao().getPatientOnce(userId)

                    withContext(Dispatchers.Main) {
                        if (patient != null && patient.password == password) {
                            context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                                .edit().putInt("userId", userId).apply()
                            context.startActivity(Intent(context, FoodIntakeQuestions::class.java))
                        } else {
                            loginError = true
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Login", color = Color.White)
        }

        if (loginError) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Incorrect password. Please try again.",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}
