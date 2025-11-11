package ru.krirll.moscowtour.backend.data.document

import kotlinx.coroutines.withContext
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.backend.domain.normalizeTimestamp
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.TicketFactory
import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.TicketFile
import ru.krirll.moscowtour.shared.domain.model.Tour
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Factory
class TicketFactoryImpl(
    private val dispatcherProvider: DispatcherProvider
) : TicketFactory {

    override suspend fun create(
        tour: Tour,
        personData: PersonData,
        requestTime: Long,
        buyTime: Long?
    ): TicketFile {
        return withContext(dispatcherProvider.io) {
            val templateFile = javaClass.getResourceAsStream("/template.docx")
                ?: error("Template not found")


            templateFile.use { fis ->
                val doc = XWPFDocument(fis)

                // Проходим по всем таблицам
                val tables = doc.tables

                // 1. Личные данные
                val personalTable = tables[0]
                val personalRow = personalTable.getRow(1)
                personalRow.getCell(0).setCleanText(personData.lastName)
                personalRow.getCell(1).setCleanText(personData.firstName)
                personalRow.getCell(2).setCleanText(personData.middleName)
                personalRow.getCell(3).setCleanText(personData.passportSeries.toString())
                personalRow.getCell(4).setCleanText(personData.passportNumber.toString())
                personalRow.getCell(5).setCleanText(personData.phone)

                // 2. Данные билета
                val locale = Locale.of("ru", "RU")
                val datesFormatter = SimpleDateFormat("dd.MM.yyyy", locale)
                val ticketTable = tables[1]
                val ticketRow = ticketTable.getRow(1)
                ticketRow.getCell(0).setCleanText(tour.title)
                ticketRow.getCell(1).setCleanText(tour.country)
                ticketRow.getCell(2).setCleanText(tour.city)
                ticketRow.getCell(3).setCleanText(
                    datesFormatter.format(
                        Date(tour.dateBegin.normalizeTimestamp())
                    )
                )
                ticketRow.getCell(4).setCleanText(
                    datesFormatter.format(
                        Date(tour.dateEnd.normalizeTimestamp())
                    )
                )
                ticketRow.getCell(5).setCleanText(tour.price.toString())

                // 3. Замена текста "Дата приобретения:" на "Дата приобретения: <значение>"
                val buyFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", locale)
                val targetParagraph = doc.paragraphs.find { it.text.contains(DATE_OF_BUY) }
                targetParagraph?.let { p ->
                    val newText =
                        p.text.replace(
                            DATE_OF_BUY,
                            "$DATE_OF_BUY ${
                                buyFormatter.format(
                                    Date(
                                        buyTime?.normalizeTimestamp() ?: requestTime.normalizeTimestamp()
                                    )
                                )
                            }"
                        )
                    while (p.runs.isNotEmpty()) p.removeRun(0)
                    p.createRun().setText(newText)
                }

                val formatter = SimpleDateFormat("dd.MM.yyyy_HH-mm-SSS", Locale.of("ru", "RU"))
                val fileName = "ticket-${
                    formatter.format(Date(requestTime.normalizeTimestamp()))
                }.docx"
                val bytes = ByteArrayOutputStream().use { outputStream ->
                    doc.write(outputStream)
                    doc.close()
                    outputStream.toByteArray()
                }
                TicketFile(fileName, bytes)
            }
        }
    }

    private fun XWPFTableCell.setCleanText(text: String) {
        val p = this.addParagraph()
        val run = p.createRun()
        run.setText(text)
    }

    companion object {
        const val DATE_OF_BUY = "Дата приобретения:"
    }
}
