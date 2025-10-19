package ru.krirll.moscowtour.shared.presentation

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
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
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.di.koin

val LifecycleOwner.componentScope: CoroutineScope
    get() {
        val main = koin.get<DispatcherProvider>().main
        val scope = CoroutineScope(main)
        lifecycle.doOnDestroy(scope::cancel)
        return scope
    }

data class ListSnapshot<T>(
    val items: MutableStateFlow<List<T>?> = MutableStateFlow(null),
    val errorCode: MutableSharedFlow<String?> = MutableSharedFlow()
) : InstanceKeeper.Instance

data class ItemSnapshot<T>(
    val items: MutableStateFlow<T?> = MutableStateFlow(null),
    val errorCode: MutableSharedFlow<String?> = MutableSharedFlow()
) : InstanceKeeper.Instance

fun LifecycleOwner.createErrorHandler(
    errorCallback: suspend (String) -> Unit
): CoroutineExceptionHandler {
    return CoroutineExceptionHandler { _, throwable ->
        componentScope.launch {
            koin.get<Log>().e("Error", throwable)
            when (throwable) {
                is HttpException -> {
                    errorCallback(throwable.message ?: throwable.httpCode.toString())
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
