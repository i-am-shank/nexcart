package com.springProjects.onlineStore.category.repository;

import com.springProjects.onlineStore.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    public List<Category> findByCategoryIdInAndDeletedFalse(List<Integer> categoryIds);

    public Category findByCategoryIdAndDeletedFalse(Integer categoryId);

    public List<Category> findByDeletedFalse();

    public Page<Category> findByDeletedFalse(Pageable pageable);

    // "%..%" - Containing  ,  "..%" - StartingWith  ,  "%.." - EndingWith
    public List<Category> findByTitleContainingAndDeletedFalse(String searchKeyword);

    public Page<Category> findByTitleContainingAndDeletedFalse(String searchKeyword, Pageable pageable);
}
