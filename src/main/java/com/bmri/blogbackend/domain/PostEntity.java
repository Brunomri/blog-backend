package com.bmri.blogbackend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "posts")
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
        if (this.tags != null) {
            return new ArrayList<>(tags);
        }
        return new ArrayList<>();
    }

    public void setTags(List<String> newTags) {
        if (newTags != null) {
            this.tags = new ArrayList<>(newTags);
        } else {
            this.tags = new ArrayList<>();
        }
    }

}
