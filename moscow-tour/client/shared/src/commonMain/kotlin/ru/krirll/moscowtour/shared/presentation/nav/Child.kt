package ru.krirll.moscowtour.shared.presentation.nav

import ru.krirll.moscowtour.shared.presentation.list.VideoScreenComponent
import ru.krirll.moscowtour.shared.presentation.loading.LoadingComponent
import ru.krirll.moscowtour.shared.presentation.overview.OverviewComponent
import ru.krirll.moscowtour.shared.presentation.overview.episode.EpisodeOverviewComponent
import ru.krirll.moscowtour.shared.presentation.overview.season.SeasonOverviewComponent
import ru.krirll.moscowtour.shared.presentation.saved.SavedMovieScreenComponent
import ru.krirll.moscowtour.shared.presentation.search.SearchScreenComponent
import ru.krirll.moscowtour.shared.presentation.settings.SettingsComponent
import ru.krirll.moscowtour.shared.presentation.settings.auth.AuthComponent
import ru.krirll.moscowtour.shared.presentation.settings.pass.EditPasswordComponent
import ru.krirll.moscowtour.shared.presentation.settings.register.RegisterComponent
import ru.krirll.moscowtour.shared.presentation.settings.serv.EditServComponent

sealed class Child : ru.krirll.ui.nav.Child {
    class VideosChild(val component: VideoScreenComponent) : Child()
    class SearchChild(val component: SearchScreenComponent) : Child()
    class OverviewChild(val component: OverviewComponent) : Child()
    class SeasonOverviewChild(val component: SeasonOverviewComponent) : Child()
    class EpisodeOverviewChild(val component: EpisodeOverviewComponent) : Child()
    class SettingsChild(val component: SettingsComponent) : Child()
    class EditServerAddrChild(val component: EditServComponent) : Child()
    class AuthChild(val component: AuthComponent) : Child()
    class RegisterChild(val component: RegisterComponent) : Child()
    class SavedMovieChild(val component: SavedMovieScreenComponent) : Child()
    class EditPasswordChild(val component: EditPasswordComponent) : Child()
    class LoadingChild(val component: LoadingComponent) : Child()
}
