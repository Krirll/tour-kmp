package ru.krirll.moscowtour.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PersonData(
    val lastName: String,
    val firstName: String,
    val middleName: String,
    val passportSeries: Int,
    val passportNumber: Int,
    val phone: String
)

open class PersonValidationException(override val message: String?) : IllegalStateException()

//todo сделать по аналогии
/*class EmptyLoginException : LoginException("Логин не может быть пустым!")
class EmptyPasswordException : LoginException("Пароль не может быть пустым!")
class IncorrectLoginException : LoginException("Логин должен содержать от 3 до 20 символов, состоять из английских букв и цифр, может включать точки, дефисы или подчёркивания, но не начинаться и не заканчиваться ими!")
class IncorrectPasswordException : LoginException("Пароль должен содержать от 8 до 30 символов, включать хотя бы одну заглавную букву, одну строчную букву и одну цифру. Допустимы символы @$!%*?&.!")
class PasswordsNotEqualsException : LoginException("Пароли не совпадают!")*/
