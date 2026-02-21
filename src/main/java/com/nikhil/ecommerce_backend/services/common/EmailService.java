package com.nikhil.ecommerce_backend.services.common;

import com.nikhil.ecommerce_backend.entities.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final MessageSource messageSource;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${admin.mail}")
    private String adminMail;

    @Async
    public void sendActivationEmail(String to, String token, Locale locale) {
        String activationLink = baseUrl + "/auth/activate?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(messageSource.getMessage("email.activation.subject", null, locale));
        message.setText(messageSource.getMessage("email.activation.body", new Object[]{activationLink}, locale));
        mailSender.send(message);
    }

    @Async
    public void notifyActivation(String to, Locale locale) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(messageSource.getMessage("email.activation.success.subject", null, locale));
        message.setText(messageSource.getMessage("email.activation.success.body", null, locale));
        mailSender.send(message);
    }

    @Async
    public void sendSellerPendingApprovalEmail(String to, Locale locale) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(messageSource.getMessage("email.seller.pending.approval.subject", null, locale));
        message.setText(messageSource.getMessage("email.seller.pending.body", null, locale));
        mailSender.send(message);
    }

    @Async
    public void sendAccountLockedEmail(String to, Locale locale) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(messageSource.getMessage("email.account.locked.subject", null, locale));
        message.setText(messageSource.getMessage("email.account.locked.body", null, locale));
        mailSender.send(message);
    }

    @Async
    public void sendPasswordChangedEmail(String to, Locale locale) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(messageSource.getMessage("email.password.changed.subject", null, locale));
        message.setText(messageSource.getMessage("email.password.changed.body", null, locale));
        mailSender.send(message);
    }

    @Async
    public void sendPasswordResetEmail(String to, String token, Locale locale) {
        String resetLink = baseUrl + "/auth/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(messageSource.getMessage("email.password.reset.subject", null, locale));
        message.setText(messageSource.getMessage("email.password.reset.body", new Object[]{resetLink}, locale));
        mailSender.send(message);
    }

    @Async
    public void sendAccountActivatedByAdminEmail(String to, Locale locale) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(messageSource.getMessage("email.admin.activated.subject", null, locale));
        message.setText(messageSource.getMessage("email.admin.activated.body", null, locale));
        mailSender.send(message);
    }

    @Async
    public void sendAccountDeactivatedByAdminEmail(String to, Locale locale) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(messageSource.getMessage("email.admin.deactivated.subject", null, locale));
        message.setText(messageSource.getMessage("email.admin.deactivated.body", null, locale));
        mailSender.send(message);
    }

    @Async
    public void sendProductActivationEmailToAdmin(Product product)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminMail);
        message.setSubject("New Product Awaiting Activation: " + product.getName());
        message.setText(
                "A new product has been added by seller '" + product.getSeller().getCompanyName() + "' " +
                        "and is awaiting your approval.\n\n" +
                        "Product Name: " + product.getName() + "\n" +
                        "Brand: " + product.getBrand() + "\n" +
                        "Category: " + product.getCategory().getName() + "\n\n" +
                        "Please log in to the admin panel to review and activate it."
        );
        mailSender.send(message);
    }
    @Async
    public void sendProductDeactivationEmail(Product product, Locale locale) {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(product.getSeller().getEmail());
            message.setSubject(messageSource.getMessage("email.product.deactivated.subject", null, locale));
            message.setText("Hello " + product.getSeller().getFirstName() + ",\n\n"
                + "This is to inform you that your product listing has been deactivated by an administrator.\n\n"
                + "Product Details:\n"
                + "ID: " + product.getId() + "\n"
                + "Name: " + product.getName() + "\n"
                + "Brand: " + product.getBrand() + "\n\n"
                + "Please contact support if you have any questions.\n\n"
                + "Thank you,\nThe Admin Team");
            mailSender.send(message);
    }

    @Async
    public void sendProductActivationEmail(Product product, Locale locale) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(product.getSeller().getEmail());
        message.setSubject(messageSource.getMessage("email.product.activated.subject", null, locale));
        message.setText("Hello " + product.getSeller().getFirstName() + ",\n\n"
                + "This is to inform you that your product listing has been activated by an administrator.\n\n"
                + "Product Details:\n"
                + "ID: " + product.getId() + "\n"
                + "Name: " + product.getName() + "\n"
                + "Brand: " + product.getBrand() + "\n\n"
                + "Please contact support if you have any questions.\n\n"
                + "Thank you,\nThe Admin Team");
        mailSender.send(message);
    }

    @Async
    public void scheduledEmail(String to, String subject, String msg ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(msg);
        mailSender.send(message);
    }
}
