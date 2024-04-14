package com.study.tasker.mapper;

import com.study.tasker.entity.User;
import com.study.tasker.model.UserModel;
import com.study.tasker.model.UserModelList;
import com.study.tasker.web.model.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User userRequestToUser(UserRequest request);

    @Mapping(source = "userId", target = "id")
    User userRequestToUser(String userId, UserRequest request);

    UserModel userToUserModel(User user);

    default UserModelList listOfUserToUserModelList(List<User> userList) {
        UserModelList userModelList = new UserModelList();
        userModelList.setModelList(userList
                .stream()
                .map(this::userToUserModel)
                .toList());

        return userModelList;
    }

}
