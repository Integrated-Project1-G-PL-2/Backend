package com.itbangmodkradankanbanapi.db1.service;

import com.itbangmodkradankanbanapi.db1.entities.LocalUser;
import com.itbangmodkradankanbanapi.db1.repositories.UserLocalRepository;
import com.itbangmodkradankanbanapi.db2.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLocalService {
    private UserLocalRepository repository;

    @Autowired
    public UserLocalService(UserLocalRepository repository){
        this.repository = repository;
    }

    public void addLocalUser(User user){
        LocalUser localUser  = repository.findByOid(user.getOid());
        if (localUser == null) {
            LocalUser newLocalUser = new LocalUser(user.getOid(),user.getUsername() ,user.getName());
            repository.save(newLocalUser);
        }
    }

}
