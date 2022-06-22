package kz.ne.railways.tezcustoms.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import kz.ne.railways.tezcustoms.service.service.ImportDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/import")
@Slf4j
public class ImportDataController {

    private final ImportDataService importDataService;

    @Operation(summary = "Import TnVed from file")
    @PostMapping("/tnved")
    //@PreAuthorize("hasAnyRole('ADMIN')") TODO убрать комментарий когда появится такая роль
    public ResponseEntity<Void> uploadTnVedFromFile(@RequestParam("file") MultipartFile file) {
        log.info("Importing TnVed data from file...");
        importDataService.importTnVedDataFromXml(file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
