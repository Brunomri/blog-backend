package com.bmri.blogbackend.repositories;

import com.bmri.blogbackend.domain.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    Page<PostEntity> getByPublishedIsTrue(Pageable pageable);

    Optional<PostEntity> getByTitle(String title);

    Page<PostEntity> getByCategory(String category, Pageable pageable);

    Page<PostEntity> getByTagsContaining(String tag, Pageable pageable);

}
