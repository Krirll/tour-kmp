package ru.krirll.moscowtour.backend.data.document

import kotlinx.coroutines.withContext
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.TicketBuilder
import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.Tour
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Date

@Factory
class TicketBuilderImpl(
    private val dispatcherProvider: DispatcherProvider
) : TicketBuilder {

    override suspend fun build(tour: Tour, personData: PersonData): String {
        //todo надо решить как доставать шаблон и куда сохранять файл
        withContext(dispatcherProvider.io) {
            FileInputStream("templatePath").use { fis ->
                val doc = XWPFDocument(fis)

                // Проходим по всем таблицам (у тебя их 2)
                val tables = doc.tables

                // 1. Личные данные
                val personalTable = tables[0]
                val personalRow = personalTable.getRow(1)
                personalRow.getCell(0).text = personData.lastName
                personalRow.getCell(1).text = personData.firstName
                personalRow.getCell(2).text = personData.middleName
                personalRow.getCell(3).text = personData.passportSeries.toString()
                personalRow.getCell(4).text = personData.passportNumber.toString()
                personalRow.getCell(5).text = personData.phone

                // 2. Данные билета
                val ticketTable = tables[1]
                val ticketRow = ticketTable.getRow(1)
                ticketRow.getCell(0).text = tour.title
                ticketRow.getCell(1).text = tour.country
                ticketRow.getCell(2).text = tour.city
                ticketRow.getCell(3).text = Date(tour.dateEnd).toString()
                ticketRow.getCell(4).text = Date(tour.dateEnd).toString()
                ticketRow.getCell(5).text = tour.price.toString()

                // 3. Замена текста "Дата приобретения:" на "Дата приобретения: <значение>"
                for (p in doc.paragraphs) {
                    if (p.text.contains("Дата приобретения:")) {
                        for (run in p.runs) {
                            val newText = p.text.replace(
                                "Дата приобретения:",
                                "Дата приобретения: ${Date(System.currentTimeMillis())}"
                            )
                            run.setText(newText, 0)
                            break
                        }
                    }
                }

                FileOutputStream("outputPath").use { fos ->
                    doc.write(fos)
                }

                doc.close()
            }
        }
    }
}
