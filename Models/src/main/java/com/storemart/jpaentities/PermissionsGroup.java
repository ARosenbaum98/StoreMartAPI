package com.storemart.jpaentities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name="employee_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionsGroup {
    @Id
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<EmployeeProfile> employeeProfiles = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="lookup_employee_group_default_permissions", joinColumns=@JoinColumn(name="group_id"))
    @Column(name="permission_id")
    @JsonIgnore
    private Set<String> employeePermissions = new LinkedHashSet<>();

    @Override
    public String toString() {
        return "PermissionsGroup{" +
                "id='" + id + '\'' +
                '}';
    }
}
