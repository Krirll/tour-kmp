package ru.krirll.http.domain

import io.ktor.http.HttpStatusCode

open class HttpException(val httpCode: Int, override val message: String?) : RuntimeException()

class BadRequestException(msg: String) : HttpException(HttpStatusCode.BadRequest.value, msg)
open class NotFoundException(msg: String) : HttpException(HttpStatusCode.NotFound.value, msg)
