package com.study.tasker.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {

    private String name;

    private String description;

    private String status;

    private String authorId;

    private String assigneeId;

    private Set<String> observerIds;

}
