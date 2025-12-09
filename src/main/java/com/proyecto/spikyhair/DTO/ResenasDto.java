package com.proyecto.spikyhair.DTO;

public class ResenasDto {
    private Long id;
    private String comentario;
    private int puntuacion;
    private UsuarioDto usuario;
    private Long peluqueriaId;

    public ResenasDto() {
    }

    public ResenasDto(Long id, String comentario, int puntuacion, UsuarioDto usuario, Long peluqueriaId) {
        this.id = id;
        this.comentario = comentario;
        this.puntuacion = puntuacion;
        this.usuario = usuario;
        this.peluqueriaId = peluqueriaId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public UsuarioDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDto usuario) {
        this.usuario = usuario;
    }

    public Long getPeluqueriaId() {
        return peluqueriaId;
    }

    public void setPeluqueriaId(Long peluqueriaId) {
        this.peluqueriaId = peluqueriaId;
    }
    
    public String getNombreUsuario() {
        return usuario != null ? usuario.getNombre() : "Desconocido";
    }


}