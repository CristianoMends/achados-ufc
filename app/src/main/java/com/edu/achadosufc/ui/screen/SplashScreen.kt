package com.edu.achadosufc.ui.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.edu.achadosufc.R

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ufc_background),
            contentDescription = "Ufc Background Image",
            modifier = Modifier.fillMaxSize(),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                Color.Black.copy(alpha = 0.6f),
                BlendMode.Darken
            ),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.brasao_vertical_cor),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 32.dp),
                color = Color.White
            )
            Text(
                text = "Carregando...",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}