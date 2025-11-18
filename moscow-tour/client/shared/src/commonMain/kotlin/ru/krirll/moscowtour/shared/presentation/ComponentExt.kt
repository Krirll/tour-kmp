package ru.krirll.moscowtour.shared.presentation

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.server_not_responding
import moscowtour.moscow_tour.client.shared.generated.resources.unknown_error
import org.jetbrains.compose.resources.getString
import ru.krirll.domain.Log
import ru.krirll.http.domain.HttpException
import ru.krirll.moscowtour.shared.di.koin
import ru.krirll.moscowtour.shared.domain.model.LoginException
import ru.krirll.moscowtour.shared.domain.model.PersonValidationException

data class ListSnapshot<T>(
    val items: MutableStateFlow<List<T>?> = MutableStateFlow(null),
    val errorCode: MutableSharedFlow<String?> = MutableSharedFlow()
) : InstanceKeeper.Instance

fun LifecycleOwner.createErrorHandler(
    scope: CoroutineScope,
    errorCallback: suspend (String) -> Unit
): CoroutineExceptionHandler {
    return CoroutineExceptionHandler { _, throwable ->
        scope.launch {
            koin.get<Log>().e("Error", throwable)
            when (throwable) {
                is HttpException -> {
                    errorCallback(throwable.message ?: throwable.httpCode.toString())
                }

                is PersonValidationException,
                is LoginException -> {
                    errorCallback(throwable.message ?: getString(Res.string.unknown_error))
                }

                else -> {
                    val code = when (throwable) {
                        is IOException -> Res.string.server_not_responding
                        else -> Res.string.unknown_error
                    }
                    errorCallback(getString(code))
                }
            }
        }
    }
}
