package com.TaskManagement.TaskFlow.Model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    private String email;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Tasks> tasks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Boards> boards;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<TaskStates> states;

}
