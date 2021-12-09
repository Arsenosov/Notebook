package com.arsenosov.notebook.compose.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arsenosov.notebook.compose.MyAppBar
import com.arsenosov.notebook.util.Screen
import com.arsenosov.notebook.viewmodels.MainViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@ExperimentalMaterialApi
@Composable
fun NewGroupScreen(viewModel: MainViewModel, openDrawer: () -> Unit) {
    Column {
        MyAppBar(title = Screen.NewGroup.title) {
            openDrawer()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
        ) {
            val nameQuery = viewModel.nameQueryGroupLive.value
            val nameError = viewModel.nameErrorGroupLive.value
            val selectedColor = viewModel.colorSelectedGroupLive.value
            val focusManager = LocalFocusManager.current
            val groupDialogState = rememberMaterialDialogState()
            OutlinedTextField(
                label = {
                    Text(
                        text = if (nameError.isNotEmpty()) nameError else "Название"
                    )
                },
                value = nameQuery,
                onValueChange = {newValue ->
                    viewModel.changeQueryNameGroup(newValue)
                },
                isError = nameError.isNotEmpty(),
                keyboardActions = KeyboardActions (
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults
                    .outlinedTextFieldColors(
                        errorBorderColor = Color.Red,
                        errorCursorColor = Color.Red,
                        errorLabelColor = Color.Red,
                    ),
            )
            Spacer(
                modifier = Modifier
                    .height(15.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Цвет"
                )
                Spacer(
                    modifier = Modifier
                        .width(10.dp),
                )
                Card(
                    modifier = Modifier
                        .clip(CircleShape)
                        .border(1.dp, Color.Black, CircleShape)
                        .size(30.dp),
                    backgroundColor = selectedColor,
                    onClick = {
                        groupDialogState.show()
                    }
                ) {
                    MaterialDialog(
                        dialogState = groupDialogState,
                        buttons = {
                            positiveButton("Ок")
                            negativeButton("Отмена")
                        },
                    ) {
                        colorChooser(
                            colors = ColorPalette.Primary,
                            onColorSelected = {
                                viewModel.changeSelectedColorGroup(it)
                            }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White, contentColor = Color.Black),
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.checkNewGroupSubmit()
                    }) {
                    Text(
                        text = "Добавить",
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}