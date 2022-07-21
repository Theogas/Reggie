package com.cat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cat.mapper.UserMapper;
import com.cat.pojo.User;
import com.cat.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
