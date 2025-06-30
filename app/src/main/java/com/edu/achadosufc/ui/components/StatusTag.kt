package com.edu.achadosufc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatusTag(isFound: Boolean) {
    Text(
        text = if (isFound) "Encontrado" else "Perdido",
        color = Color.White,
        modifier = Modifier
            .background(
                color = if (isFound) Color(0xFF2ECC71) else Color(0xFFE74C3C),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelMedium
    )
}
