package com.github.edivaldoramos.repository;

import com.github.edivaldoramos.model.Role;
import com.github.edivaldoramos.model.RoleEnum;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(RoleEnum name);
}