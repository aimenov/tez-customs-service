package kz.ne.railways.tezcustoms.service.model;

import lombok.Data;

@Data
public class InvoiceRow {
    private String name;
    private String code;
    private String unit;
    private String quantity;
    private String netto;
    private String brutto;
    private String price;
    private String currencyCode;
    private String totalPrice;
    private String description;

    public InvoiceRow() {}
}
