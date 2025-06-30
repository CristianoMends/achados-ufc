package com.edu.achadosufc.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun MessageDialog(
    title: String,
    message: String,
    confirmButtonText: String = "Ok",
    confirmButtonAction: (() -> Unit) = {},
    dismissButtonText: String = "",
    dismissButtonAction: (() -> Unit) = {}
) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = confirmButtonAction) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = dismissButtonAction) {
                Text(dismissButtonText)
            }
        },
        title = { Text(title) },
        text = { Text(message) }
    )
}
