package kz.ne.railways.tezcustoms.service.util;

import kz.ne.railways.tezcustoms.service.model.InvoiceData;
import kz.ne.railways.tezcustoms.service.model.InvoiceRow;
import kz.ne.railways.tezcustoms.service.repository.TnVedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ExcelReader {

    private final TnVedRepository tnVedRepository;
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public InvoiceData getInvoiceFromFile(InputStream file) {
        InvoiceData invoiceData = new InvoiceData();
        try (Workbook workbook = new XSSFWorkbook(file)) {
            DataFormatter formatter = new DataFormatter();
            Sheet sheet = workbook.getSheetAt(0);
            invoiceData.setInvoiceNumber(sheet.getRow(1).getCell(2).getStringCellValue());
            invoiceData.setInvoiceDate(sheet.getRow(2).getCell(2).getDateCellValue());
            invoiceData.setShipper(sheet.getRow(3).getCell(2).getStringCellValue());
            invoiceData.setConsignee(sheet.getRow(4).getCell(2).getStringCellValue());
            invoiceData.setTotalPackageNumber((int) sheet.getRow(5).getCell(2).getNumericCellValue());

            int totalGoodsNumber = 0;
            for (Row row : sheet) {
                if (row.getRowNum() < 8)
                    continue;
                if (row.getCell(0).getStringCellValue().equals("Итого/Total:")) {
                    invoiceData.setTotal(formatter.formatCellValue(row.getCell(9)));
                    break;
                }

                InvoiceRow invoiceRow = new InvoiceRow();
                invoiceRow.setCode(formatter.formatCellValue(row.getCell(1)));
                invoiceRow.setName(formatter.formatCellValue(row.getCell(2)));
                invoiceRow.setUnit(formatter.formatCellValue(row.getCell(3)));
                invoiceRow.setQuantity(formatter.formatCellValue(row.getCell(4)));
                invoiceRow.setNetto(formatter.formatCellValue(row.getCell(5)));
                invoiceRow.setBrutto(formatter.formatCellValue(row.getCell(6)));
                invoiceRow.setPrice(formatter.formatCellValue(row.getCell(7)));
                invoiceRow.setCurrencyCode(formatter.formatCellValue(row.getCell(8)));
                invoiceRow.setTotalPrice(formatter.formatCellValue(row.getCell(9)));
                invoiceRow.setDescription(getDescriptionTnVed(invoiceRow.getCode()));
                invoiceData.addInvoiceItems(invoiceRow);
                totalGoodsNumber++;
            }
            invoiceData.setTotalGoodsNumber(totalGoodsNumber);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return invoiceData;
    }

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    private String getDescriptionTnVed(String code) {
        return kz.ne.railways.tezcustoms.service.util.StringUtils.merge(
                tnVedRepository.findTextListByCode(code), System.lineSeparator());
    }

}
