package com.proyecto.spikyhair.repository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailRepository {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoReserva(String destinatario, String nombre, String servicio, String fechaHora) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);
        helper.setTo(destinatario);
        helper.setSubject("Confirmación de tu reserva");
        helper.setText(
            "<h1>Hola " + nombre + "</h1>" +
            "<p>Tu reserva ha sido confirmada.</p>" +
            "<p><b>Servicio:</b> " + servicio + "</p>" +
            "<p><b>Fecha y Hora:</b> " + fechaHora + "</p>" +
            "<p>¡Gracias por confiar en nuestro sistema de reservas!</p>",
            true
        );
        mailSender.send(mensaje);
    }
}



