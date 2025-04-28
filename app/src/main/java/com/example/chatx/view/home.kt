package com.example.chatx.view

import android.R.attr.onClick
import android.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Home() {
    Row(

    ){
       Text("ChatX")
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "More Options",
            modifier = Modifier
                .size(48.dp)

        )
    }
}