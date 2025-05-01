package com.example.chatx.view
import androidx.compose.foundation.BorderStroke
import com.example.chatx.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Home(navController: NavHostController) {
    val messages = listOf("Hello", "How are you", "Welcome to ChatX!", "This is awesome", "Let us build more")
    val user = FirebaseAuth.getInstance().currentUser
    LaunchedEffect(true) {
        if (user == null) {
            navController.navigate("signin") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(top = 30.dp)
                .background(Color(0xFF0096C7)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = "ChatX",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(24.dp),
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "More Options",
                tint = Color.White,
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(messages){ message ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable(onClick = {navController.navigate("chat/123")}),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile2),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        Text(
                            text = "Nicholas Murimi",
                            color = Color.Black,
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp
                        )
                        Text(
                            text = message,
                            color = Color.Black,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        )

                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0096C7))
                        ) {
                            Text(
                                text = "2",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "5 min ago",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }


                }

            }
        }
    }


}
