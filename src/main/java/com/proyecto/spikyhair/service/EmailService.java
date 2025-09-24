package com.proyecto.spikyhair.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoReserva(String correo, String nombreUsuario, String servicio, String fecha, String hora)
            throws MessagingException {

        String asunto = "Confirmación de Reserva";
        String mensaje = "Hola " + nombreUsuario + ",\n\n" +
                "Tu reserva para el servicio: " + servicio +
                " ha sido registrada para la fecha: " + fecha + " a las " + hora + ".\n\n" +
                "¡Gracias por confiar en nosotros!";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(correo);
        message.setSubject(asunto);
        message.setText(mensaje);

        mailSender.send(message);
        try {
            Thread.sleep(2000); // Pausa de 2 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void enviarCorreosMasivos() {
        
        throw new UnsupportedOperationException("Unimplemented method 'enviarCorreosMasivos'");
    }
}
