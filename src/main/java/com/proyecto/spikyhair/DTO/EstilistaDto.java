package com.proyecto.spikyhair.DTO;

import org.springframework.web.multipart.MultipartFile;

import com.proyecto.spikyhair.entity.Estilista;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstilistaDto {
    private Long id;
    private String nombre;
    private String especialidad;
    private Long peluqueriaId;

    private MultipartFile archivoImagen;  // archivo subido
    private String imagenPerfil;  
    public EstilistaDto(Estilista estilista) {
    if (estilista != null) {
        this.id = estilista.getId();
        this.nombre = estilista.getNombre();
        this.especialidad = estilista.getEspecialidad();
        this.peluqueriaId = estilista.getPeluqueria() != null ? estilista.getPeluqueria().getId() : null;
        this.imagenPerfil = estilista.getImagenPerfil();
    }
}
        // nombre del archivo que se guarda en BD
}


