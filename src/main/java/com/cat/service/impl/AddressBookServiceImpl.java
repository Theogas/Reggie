package com.cat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cat.mapper.AddressBookMapper;
import com.cat.pojo.AddressBook;
import com.cat.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
