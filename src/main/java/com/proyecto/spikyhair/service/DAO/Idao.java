package com.proyecto.spikyhair.service.DAO;

import java.util.List;



public interface Idao<T, ID, DTO> {
    List<DTO> getAll();
    DTO getById(ID id);
    DTO save(DTO dto);
    DTO update(ID id, DTO dto);
    void delete(ID id);
    long count();
}

