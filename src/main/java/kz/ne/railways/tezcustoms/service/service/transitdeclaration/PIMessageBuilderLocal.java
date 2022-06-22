package kz.ne.railways.tezcustoms.service.service.transitdeclaration;

import kz.ne.railways.tezcustoms.service.service.bean.PrevInfoBeanDAOLocal;
import ru.customs.information.customsdocuments.pirwinformationcu._5_11.PIRWInformationCUType;

public interface PIMessageBuilderLocal {
    public PIRWInformationCUType build(Long invoiceUin);

    public void setDao(PrevInfoBeanDAOLocal dao);
}
