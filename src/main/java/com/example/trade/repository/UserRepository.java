package com.example.trade.repository;

import com.example.trade.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User getByUsername(String username);

    Set<User> findAllByActiveIsFalse();

}
