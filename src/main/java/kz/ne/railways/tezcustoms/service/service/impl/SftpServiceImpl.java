package kz.ne.railways.tezcustoms.service.service.impl;

import com.jcraft.jsch.*;
import kz.ne.railways.tezcustoms.service.service.SftpService;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.Vector;

@Service
@Slf4j
public class SftpServiceImpl implements SftpService {

    @Value("${services.internal.fileServer.prod.host}")
    private String host;

    @Value("${services.internal.fileServer.prod.port}")
    private Integer port;

    @Value("${services.internal.fileServer.prod.user}")
    private String user;

    @Value("${services.internal.fileServer.prod.password}")
    private String password;

    private final String invoicesPath = "/tezcustoms/files/invoices/";
    private final String registrationDocsPath = "/tezcustoms/files/registration_docs/";

    @Override
    public boolean sendInvoice(InputStream is, String filename, String invoiceUn) throws JSchException, SftpException, FileNotFoundException {
        boolean result = false;
        try {
            JSch jsch = new JSch();
            Session session;

            session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            String invoiceIdPath = "";
            if (NumberUtils.isCreatable(invoiceUn)) {
                Integer invoiceId = (Integer.parseInt(invoiceUn) / 10000) * 10000;
                log.debug("divided invoiceId: {}", invoiceId);
                invoiceIdPath = invoiceId.toString();
            }

            try {
                sftpChannel.mkdir(invoicesPath + invoiceIdPath);
            } catch (SftpException ex) {
                log.debug("in SFtp send: folder already exists");
            }

            try {
                sftpChannel.mkdir(invoicesPath + invoiceIdPath + "/" + invoiceUn);
            } catch (SftpException ex) {
                log.debug("in SFtp send: folder already exists");
            }

            sftpChannel.cd(invoicesPath + invoiceIdPath + "/" + invoiceUn);
            sftpChannel.put(is, filename);
            sftpChannel.exit();
            session.disconnect();
            result = true;
        } catch (Exception ex) {
            log.debug("in SFtp send: ");
        }

        return result;
    }

    @Override
    public void getInvoice(String invoiceId, String uuid, OutputStream out) throws JSchException, SftpException, FileNotFoundException {
        JSch jsch = new JSch();
        Session session;

        session = jsch.getSession(user, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect();
        // File file = new File(path);
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        // sftpChannel.cd("/tezcustoms/files/invoices/"+invoiceUn);
        log.info("path: " + invoicesPath + invoiceId + "/" + uuid);
        String invoiceIdPath = "";
        if (NumberUtils.isCreatable(invoiceId)) {
            int invoiceIdPathInt = (Integer.parseInt(invoiceId) / 10000) * 10000;
            invoiceIdPath = Integer.toString(invoiceIdPathInt);
        }
        String newPath = invoicesPath + invoiceIdPath + "/" + invoiceId + "/" + uuid;
        String oldPath = invoicesPath + invoiceId + "/" + uuid;
        Vector<ChannelSftp.LsEntry> lsEntryVector = new Vector<>();
        try {
            lsEntryVector = sftpChannel.ls(invoicesPath + invoiceIdPath + "/" + invoiceId);
        } catch (SftpException ex) {
            // TODO: handle exception
        }
        boolean foundInNewPath = false;
        for (LsEntry lsEntry : lsEntryVector) {
            if (uuid.equals(lsEntry.getFilename())) {
                foundInNewPath = true;
                break;
            }
        }
        if (foundInNewPath) {
            sftpChannel.get(newPath, out);
        } else {
            sftpChannel.get(oldPath, out);
        }
        sftpChannel.exit();
        session.disconnect();
    }

    @Override
    public String sendRegistrationDoc(MultipartFile file, String iin) {
        String filepath = null;
        try {
            JSch jsch = new JSch();
            Session session;

            session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            try {
                sftpChannel.mkdir(registrationDocsPath + iin);
            } catch (SftpException ex) {
                log.debug("in SFtp send: folder already exists");
            }

            sftpChannel.cd(registrationDocsPath + iin);

            InputStream inputStream = file.getInputStream();
            String filename = UUID.randomUUID().toString();

            sftpChannel.put(inputStream, filename);
            filepath = registrationDocsPath + iin + "/" + filename;

            sftpChannel.exit();
            session.disconnect();
        } catch (Exception ex) {
            log.debug("in SFtp send: could not save registration document");
        }

        return filepath;
    }
}
