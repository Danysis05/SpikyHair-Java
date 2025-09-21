package com.proyecto.spikyhair.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.List;
import com.proyecto.spikyhair.entity.Reserva;
import com.proyecto.spikyhair.enums.Estado;
import com.proyecto.spikyhair.repository.ReservasRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoReserva(String correo, String nombreUsuario, String servicio, String fecha)
            throws MessagingException {

        String asunto = "Confirmación de Reserva";
        String mensaje = "Hola " + nombreUsuario + ",\n\n" +
                "Tu reserva para el servicio: " + servicio +
                " ha sido registrada para la fecha: " + fecha + ".\n\n" +
                "¡Gracias por confiar en nosotros!";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(correo);
        message.setSubject(asunto);
        message.setText(mensaje);

        mailSender.send(message);
    }

    public void enviarCorreosMasivos() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'enviarCorreosMasivos'");
    }
}
