package kz.ne.railways.tezcustoms.service.service.transitdeclaration;

import kz.ne.railways.tezcustoms.service.model.transit_declaration.SaveDeclarationResponseType;
import ru.customs.information.customsdocuments.esadout_cu._5_11.ESADoutCUType;

public interface TransitDeclarationServiceLocal {
    public ESADoutCUType build(long invoiceId);

    public SaveDeclarationResponseType send(Long invoiceUn);

    public String getXml(ESADoutCUType value);
}
