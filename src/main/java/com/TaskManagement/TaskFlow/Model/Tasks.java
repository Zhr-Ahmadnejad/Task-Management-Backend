package com.TaskManagement.TaskFlow.Model;

import java.util.Set;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Tasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String taskName;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private TaskStates state;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Boards board;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private Set<SubTasks> subTasks;

}
