package ru.krirll.http.domain

interface UserAgentProvider {
    fun provide(): String?
}
