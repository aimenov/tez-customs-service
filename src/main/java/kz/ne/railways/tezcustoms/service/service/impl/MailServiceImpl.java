package kz.ne.railways.tezcustoms.service.service.impl;

import kz.ne.railways.tezcustoms.service.entity.User;
import kz.ne.railways.tezcustoms.service.service.MailService;
import kz.ne.railways.tezcustoms.service.util.HtmlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    @Value("${server.baseUrl}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String username;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    @Async
    @Override
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        if (log.isDebugEnabled()) {
            log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                    isMultipart, isHtml, to, subject, content);
        } else {
            log.info("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}'",
                    isMultipart, isHtml, to, subject);
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            if(isHtml) {
                isMultipart = true;
            }
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, "UTF-8");
            message.setFrom(username);
            message.setTo(to);
            message.setSubject(subject);
            if(isHtml){
                message.setText(HtmlUtils.html2text(content), content);
            } else {
                message.setText(content);
            }
            javaMailSender.send(mimeMessage);
            log.info("Sent email to User '{}'", to);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.warn("Email could not be sent to user '{}'", to, e);
            } else {
                log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
            }
        }
    }

    @Async
    @Override
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        Locale locale = Locale.getDefault();
        Context context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("baseUrl", baseUrl);
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    @Async
    @Override
    public void sendActivationEmail(User user) {
        log.debug("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "activationEmail", "email.activation.title");
    }


}
