package com.itbangmodkradankanbanapi.db1.v3.service;

import com.itbangmodkradankanbanapi.db1.v3.entities.LocalUser;
import com.itbangmodkradankanbanapi.db1.v3.repositories.LocalUserRepository;
import com.itbangmodkradankanbanapi.db2.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLocalService {
    private LocalUserRepository repository;

    private ModelMapper mapper;

    @Autowired
    public UserLocalService(LocalUserRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public LocalUser addLocalUser(User user) {
        LocalUser localUser = repository.findByOid(user.getOid());
        if (localUser == null) {
            LocalUser newLocalUser = mapper.map(user, LocalUser.class);
            return repository.save(newLocalUser);
        }
        return null;
    }

}
