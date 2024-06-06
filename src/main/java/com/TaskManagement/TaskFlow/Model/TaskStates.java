package com.TaskManagement.TaskFlow.Model;

import java.util.Set;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "task_states")
@Getter
@Setter
public class TaskStates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String stateName;

    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL)
    private Set<Tasks> tasks;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Boards board;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

}
