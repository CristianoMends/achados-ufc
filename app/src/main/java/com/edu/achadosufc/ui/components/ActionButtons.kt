package com.edu.achadosufc.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edu.achadosufc.data.model.Item

@Composable
fun ActionButtons(
    item: Item,
    isOwner: Boolean,
    context: Context,
    onNotifyClick: () -> Unit,
    onScheduleClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!isOwner) {
            Button(
                onClick = onNotifyClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (item.isFound) "EU PERDI ISSO!" else "EU ENCONTREI!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        OutlinedButton(
            onClick = { /* TODO: Chamar a lógica de agendamento de alarme */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Criar Lembrete")
        }
    }
}