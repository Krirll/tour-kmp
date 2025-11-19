package ru.krirll.moscowtour.shared.data

import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.domain.model.EmptyPersonDataException
import ru.krirll.moscowtour.shared.domain.model.IncorrectPersonNameException
import ru.krirll.moscowtour.shared.domain.model.PassportNumberException
import ru.krirll.moscowtour.shared.domain.model.PassportSeriesException
import ru.krirll.moscowtour.shared.domain.model.PhoneNumberException

@Factory
class PersonDataValidator {

    fun validate(
        lastName: String,
        firstName: String,
        middleName: String,
        series: String,
        number: String,
        phone: String
    ) {
        if (lastName.isEmpty() || firstName.isEmpty() || middleName.isEmpty() ||
            series.isEmpty() || number.isEmpty() || phone.isEmpty()
        ) {
            throw EmptyPersonDataException()
        }

        val nameRegex = "^[А-ЯЁ][а-яё]{1,29}$".toRegex()
        if (!lastName.matches(nameRegex) || !firstName.matches(nameRegex) || !middleName.matches(nameRegex)) {
            throw IncorrectPersonNameException()
        }

        if (!series.matches("^\\d{4}$".toRegex())) {
            throw PassportSeriesException()
        }

        if (!number.matches("^\\d{6}$".toRegex())) {
            throw PassportNumberException()
        }

        if (!phone.matches("^\\+7\\d{10}$".toRegex())) {
            throw PhoneNumberException()
        }
    }
}
