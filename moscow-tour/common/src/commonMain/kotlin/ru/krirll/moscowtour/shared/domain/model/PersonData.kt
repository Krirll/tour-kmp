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

open class PersonDataValidationException(override val message: String?) : IllegalStateException()

class EmptyPersonDataException : PersonDataValidationException("Все поля обязательны и должны быть заполнены!")
class IncorrectPersonNameException : PersonDataValidationException("Имя, фамилия и отчество должны состоять из русского алфавита, начинаться с заглавной буквы, быть не меньше 2 и не более 30 символов!")
class PassportSeriesException : PersonDataValidationException("Серия паспорта должна состоять из 4 цифр!")
class PassportNumberException : PersonDataValidationException("Номер паспорта должна состоять из 6 цифр!")
class PhoneNumberException : PersonDataValidationException("Номер телефона должен начинаться с +7 и 10 цифр! ")
