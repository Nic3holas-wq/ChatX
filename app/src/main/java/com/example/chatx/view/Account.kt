package com.example.chatx.view

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.room.Room
import coil.compose.rememberAsyncImagePainter
import com.example.chatx.R
import com.example.chatx.model.data.AppDatabase
import com.example.chatx.model.data.User
import com.example.chatx.model.data.UserInfo
import com.example.chatx.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.contracts.contract


@Composable
fun Account(navController: NavHostController, viewModel: UserViewModel = viewModel()) {
    val user = viewModel.user.value
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val newName = remember { mutableStateOf(user?.name ?: "") }
    val storage = FirebaseStorage.getInstance()
    val userId = Firebase.auth.currentUser?.uid
    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()
    val imageUrl = remember {
        mutableStateOf<String?>(null)
    }
    LaunchedEffect(Unit) {
        val user = withContext(Dispatchers.IO){
            db.userDao().getUser(phone = user?.phoneNumber ?:"+254716453748")
        }
        imageUrl.value = user?.profileImageUrl
        newName.value = user?.name ?:"Username"
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            CoroutineScope(Dispatchers.IO).launch {
                userDao.insertUser(
                    User(phone = user?.phoneNumber ?: "+254716453748", profileImageUrl = it.toString())
                )
                withContext(Dispatchers.Main) {
                    imageUrl.value = it.toString()
                }
            }
        }

    }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 30.dp)
            .fillMaxSize()

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 30.dp,
                        bottomEnd = 30.dp
                    ))
                .height(150.dp),

        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .clickable(onClick = {navController.popBackStack()})
                            .padding(start = 10.dp),
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back arrow",
                        tint = Color.White
                    )
                    Text(
                        text = "Account",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clickable(
                                onClick = {
                                    launcher.launch("image/*")
                                }
                            )
                    ) {
                       imageUrl.value?.let { url ->
                           Image(
                               painter = rememberAsyncImagePainter(
                                   model = imageUrl.value, // ✅ correct
                                   error = painterResource(id = R.drawable.user_photo),
                                   placeholder = painterResource(id = R.drawable.user_photo)
                               ),
                               contentDescription = "Profile Picture",
                               contentScale = ContentScale.Crop,
                               modifier = Modifier
                                   .size(60.dp)
                                   .clip(CircleShape)
                           )
                       }
                        Image(
                            painter = painterResource(id = R.drawable.cam),
                            contentDescription = "Camera Icon",
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.TopEnd)
                                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                                .padding(4.dp)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = user?.name ?: "No Name",
                            color = Color.White,
                            fontSize = 22.sp
                        )
                        Text(
                            text = user?.phoneNumber ?: "No Phone",
                            color = Color.White,
                            fontSize = 18.sp,
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Details",
                        tint = Color.White,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(10.dp)
                            .clickable(
                                onClick = {
                                    showDialog.value = true
                                }
                            )
                    )
                }
                if (showDialog.value){
                    androidx.compose.material.AlertDialog(
                        onDismissRequest = {showDialog.value = false},
                        title = { Text(text = "Edit Username") },
                        text = {
                            androidx.compose.material.OutlinedTextField(
                                value = newName.value,
                                onValueChange = {newName.value = it},
                                label = {Text("Enter new username")}
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val uid = Firebase.auth.currentUser?.uid
                                    val db = Firebase.firestore
                                    if (uid != null) {
                                        db.collection("users")
                                            .document(uid)
                                            .set(mapOf("name" to newName.value), SetOptions.merge())
                                            .addOnSuccessListener{
                                                CoroutineScope(Dispatchers.IO).launch{
                                                    userDao.insertUser(User(phone = user?.phoneNumber ?: "", name = newName.value))

                                                }
                                                Toast.makeText(context, "Name updated", Toast.LENGTH_LONG).show()
                                                showDialog.value = false
                                            }
                                            .addOnFailureListener{
                                                Toast.makeText(context, "Update failed!", Toast.LENGTH_LONG).show()
                                            }
                                    }
                                }
                            ) {
                                Text("Save")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {showDialog.value = false}
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
            }

    }
}

