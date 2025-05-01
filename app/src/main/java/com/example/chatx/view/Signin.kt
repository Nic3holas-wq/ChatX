package com.example.chatx.view

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Signin(navController: NavHostController) {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val phoneNumber = remember { mutableStateOf("") }
    val otp = remember { mutableStateOf("") }
    val verificationId = remember { mutableStateOf<String?>(null) }

    fun sendVerificationCode() {
        val activity = context as? Activity
        if (activity != null) {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+254${phoneNumber.value}")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    navController.navigate("home") {
                                        popUpTo("signin") { inclusive = true }
                                    }
                                }
                            }
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCodeSent(verificationIdParam: String, token: PhoneAuthProvider.ForceResendingToken) {
                        verificationId.value = verificationIdParam
                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } else {
            Toast.makeText(context, "Activity context is required", Toast.LENGTH_SHORT).show()
        }
    }

    fun verifyCode() {
        val credential = PhoneAuthProvider.getCredential(verificationId.value!!, otp.value)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate("home") {
                        popUpTo("signin") { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "Invalid Code", Toast.LENGTH_SHORT).show()
                }
            }
    }

    Column(
        modifier = Modifier
            .padding(top = 80.dp, start = 5.dp, end = 5.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ChatX",
            color = Color(0xFF0096C7),
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Welcome!",
            color = Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "To continue, enter phone number to create account",
            color = Color.Black,
            fontSize = 16.sp
        )
        OutlinedTextField(
            modifier = Modifier.padding(20.dp),
            value = phoneNumber.value,
            label = { Text("Phone Number") },
            textStyle = TextStyle(fontSize = 24.sp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF0096C7),
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.Black,
                cursorColor = Color(0xFF0096C7),
                focusedLabelColor = Color(0xFF0096C7)
            ),
            onValueChange = {
                if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                    phoneNumber.value = it
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true
        )
        Button(
            onClick = { sendVerificationCode() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0096C7),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Send OTP", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        if (verificationId.value != null) {
            OutlinedTextField(
                value = otp.value,
                onValueChange = { if (it.length <= 6) otp.value = it },
                label = { Text("Enter OTP") },
                textStyle = TextStyle(fontSize = 24.sp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Button(
                onClick = { verifyCode() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0096C7)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Verify", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
