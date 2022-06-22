package kz.ne.railways.tezcustoms.service.service.impl;

import kz.ne.railways.tezcustoms.service.entity.TnVed;
import kz.ne.railways.tezcustoms.service.mapper.TnVedMapper;
import kz.ne.railways.tezcustoms.service.repository.TnVedRepository;
import kz.ne.railways.tezcustoms.service.service.ImportDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ImportDataServiceImpl implements ImportDataService {

    private final TnVedRepository tnVedRepository;

    public void importTnVedDataFromXml(MultipartFile file) {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(file.getInputStream(), "windows-1251");

            List<TnVed> tnVeds = new ArrayList<>();
            TnVed tnVed = null;
            Map<Integer, Long> levelsMap = new HashMap<>();
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {
                        case "tnved" -> tnVed = new TnVed();
                        case "ID" -> {
                            nextEvent = reader.nextEvent();
                            tnVed.setId(Long.valueOf(nextEvent.asCharacters().getData()));
                        }
                        case "Level" -> {
                            nextEvent = reader.nextEvent();
                            int level = Integer.parseInt(nextEvent.asCharacters().getData());
                            tnVed.setLevel(level);
                            if (level > 0) {
                                tnVed.setParent(TnVedMapper.fromId(levelsMap.get(level-1)));
                            }
                            levelsMap.put(level, tnVed.getId());
                        }
                        case "Code" -> {
                            nextEvent = reader.nextEvent();
                            tnVed.setCode(nextEvent.asCharacters().getData());
                        }
                        case "CodeEx" -> {
                            nextEvent = reader.nextEvent();
                            tnVed.setCodeEx(nextEvent.asCharacters().getData());
                        }
                        case "Text" -> {
                            nextEvent = reader.nextEvent();
                            tnVed.setText(nextEvent.asCharacters().getData());
                        }
                        case "TextEx" -> {
                            nextEvent = reader.nextEvent();
                            tnVed.setTextEx(nextEvent.asCharacters().getData());
                        }
                        case "Unit" -> {
                            nextEvent = reader.nextEvent();
                            tnVed.setUnit(nextEvent.asCharacters().getData());
                        }
                        case "UnitCode" -> {
                            nextEvent = reader.nextEvent();
                            tnVed.setUnitCode(nextEvent.asCharacters().getData());
                        }
                    }
                }
                if (nextEvent.isEndElement()) {
                    EndElement endElement = nextEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("tnved")) {
                        tnVeds.add(tnVed);
                    }
                }
            }
            tnVedRepository.saveAll(tnVeds);
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
    }

}