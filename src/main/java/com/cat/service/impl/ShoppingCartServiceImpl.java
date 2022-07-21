package com.cat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cat.mapper.ShoppingCartMapper;
import com.cat.pojo.ShoppingCart;
import com.cat.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
