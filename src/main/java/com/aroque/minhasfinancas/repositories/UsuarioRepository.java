package com.aroque.minhasfinancas.repositories;

import com.aroque.minhasfinancas.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    // Optional pq pode existir ou não
                        // Query methods
//    Optional<UsuarioModel> findByEmail(String email);

    // Igual ao de cima:
     boolean existsByEmail(String email);

     Optional<UsuarioModel> findByEmail(String email);
}
