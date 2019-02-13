package com.propra.happybay.Repository;

import com.propra.happybay.Model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<Role,Long> {
    List<Role> findAll();
    public Role findByRole(String role);
}
