package kz.ne.railways.tezcustoms.service.service;

import kz.ne.railways.tezcustoms.service.model.FormData;
import kz.ne.railways.tezcustoms.service.model.UserInvoices;

import java.util.List;

public interface UserInvoiceService {

    List<UserInvoices> getUserInvoices(Long userId);

    FormData getInvoice(Long id);

    boolean existsByInvcNum(String invcNum);
}
