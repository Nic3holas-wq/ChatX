package com.example.chatx.view

import com.example.chatx.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.chatx.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun StartChatScreen(navController: NavHostController, viewModel: HomeViewModel = viewModel()) {
    val users = viewModel.allUsers.collectAsState()
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(top = 40.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable(onClick = {navController.popBackStack()}),
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back Button",
                tint = Color.White

            )
            Text(
                text = "Start New Chat",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }


        LazyColumn {
            items(users.value.filter { it.uid != currentUserUid }) { user ->
            Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate("chat/${user.uid}")
                        },
                    verticalAlignment = Alignment.CenterVertically,

                ) {
                val imageUrl = if (user.profileImageUrl.isEmpty()) {
                    "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.freepik.com%2Ffree-photos-vectors%2Fadd-user&psig=AOvVaw2Zbu-vWiXhZwO710rKJ47N&ust=1746556867564000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCNiBpsX9jI0DFQAAAAAdAAAAABAe" // or use local drawable below
                } else {
                    user.profileImageUrl
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
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(text = user.phoneNumber, color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Normal)
                        Text(text = user.name,color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp,fontWeight = FontWeight.Light)
                    }
                }
            }
        }
    }
}
