package com.example.chatx.view

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.colorspace.WhitePoint
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.example.chatx.model.data.AppDatabase
import com.example.chatx.model.data.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.grpc.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Signin(navController: NavHostController) {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val phoneNumber = remember { mutableStateOf("") }
    val otp = remember { mutableStateOf("") }
    val verificationId = remember { mutableStateOf<String?>(null) }
    var isLoading by remember{ mutableStateOf(false)}
    var otpSent  by remember { mutableStateOf(false) }
    val instructionText = if (otpSent) "Enter OTP" else "To continue, enter phone number to create account"

    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()

    fun sendVerificationCode() {
        val activity = context as? Activity
        if (activity != null) {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+254${phoneNumber.value}")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        isLoading = false
                        otpSent = true
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        userDao.insertUser(User(phone = "+254${phoneNumber.value}", isLoggedIn = true))
                                    }
                                    navController.navigate("home") {
                                        popUpTo("signin") { inclusive = true }
                                    }
                                }
                            }
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        isLoading = false
                        Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    override fun onCodeSent(verificationIdParam: String, token: PhoneAuthProvider.ForceResendingToken) {
                        verificationId.value = verificationIdParam
                        isLoading = false
                        otpSent = true
                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } else {
            Toast.makeText(context, "Activity context is required", Toast.LENGTH_SHORT).show()
        }
    }

    fun createUserDoc(){
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userDoc = Firebase.firestore.collection("users").document(user.uid)
            userDoc.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    // Create user document
                    val newUser = mapOf(
                        "uid" to user.uid,
                        "phoneNumber" to user.phoneNumber,
                        "name" to "Username", // optional placeholder
                        "profileImageUrl" to "" // optional placeholder
                    )
                    userDoc.set(newUser)
                }
            }
        }

    }
    fun verifyCode() {
        val credential = PhoneAuthProvider.getCredential(verificationId.value!!, otp.value)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    CoroutineScope(Dispatchers.IO).launch {
                        userDao.insertUser(User(phone = "+254${phoneNumber.value}", isLoggedIn = true))
                        createUserDoc()
                    }
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
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 80.dp, start = 5.dp, end = 5.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ChatX",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Welcome!",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = instructionText,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp
        )


        if (verificationId.value != null) {
            OutlinedTextField(
                value = otp.value,
                onValueChange = { if (it.length <= 6) otp.value = it },
                label = { Text("Enter OTP") },
                textStyle = TextStyle(fontSize = 24.sp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Button(
                onClick = {
                    verifyCode()
                    isLoading = true
                          },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(25.dp)
                                .padding(end = 8.dp)
                                .align(Alignment.CenterVertically),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Text(
                            "Please wait...",
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                } else {
                    Text("Verify", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

            }
        }else {
            OutlinedTextField(
                modifier = Modifier.padding(20.dp),
                value = phoneNumber.value,
                label = { Text("Phone Number") },
                textStyle = TextStyle(fontSize = 24.sp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
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
                onClick = {
                    if(!isLoading){
                        isLoading = true
                        sendVerificationCode()

                    }
                },
                enabled = !isLoading,

                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if(isLoading){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(25.dp)
                                .padding(end = 8.dp)
                                .align(Alignment.CenterVertically),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                        Text(
                            "Sending...",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                    }

                } else{
                    Text("Send OTP", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
