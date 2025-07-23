package com.edu.achadosufc.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edu.achadosufc.data.model.Item
import com.edu.achadosufc.data.model.UserResponse

@Composable
fun ActionButtons(
    item: Item,
    isOwner: Boolean,
    context: Context,
    onSendMessage: () -> Unit,
    onScheduleClick: () -> Unit,
    messagesReceivedBy : List<UserResponse> = emptyList()
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!isOwner) {
            Button(
                onClick = onSendMessage,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Enviar Mensagem",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (messagesReceivedBy.isNotEmpty()) {
                Text(
                    text = "Usuários que enviaram mensagem:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                messagesReceivedBy.forEach { user ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Ícone de usuário",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(text = user.name, style = MaterialTheme.typography.bodyMedium)
                    }
                }
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