package ru.krirll.moscowtour.backend.di

import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import ru.krirll.backend.data.TokenVerifier
import ru.krirll.backend.domain.JwtInfo
import ru.krirll.backend.domain.LoginVerifier
import ru.krirll.moscowtour.backend.data.BackendRemoteEventHandler
import ru.krirll.moscowtour.backend.data.auth.AuthTokenRepositoryFactory
import ru.krirll.moscowtour.backend.di.factory.SavedToursRepositoryFactory
import ru.krirll.moscowtour.backend.di.factory.SearchRepositoryFactory
import ru.krirll.moscowtour.backend.di.factory.TicketsRepositoryFactory
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.EventType
import ru.krirll.moscowtour.shared.domain.ToursApi

@Single
class RoutingEntryPoint(
    val toursApi: ToursApi,
    val searchFactory: SearchRepositoryFactory,
    val savedToursFactory: SavedToursRepositoryFactory,
    val ticketsFactory: TicketsRepositoryFactory,
    val dispatcherProvider: DispatcherProvider
)

@Single
class WebSocketEntryPoint(
    @Named(EventType.SEARCH) val searchEventHandler: BackendRemoteEventHandler,
    @Named(EventType.SAVED) val savedEventHandler: BackendRemoteEventHandler,
    @Named(EventType.TICKETS) val ticketsEventHandler: BackendRemoteEventHandler,
    val tokenVerifier: TokenVerifier
)

@Single
class AuthEntryPoint(
    val loginVerifier: LoginVerifier,
    val authTokenRepositoryFactory: AuthTokenRepositoryFactory,
    val jwtInfo: JwtInfo,
    val tokenVerifier: TokenVerifier
)
