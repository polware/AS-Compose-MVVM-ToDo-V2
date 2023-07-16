package com.polware.todov2compose.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun DeleteAlertDialog(
    title: String,
    message: String,
    openDialog: Boolean,
    closeDialog: () -> Unit,
    onYesClick: () -> Unit
) {
    if (openDialog) {
        AlertDialog(
            title = {
                    Text(
                        text = title,
                        fontSize = MaterialTheme.typography.h5.fontSize,
                        fontWeight = FontWeight.Bold
                    )
            },
            text = {
                   Text(
                       text = message,
                       fontSize = MaterialTheme.typography.subtitle1.fontSize,
                       fontWeight = FontWeight.Normal
                   )
            },
            confirmButton = {
                Button(onClick = {
                    onYesClick()
                    closeDialog()
                    }
                ) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    closeDialog()
                    }
                ) {
                    Text(text = "No")
                }
            },
            onDismissRequest = {
                closeDialog()
                }
        )
    }
}