package com.example.corespringsecurity.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class RoleHierarchy implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "child_name")
    private String childName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_name", referencedColumnName = "child_name") // foreign key 대신 해당 데이터의 child_name 컬럼 값을 표시
    private RoleHierarchy parentName;

    @OneToMany(mappedBy = "parentName")
    private Set<RoleHierarchy> roleHierarchies = new HashSet<>();

    @Builder
    public RoleHierarchy(String childName) {
        this.childName = childName;
    }

    public void setParentName(RoleHierarchy parentName) {
        this.parentName = parentName;
    }
}
