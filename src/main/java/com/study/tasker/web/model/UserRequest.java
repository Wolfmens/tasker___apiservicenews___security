package com.study.tasker.web.model;

import com.study.tasker.entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String name, email, password;

    private List<RoleType> roles = new ArrayList<>();

}
