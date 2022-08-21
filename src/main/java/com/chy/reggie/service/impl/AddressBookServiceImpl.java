package com.chy.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chy.reggie.javabean.AddressBook;
import com.chy.reggie.mapper.AddressBookMapper;
import com.chy.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper,AddressBook> implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;
}
