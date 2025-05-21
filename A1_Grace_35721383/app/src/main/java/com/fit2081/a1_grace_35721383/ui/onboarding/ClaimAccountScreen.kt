package com.fit2081.a1_grace_35721383.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.fit2081.a1_grace_35721383.common.AppDatabase
import com.fit2081.a1_grace_35721383.ui.home.HomeScreen1
import com.fit2081.a1_grace_35721383.ui.theme.A1_Grace_35721383Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClaimAccountActivity : ComponentActivity() {
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
                        ClaimAccountScreen(userId = userId)
                    }
                }
            }
        }
    }
}
@Composable
fun ClaimAccountScreen(userId: Int) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val patientDao = db.patientDao()

    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var matchError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Claim Your Account", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = false
            },
            label = { Text("Enter your name") },
            isError = nameError,
            modifier = Modifier.fillMaxWidth()
        )
        if (nameError) {
            Text("Name is required", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
            },
            label = { Text("Set a password") },
            isError = passwordError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        if (passwordError) {
            Text("Password must be at least 4 characters", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                matchError = false
            },
            label = { Text("Confirm password") },
            isError = matchError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        if (matchError) {
            Text("Passwords do not match", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                nameError = name.isBlank()
                passwordError = password.length < 4
                matchError = confirmPassword != password

                if (!nameError && !passwordError && !matchError) {
                    CoroutineScope(Dispatchers.IO).launch {
                        patientDao.updateNameAndPassword(userId, name, password)

                        with(context.getSharedPreferences("UserSession", Context.MODE_PRIVATE).edit()) {
                            putInt("userId", userId)
                            apply()
                        }

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Account claimed successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            context.startActivity(Intent(context, HomeScreen1::class.java))
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("Claim Account", color = Color.White)
        }
    }
}