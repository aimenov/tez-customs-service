package kz.ne.railways.tezcustoms.service.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImportDataService {
    /**
     * Import data from file and insert to the table
     * @param file file to import (this file can be found in jira ticket)
     */
    void importTnVedDataFromXml(MultipartFile file);
}
