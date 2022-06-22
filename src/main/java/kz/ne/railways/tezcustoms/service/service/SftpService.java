package kz.ne.railways.tezcustoms.service.service;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SftpService {

    boolean sendInvoice(InputStream is, String filename, String invoiceUn) throws JSchException, SftpException, FileNotFoundException;

    void getInvoice(String invoiceId, String uuid, OutputStream out) throws JSchException, SftpException, FileNotFoundException;

    String sendRegistrationDoc(MultipartFile file, String iin);

}
