package com.serh.trackmoney.repository;

import com.serh.trackmoney.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
