package kz.ne.railways.tezcustoms.service.service.transitdeclaration;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class PINamespacePrefixMapper extends NamespacePrefixMapper {

    private static final String cat_ru_PREFIX = "cat_ru";
    private static final String cat_ru_URI = "urn:customs.ru:CommonAggregateTypes:5.10.0";

    private static final String PIRWCU_PREFIX = "PIRWCU";
    private static final String PIRWCU_URI = "urn:customs.ru:Information:CustomsDocuments:PIRWInformationCU:5.10.0";

    private static final String catESAD_cu_PREFIX = "catESAD_cu";
    private static final String catESAD_cu_URI = "urn:customs.ru:CUESADCommonAggregateTypesCust:5.11.0";

    private static final String CategoryCust_PREFIX = "CategoryCust";
    private static final String CategoryCust_URI = "urn:customs.ru:Categories:3.0.0";

    @Override
    public String getPreferredPrefix(String arg0, String arg1, boolean arg2) {
        if (cat_ru_URI.equals(arg0)) {
            return cat_ru_PREFIX;
        } else if (PIRWCU_URI.equals(arg0)) {
            return PIRWCU_PREFIX;
        } else if (catESAD_cu_URI.equals(arg0)) {
            return catESAD_cu_PREFIX;
        } else if (CategoryCust_URI.equals(arg0)) {
            return CategoryCust_PREFIX;
        }
        return arg1;
    }

}
