package com.example.chatx.view
import android.graphics.drawable.Icon
import android.widget.Space
import com.example.chatx.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun Chat(navController: NavHostController, userId: String?) {
    if (userId == null){
        Text("User ID missing!", color = Color.Red)
        navController.navigate("home")
    }
    val messages = listOf("Hello there kiongos!", "Uko aje buda!","Unafika lini","Unafika lini","Unafika lini","Unafika lini","Unafika lini","Unafika lini","Unafika lini","Unafika lini","Unafika lini","Unafika lini","Unafika lini","Unafika lini","Unafika lini","Unafika lini","Unafika lini","Unafika lini","Unafika lini","Unafika lini",)
    var textMsg by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color(0xFF0096C7)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(30.dp)
                        .clickable(onClick = {navController.popBackStack()}),
                    imageVector = Icons.Filled.ArrowBack,
                    tint = Color.White,
                    contentDescription = "Back arrow"
                )
                Image(
                    painter = painterResource(id = R.drawable.profile2),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
                Column(
                    modifier = Modifier
                        .padding(start = 5.dp)
                ) {
                    Text(
                        text = "Nicholas Murimi",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Online",
                        color = Color.Green
                    )
                }
            }
            Icon(
                imageVector = Icons.Filled.MoreVert,
                tint = Color.White,
                contentDescription = "More Options",
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(24.dp)
                    .clickable(onClick = {})
            )
        }

        LazyColumn(
            modifier = Modifier
                .padding(5.dp)
        ) {
            items(messages){message ->
                Box(
                    modifier = Modifier
                        .background(Color(0xFF0096C7), shape = RoundedCornerShape(16.dp))
                        .padding(10.dp)
                        .width(180.dp)

                ){
                    Column(

                    ) {
                        Text(
                            text = message,
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                tint = Color.Green,
                                contentDescription = "Read msg icon",
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Yesterday",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light
                            )
                            Text(
                                text = "7:10 PM",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value =textMsg,
                onValueChange = {textMsg = it},
                )
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = "Send Icons",
                tint = Color(0xFF0096C7),
                modifier = Modifier
                    .size(35.dp)
                    .rotate(-30f)
                    .clickable(onClick = {})
            )
        }

    }
}