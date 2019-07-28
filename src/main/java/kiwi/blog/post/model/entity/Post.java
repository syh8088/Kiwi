package kiwi.blog.post.model.entity;

import kiwi.blog.category.model.entity.Category;
import kiwi.blog.common.model.entity.Common;
import kiwi.blog.tag.model.entity.Tag;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Post extends Common {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long postNo;

    private String title;

    private String content;

    private Long viewCount;

    @ManyToOne
    @JoinColumn(name = "categoryNo", insertable = false, updatable = false)
    private Category category;

    @ManyToMany
    @JoinTable(name = "post_tag_mapping", joinColumns = @JoinColumn(name = "post_no"), inverseJoinColumns = @JoinColumn(name = "tag_no"))
    private List<Tag> tags;
}