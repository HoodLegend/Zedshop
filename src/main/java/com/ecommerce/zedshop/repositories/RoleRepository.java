package com.ecommerce.zedshop.repositories;

import com.ecommerce.zedshop.models.Role;
import com.ecommerce.zedshop.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
