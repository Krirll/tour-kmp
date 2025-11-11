package ru.krirll.moscowtour.shared.presentation.overview.episode

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.EventType
import ru.krirll.moscowtour.shared.domain.RemoteEvent
import ru.krirll.moscowtour.shared.domain.RemoteEventListener
import ru.krirll.moscowtour.shared.domain.interactor.AboutVideoInteractor
import ru.krirll.moscowtour.shared.presentation.ItemSnapshot
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.componentScope
import ru.krirll.moscowtour.shared.presentation.createErrorHandler
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class EpisodeOverviewComponent(
    private val interactor: AboutVideoInteractor,
    val videoLauncher: VideoLauncher,
    private val dispatcherProvider: DispatcherProvider,
    private val context: ComponentContext,
    private val id: Long,
    private val seasonId: Long,
    val doBack: () -> Unit,
    private val remoteEventListener: RemoteEventListener,
    private val snapshot: ItemSnapshot<VideoLinksResponse> = context.instanceKeeper.getOrCreate { ItemSnapshot() },
) : ComponentContext by context {
    val recentlyWatchedEvent = remoteEventListener.event
        .filter { it is RemoteEvent.OnTicket && it.movieId == id }
    private val exceptionHandler = createErrorHandler {
        snapshot.errorCode.emit(it)
    }
    val links = snapshot.items.asStateFlow()
    val errorCode = snapshot.errorCode.asSharedFlow()

    fun tryToLoadLinks() {
        componentScope.launch(dispatcherProvider.main + exceptionHandler) {
            snapshot.errorCode.emit(null)
            val currentLinks = interactor.fetchVideoLinks(id, seasonId)
            snapshot.items.emit(currentLinks)
        }
    }

    fun toggleWatched(episode: Int) {
        val item = snapshot.items.value ?: return
        componentScope.launch(dispatcherProvider.main + exceptionHandler) {
            val rw = RecentlyWatchedInfo(0, id, episode.toLong(), seasonId)
            snapshot.items.value = interactor.toggleRecentlyWatched(item, rw)
        }
    }
}

@Factory(binds = [EpisodeOverviewFactory::class])
class EpisodeOverviewFactory(
    private val interactor: AboutVideoInteractor,
    @Named(EventType.SAVED) private val remoteEventListener: RemoteEventListener,
    private val dispatcherProvider: DispatcherProvider,
    private val videoLauncher: VideoLauncher
) : ComponentFactory<Child.EpisodeOverviewChild, Route.Overview.Episode> {
    override fun create(
        route: Route.Overview.Episode,
        child: ComponentContext,
        root: RootComponent
    ): Child.EpisodeOverviewChild {
        val comp = EpisodeOverviewComponent(
            interactor,
            videoLauncher,
            dispatcherProvider,
            child,
            route.id,
            route.selectedSeasonId,
            doBack = { root.onBack() },
            remoteEventListener
        )
        return Child.EpisodeOverviewChild(comp)
    }
}

