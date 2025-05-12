package com.example.chatx.view

import com.example.chatx.R
import android.media.MediaPlayer
import android.widget.Toast
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.chatx.model.data.Message
import com.example.chatx.model.data.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(navController: NavHostController, userId: String?) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.message_sound) }

    if (userId == null) {
        LaunchedEffect(Unit) {
            navController.navigate("home")
        }
        Text("User ID missing!", color = Color.Red)
        return
    }
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid
    var textMsg by remember {
        mutableStateOf("")
    }

    val messagesState = remember { mutableStateListOf<Message>() }

    var recipientInfo by remember { mutableStateOf<UserInfo?>(null) }
    val chatId = if (currentUserId!! < userId) "$currentUserId-$userId" else "$userId-$currentUserId"

    LaunchedEffect(userId) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserInfo::class.java)
                recipientInfo = user
            }
    }

    LaunchedEffect(userId) {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, _ ->
                snapshots?.let {
                    messagesState.clear()
                    for (doc in it) {
                        val msg = doc.toObject(Message::class.java)
                        messagesState.add(msg)
                    }
                }
            }
    }

    fun sendMessage(text: String) {
        if (text.isNotBlank() && currentUserId != null && userId != null) {

            val chatRef = db.collection("chats").document(chatId)
            val messageRef = chatRef.collection("messages").document()

            val messageData = mapOf(
                "senderId" to currentUserId,
                "content" to text,
                "timestamp" to FieldValue.serverTimestamp()
            )

            val lastMessageData = mapOf(
                "text" to text,
                "senderId" to currentUserId,
                "timestamp" to FieldValue.serverTimestamp()
            )

            // Run batched write or chained calls
            chatRef.set(
                mapOf(
                    "participants" to listOf(currentUserId, userId),
                    "lastMessage" to lastMessageData
                ),
                SetOptions.merge()
            ).addOnSuccessListener {
                messageRef.set(messageData)
                //play a sound when a message is sent
                mediaPlayer.start()
            }
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(top = 30.dp, bottom = 10.dp)
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(MaterialTheme.colorScheme.primary),
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
                val imageUrl = if (recipientInfo?.profileImageUrl?.isEmpty() != false) {
                    "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.freepik.com%2Ffree-photos-vectors%2Fadd-user&psig=AOvVaw2Zbu-vWiXhZwO710rKJ47N&ust=1746556867564000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCNiBpsX9jI0DFQAAAAAdAAAAABAe" // or use local drawable below
                } else {
                    recipientInfo!!.profileImageUrl
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
                        .size(60.dp)
                        .clip(CircleShape)
                )
                Column(
                    modifier = Modifier
                        .padding(start = 5.dp)
                ) {
                    recipientInfo?.let {
                        Text(
                            text = it.phoneNumber,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Online",
                            color = Color.White
                        )
                    }

                }
            }
            MinimalDropdownMenu()

        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(bottom = 70.dp),
                reverseLayout = true
            ) {
                items(messagesState.reversed()) { message ->
                    val isMe = message.senderId == currentUserId
                    val backgroundColor = if (isMe) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary
                    val alignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = alignment
                    ) {
                        Column(
                            modifier = Modifier
                                .background(backgroundColor, RoundedCornerShape(16.dp))
                                .padding(10.dp)
                                .widthIn(max = 250.dp)
                        ) {
                            Text(
                                text = message.content,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp
                            )
                            val formatted = message.timestamp?.toDate()?.let {
                                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it)
                            } ?: ""

                            Text(
                                    text = formatted,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Light
                                )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(end = 5.dp)
                    .fillMaxWidth()
                    .background(Color.White),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value =textMsg,
                    onValueChange = {textMsg = it},
                    maxLines = 3,
                    textStyle = TextStyle(fontSize = 18.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.Black,
                        cursorColor = Color.Gray,
                        focusedLabelColor = Color.Gray
                    ),
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (textMsg.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray,
                            shape = CircleShape
                        )
                        .then(
                            if (textMsg.isNotBlank()) {
                                Modifier.clickable {
                                    sendMessage(textMsg)
                                    textMsg = ""
                                }
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Send Icon",
                        tint = Color.White,
                        modifier = Modifier
                            .size(35.dp)
                            .padding(3.dp)
                    )
                }


            }
        }
    }
}
@Composable
fun MinimalDropdownMenu() {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = Color.White
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Block") },
                onClick = {
                    Toast.makeText(context, "Nicholas blocked", Toast.LENGTH_LONG).show()
                }
            )
            DropdownMenuItem(
                text = { Text("Report") },
                onClick = {
                    Toast.makeText(context, "Nicholas Reported", Toast.LENGTH_LONG).show()
                }
            )
            DropdownMenuItem(
                text = {Text("Mute")},
                onClick = {
                    Toast.makeText(context, "Nicholas Muted", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}