package ru.krirll.moscowtour.backend.data.document

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.koin.core.annotation.Factory
import java.io.FileInputStream
import java.io.FileOutputStream

@Factory
class TicketCreator {
    suspend fun create() {
        FileInputStream("templatePath").use { fis ->
            val doc = XWPFDocument(fis)

            // Проходим по всем таблицам (у тебя их 2)
            val tables = doc.tables

            //todo передавать данные и заполнять + подкорректировать шаблон
//            // 1. Личные данные
//            val personalTable = tables[0]
//            val personalRow = personalTable.getRow(1)
//            personalRow.getCell(0).text = data.lastName
//            personalRow.getCell(1).text = data.firstName
//            personalRow.getCell(2).text = data.middleName
//            personalRow.getCell(3).text = data.passportSeries
//            personalRow.getCell(4).text = data.passportNumber
//            personalRow.getCell(5).text = data.phone
//
//            // 2. Данные билета
//            val ticketTable = tables[1]
//            val ticketRow = ticketTable.getRow(1)
//            ticketRow.getCell(0).text = data.tourName
//            ticketRow.getCell(1).text = data.country
//            ticketRow.getCell(2).text = data.city
//            ticketRow.getCell(3).text = data.dateBegin
//            ticketRow.getCell(4).text = data.dateEnd
//            ticketRow.getCell(5).text = data.price
//
//            // 3. Замена текста "Дата приобретения:" на "Дата приобретения: <значение>"
//            for (p in doc.paragraphs) {
//                if (p.text.contains("Дата приобретения:")) {
//                    for (run in p.runs) {
//                        val newText = p.text.replace("Дата приобретения:", "Дата приобретения: ${data.purchaseDate}")
//                        run.setText(newText, 0)
//                        break
//                    }
//                }
//            }

            FileOutputStream("outputPath").use { fos ->
                doc.write(fos)
            }

            doc.close()
        }
    }
}
