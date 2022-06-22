package kz.ne.railways.tezcustoms.service.service.impl;

import kz.ne.railways.tezcustoms.service.model.FormData;
import kz.ne.railways.tezcustoms.service.model.UserInvoices;
import kz.ne.railways.tezcustoms.service.repository.asudkr.NeInvoiceRepository;
import kz.ne.railways.tezcustoms.service.service.UserInvoiceService;
import kz.ne.railways.tezcustoms.service.service.ForDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserInvoiceServiceImpl implements UserInvoiceService {

    private final NeInvoiceRepository neInvoiceRepository;
    private final ForDataService forDataService;

    @Override
    public List<UserInvoices> getUserInvoices(Long userId) {
        return neInvoiceRepository.getInvoicesByUser(userId);
    }

    @Override
    public FormData getInvoice(Long id) {
        return forDataService.getFormData(id.toString());
    }

    @Override
    public boolean existsByInvcNum(String invcNum){
        return neInvoiceRepository.existsByInvcNum(invcNum);
    }
}
