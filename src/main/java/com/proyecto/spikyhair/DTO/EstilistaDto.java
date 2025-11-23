package com.proyecto.spikyhair.DTO;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstilistaDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String especialidad;
    private Long peluqueriaId;

    private MultipartFile archivoImagen;  // archivo subido
    private String imagenPerfil;          // nombre del archivo que se guarda en BD
}


