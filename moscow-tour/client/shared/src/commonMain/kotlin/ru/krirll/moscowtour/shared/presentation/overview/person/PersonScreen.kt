package ru.krirll.moscowtour.shared.presentation.overview.person

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.`continue`
import moscowtour.moscow_tour.client.shared.generated.resources.error_title
import moscowtour.moscow_tour.client.shared.generated.resources.first_name
import moscowtour.moscow_tour.client.shared.generated.resources.last_name
import moscowtour.moscow_tour.client.shared.generated.resources.middle_name
import moscowtour.moscow_tour.client.shared.generated.resources.okay
import moscowtour.moscow_tour.client.shared.generated.resources.passport_number
import moscowtour.moscow_tour.client.shared.generated.resources.passport_series
import moscowtour.moscow_tour.client.shared.generated.resources.person_data
import moscowtour.moscow_tour.client.shared.generated.resources.phone_number
import moscowtour.moscow_tour.client.shared.generated.resources.unknown_error
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.moscowtour.shared.presentation.SimpleAppBar
import ru.krirll.moscowtour.shared.presentation.base.Loading
import ru.krirll.moscowtour.shared.presentation.base.ScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonScreen(component: PersonComponent) {
    val state by component.state.collectAsState()
    BaseScreen(
        appBar = {
            SimpleAppBar(
                stringResource(Res.string.person_data),
                doBack = { component.onBack() }
            )
        },
        content = {
            var lastName by rememberSaveable { mutableStateOf("") }
            var firstName by rememberSaveable { mutableStateOf("") }
            var middleName by rememberSaveable { mutableStateOf("") }
            var series by rememberSaveable { mutableStateOf("") }
            var number by rememberSaveable { mutableStateOf("") }
            var phone by rememberSaveable { mutableStateOf("") }
            Box(modifier = Modifier.padding(it)) {
                when (val s = state) {
                    is ScreenState.Loading -> Loading()
                    else -> {
                        if (s is ScreenState.Succeed) {
                            component.goNext()
                        } else if (s is ScreenState.Error) {
                            AlertDialog(
                                onDismissRequest = { component.resetState() },
                                title = { Text(stringResource(Res.string.error_title)) },
                                text = {
                                    Text(
                                        s.e.message ?: stringResource(Res.string.unknown_error)
                                    )
                                },
                                confirmButton = {
                                    Text(
                                        text = stringResource(Res.string.okay),
                                        modifier = Modifier.padding(8.dp)
                                            .clickable { component.resetState() }
                                    )
                                }
                            )
                        }
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                            item {
                                OutlinedTextField(
                                    lastName,
                                    onValueChange = { lastName = it },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    label = { Text(stringResource(Res.string.last_name)) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                OutlinedTextField(
                                    firstName,
                                    onValueChange = { firstName = it },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    label = { Text(stringResource(Res.string.first_name)) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                OutlinedTextField(
                                    middleName,
                                    onValueChange = { middleName = it },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    label = { Text(stringResource(Res.string.middle_name)) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                OutlinedTextField(
                                    series,
                                    onValueChange = { series = it },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    label = { Text(stringResource(Res.string.passport_series)) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                OutlinedTextField(
                                    number,
                                    onValueChange = { number = it },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    label = { Text(stringResource(Res.string.passport_number)) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                OutlinedTextField(
                                    phone,
                                    onValueChange = { phone = it },
                                    singleLine = true,
                                    keyboardActions = KeyboardActions(onDone = {
                                        component.validateData(
                                            lastName, firstName, middleName, series, number, phone
                                        )
                                    }),
                                    label = { Text(stringResource(Res.string.phone_number)) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                Button(
                                    onClick = {
                                        component.validateData(
                                            lastName, firstName, middleName, series, number, phone
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                ) {
                                    Text(stringResource(Res.string.`continue`))
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
