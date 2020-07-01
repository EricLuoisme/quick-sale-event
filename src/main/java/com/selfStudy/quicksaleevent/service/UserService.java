package com.selfStudy.quicksaleevent.service;

import com.selfStudy.quicksaleevent.dao.UserDao;
import com.selfStudy.quicksaleevent.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    UserDao userDao; // injected by constructor

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User getById(int id) {
        return userDao.getById(id);
    }

    @Transactional
    public boolean tx() {
        User u1 = new User();
        u1.setId(20);
        u1.setName("test for insertion");
        userDao.insert(u1);

        User u2 = new User();
        u2.setId(2);
        u2.setName("test for insertion");
        userDao.insert(u2);

        return true;
    }
}
