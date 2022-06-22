package kz.ne.railways.tezcustoms.service.service;


import kz.ne.railways.tezcustoms.service.entity.User;

public interface EcpService {
    boolean isValidSigner(String signedData, User user);

    boolean isValidSigner(String signedData, String bin);
}
