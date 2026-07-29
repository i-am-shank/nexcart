package com.springProjects.onlineStore.user.repository;

import com.springProjects.onlineStore.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserIdAndDeletedFalse(Integer userId);

    User findByEmailAndDeletedFalse(String email);

    List<User> findByNameContainingAndDeletedFalseOrEmailStartingWithAndDeletedFalse(
            String nameKeyword, String emailKeyword);

    // above method with Sort
    List<User> findByNameContainingAndDeletedFalseOrEmailStartingWithAndDeletedFalse(
            String nameKeyword, String emailKeyword, Sort sort);

    // above method with Pagination
    Page<User> findByNameContainingAndDeletedFalseOrEmailStartingWithAndDeletedFalse(
            String nameKeyword, String emailKeyword, Pageable pageable);

    List<User> findAllByDeletedFalse();

    // above method with Sort
    List<User> findAllByDeletedFalse(Sort sort);

    // above method with Pagination
    Page<User> findAllByDeletedFalse(Pageable pageable);

    Boolean existsByUserIdAndDeletedFalse(Integer userId);
}
