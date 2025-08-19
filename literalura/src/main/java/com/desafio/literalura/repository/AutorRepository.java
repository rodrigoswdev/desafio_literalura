package com.desafio.literalura.repository;

import com.desafio.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {

    @Query("SELECT a FROM Autor a LEFT JOIN FETCH a.libros")
    List<Autor> findAllConLibros();

    @Query("SELECT a FROM Autor a LEFT JOIN FETCH a.libros WHERE (a.anoFallecimiento IS NULL OR a.anoFallecimiento > :ano) AND a.anoNacimiento <= :ano")
    List<Autor> findAutoresVivosEnAnoConLibros(@Param("ano")int ano);

    Optional<Autor> findByNombre(String nombre);
}
