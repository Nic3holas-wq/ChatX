package com.example.chatx.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.chatx.R
import com.example.chatx.viewmodel.HomeViewModel
import com.example.chatx.viewmodel.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavHostController, viewModel: HomeViewModel = viewModel()) {
    val chatItems = viewModel.chatList
    val user = FirebaseAuth.getInstance().currentUser
    val settingsViewModel: SettingsViewModel = viewModel()

    LaunchedEffect(true) {
        if (user == null) {
            navController.navigate("signin") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 30.dp)
                    .background(MaterialTheme.colorScheme.primary),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = "ChatX",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                HomeDropdownMenu(navController)
            }

            // Chat List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(chatItems) { chatItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                navController.navigate("chat/${chatItem.user.uid}")
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val imageUrl = if (chatItem.user.profileImageUrl.isEmpty()) {
                            "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.freepik.com%2Ffree-photos-vectors%2Fadd-user&psig=AOvVaw2Zbu-vWiXhZwO710rKJ47N&ust=1746556867564000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCNiBpsX9jI0DFQAAAAAdAAAAABAe" // or use local drawable below
                        } else {
                            chatItem.user.profileImageUrl
                        }
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = imageUrl,
                                error = painterResource(id = R.drawable.user_photo), // local fallback if load fails
                                placeholder = painterResource(id = R.drawable.user_photo)
                            ),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
//                            val displayName = if (chatItem.user.name.isEmpty()){
//                                chatItem.user.phoneNumber
//                            }else{
//                                chatItem.user.name
//                            }
                            Text(
                                text = chatItem.user.name,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = chatItem.lastMessage.text,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp
                            )
                        }

                        val formattedTime = remember(chatItem.lastMessage.timestamp) {
                            val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                            val date = chatItem.lastMessage.timestamp.toDate() // Convert Firebase Timestamp to java.util.Date
                            sdf.format(date)
                        }


                        Text(
                            text = formattedTime,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                navController.navigate("start_chat")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Composable
fun HomeDropdownMenu(navController: NavHostController) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.padding(16.dp)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = Color.White
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Account") },
                onClick = {
                    navController.navigate("account_info")
                }
            )
            DropdownMenuItem(
                text = { Text("Create Group") },
                onClick = {
                    navController.navigate("create_group")
                }
            )
            DropdownMenuItem(
                text = { Text("Settings") },
                onClick = {
                    navController.navigate("settings")
                }
            )
            DropdownMenuItem(
                text = { Text("Theme") },
                onClick = {
                    navController.navigate("theme")
                }
            )
        }
    }
}
