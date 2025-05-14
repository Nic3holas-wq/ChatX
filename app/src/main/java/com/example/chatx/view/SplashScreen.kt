package com.example.chatx.view

import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.view.animation.OvershootInterpolator
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import com.example.chatx.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.chatx.viewmodel.SettingsViewModel
import com.example.chatx.viewmodel.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = viewModel(),
    onAuthSuccess: () -> Unit,
    onAuthFail: () -> Unit,
    activity: FragmentActivity,
){
    val settingsViewModel: SettingsViewModel = viewModel()
    val isFingerprint by settingsViewModel.isFingerprintEnabled.collectAsState()
    val context = LocalContext.current
    val scale = remember {
        Animatable(0f)
    }
    val textScale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(40f) }

    fun authenticateWithBiometrics(context: android.content.Context, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback(){
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onFailure()
                }

                override fun onAuthenticationFailed() {
                    onFailure()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Fingerprint Authentication")
            .setSubtitle("Log in using your fingerprint")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.3f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        launch{textScale.animateTo(1f, tween(800))}
        launch{alpha.animateTo(1f, tween(800))}
        launch{offsetY.animateTo(0f, tween(800))}
        delay(3000L)
        val isLoggedIn = viewModel.isUserLoggedIn()
        if (isLoggedIn && isFingerprint){
                authenticateWithBiometrics(
                    context = context,
                    onSuccess = onAuthSuccess,
                    onFailure = onAuthFail
                )

        } else if (isLoggedIn) {
            onAuthSuccess()
        } else {
            navController.navigate("signin"){
                popUpTo("splash_screen") {inclusive = true}
            }
        }

        }




    Column  (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.chat),
            contentDescription = "Logo",
            modifier = Modifier
                .graphicsLayer(
                scaleX = scale.value,
                scaleY = scale.value
            )
        )
        /*Text(
            text = "ChatX",
            modifier = Modifier
                .scale(textScale.value)
                .alpha(alpha.value)
                .offset(y = offsetY.value.dp),
            color = Color(0xFF0096C7),
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold
        )*/
    }
}