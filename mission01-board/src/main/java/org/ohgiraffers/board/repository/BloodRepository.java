package org.ohgiraffers.board.repository;

import org.ohgiraffers.board.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodRepository extends JpaRepository<Post, Long> {
}
