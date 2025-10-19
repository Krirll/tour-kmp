package ru.krirll.ui.nav

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer

abstract class BaseRootComponent<T : Route>(
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext, BackHandlerOwner {

    abstract val initStack: List<T>
    protected abstract val serializer: KSerializer<T>

    private val scope = coroutineScope()

    protected val nav = StackNavigation<T>()

    protected val _childStack by lazy {
        childStack(
            source = nav,
            serializer = serializer,
            initialStack = { initStack },
            childFactory = ::newChild
        )
    }

    private val backCallback = BackCallback { onBack() }
    private val _onFinish = MutableStateFlow<Unit?>(null)
    val onFinish = _onFinish.filterNotNull()

    fun navReplace(vararg route: T) {
        nav.replaceAll(*route)
    }

    fun nav(route: T, bringToFront: Boolean = false) {
        if (bringToFront) {
            nav.bringToFront(route)
        } else {
            nav.pushNew(route)
        }
    }

    fun onBack() {
        nav.pop { popped ->
            if (!popped) finish()
        }
    }

    fun finish() {
        scope.launch { _onFinish.emit(Unit) }
    }

    val childStack: Value<ChildStack<*, Child>> by lazy { _childStack }

    init {
        backHandler.register(backCallback)
    }

    protected abstract fun newChild(route: T, ctx: ComponentContext): Child
}
