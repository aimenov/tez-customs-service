package kz.ne.railways.tezcustoms.service.model;

public interface UserInvoices {
    String getInvoiceNum();

    String getReciveStationCode();

    String getDestStationCode();

    Integer getInvoiceStatus();

    String getUinpCode();

    String getInvoiceDate();

    Integer getInvoiceUn();

    String getDeclarantName();

    String getArriveStation();
}
