package com.storemart.Oauth2authorizationserver.models.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name="employee_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProfile {


    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @Column(name = "username", nullable = false, length = 16)
    private String username;

    @Column(name = "email", nullable = false, length = 60)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private Long phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private PermissionsGroup group;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="lookup_employee_permission", joinColumns=@JoinColumn(name="employee_id"))
    @Column(name="permission_id")
    private Set<String> employeePermissions = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "lookup_employee_supervisor",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "supervisor_id"))
    @JsonIgnore
    private Set<EmployeeProfile> supervisors = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "lookup_employee_supervisor",
            joinColumns = @JoinColumn(name = "supervisor_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id"))
    @JsonIgnore
    private Set<EmployeeProfile> supervisees = new LinkedHashSet<>();

    public boolean equals(EmployeeProfile obj) {
        return this.id.equals(obj.getId());
    }
}
