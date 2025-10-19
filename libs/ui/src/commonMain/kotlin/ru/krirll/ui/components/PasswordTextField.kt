package ru.krirll.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import moscowtour.libs.ui.generated.resources.Res
import moscowtour.libs.ui.generated.resources.visibility
import moscowtour.libs.ui.generated.resources.visibility_off
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelRes: StringResource,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onDone: (() -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = if (onDone == null) ImeAction.Next else ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(onDone = { onDone?.invoke() }),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        label = { Text(stringResource(labelRes)) },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        trailingIcon = {
            val image = if (passwordVisible) {
                Res.drawable.visibility
            } else {
                Res.drawable.visibility_off
            }
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = vectorResource(image), contentDescription = null)
            }
        }
    )
}

