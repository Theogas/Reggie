package com.cat.dto;

import com.cat.pojo.User;
import lombok.Data;

@Data
public class UserDto extends User {
    private String code;
}
