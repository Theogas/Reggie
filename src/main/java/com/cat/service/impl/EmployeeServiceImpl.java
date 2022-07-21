package com.cat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cat.mapper.EmployeeMapper;
import com.cat.pojo.Employee;
import com.cat.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}

