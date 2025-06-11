package com.proyecto.spikyhair.service.DAO;
import java.util.List;

public interface Idao <T, ID> {

    // metodo para listar todos los elementos
    List<T> getAll();

    // metodo para listar un elemento por id
    T getById(ID id);
    
    // metodo para crear
    T create(T entity);

    // metodo para actualizar
    T update(T entity);

    // metodo para eliminar
    void delete(ID id);

}
