package com.bmri.blogbackend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
public class PostEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    @Lob
    private String content;

    private String category;

    @ElementCollection
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<String> tags = new ArrayList<>();

    @Column(nullable = false)
    private boolean published = false;

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public void setTags(List<String> newTags) {
        if (newTags != null) {
            this.tags = List.copyOf(newTags);
        } else {
            this.tags = Collections.emptyList();
        }
    }

}
