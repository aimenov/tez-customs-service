package kz.ne.railways.tezcustoms.service.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class InvoiceData {
    private String invoiceNumber;
    private Date invoiceDate;
    private String shipper;
    private String consignee;
    private Integer totalGoodsNumber;
    private Integer totalPackageNumber;
    private String total;
    private List<InvoiceRow> invoiceItems = new ArrayList<>();

    public void addInvoiceItems(InvoiceRow row) {
        invoiceItems.add(row);
    }
}
