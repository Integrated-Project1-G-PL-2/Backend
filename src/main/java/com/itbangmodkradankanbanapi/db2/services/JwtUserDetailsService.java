package com.itbangmodkradankanbanapi.db2.services;

import com.itbangmodkradankanbanapi.db1.v3.entities.BoardOfUser;
import com.itbangmodkradankanbanapi.db1.v3.entities.Invitation;
import com.itbangmodkradankanbanapi.db1.v3.entities.LocalUser;
import com.itbangmodkradankanbanapi.db1.v3.service.BoardService;
import com.itbangmodkradankanbanapi.db1.v3.service.InvitationService;
import com.itbangmodkradankanbanapi.db1.v3.service.UserLocalService;
import com.itbangmodkradankanbanapi.db2.entities.AuthUser;
import com.itbangmodkradankanbanapi.db2.entities.User;
import com.itbangmodkradankanbanapi.db2.repositories.UserRepository;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLocalService userLocalService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private InvitationService invitationService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        userLocalService.addLocalUser(user);
        UserDetails userDetails = new AuthUser(userName, user.getPassword());
        return userDetails;
    }

    public boolean existUser(String oid, String email) {
        User user = userRepository.findUserByEmailAndOid(email, oid);
        return user != null;
    }

    public User getUserByOidAndEmail(String oid, String email) {
        return userRepository.findUserByEmailAndOid(email, oid);
    }

    public UserDetails loadUserByUsername(String userName, String token, String boardId, String oid) throws ItemNotFoundException {
        User user = userRepository.findByUsername(userName);
        LocalUser localUser = userLocalService.getLocalUserByOid(oid);
        if (user == null && localUser == null) {
            throw new ItemNotFoundException("User not found");
        } else if (user != null) {
            userLocalService.addLocalUser(user);
        }
        List<GrantedAuthority> roles = new ArrayList<>();
        // get , post without board id
        if (boardId == null && user != null) {
            roles.add(new SimpleGrantedAuthority("OWNER"));
            return new AuthUser(user.getUsername(), user.getPassword(), roles);
        } else if (boardId == null && localUser != null) {
            roles.add(new SimpleGrantedAuthority("OWNER"));
            return new AuthUser(localUser.getUsername(), "", roles);
        }
        BoardOfUser boardOfUser = boardService.validateUserAndBoard(token, boardId);
        if (boardOfUser != null && boardService.isOwner(boardOfUser)) {
            roles.add(new SimpleGrantedAuthority("OWNER"));
        } else if (boardOfUser != null && boardService.canModify(boardOfUser)) {
            roles.add(new SimpleGrantedAuthority("COLLABORATOR-WRITER"));
        } else if (boardOfUser != null && boardService.canAccess(boardOfUser)) {
            roles.add(new SimpleGrantedAuthority("COLLABORATOR-READER"));
        } else if (boardService.isPublicAccessibility(boardService.getBoardById(boardId))) {
            roles.add(new SimpleGrantedAuthority("PUBLIC-ACCESS"));
        }
        return new AuthUser(localUser.getUsername(), "", roles);
    }

    public UserDetails loadUserByUsernameForInvitation(String userName, String token, String boardId, String oid) throws ItemNotFoundException {
        User user = userRepository.findByUsername(userName);
        LocalUser localUser = userLocalService.getLocalUserByOid(oid);
        if (user == null && localUser == null) {
            throw new ItemNotFoundException("User not found");
        } else if (user != null) {
            userLocalService.addLocalUser(user);
        }
        List<GrantedAuthority> roles = new ArrayList<>();
        Invitation invitation = invitationService.validateUserAndBoard(token, boardId);
        BoardOfUser boardOfUser = boardService.validateUserAndBoard(token, boardId);
        if (boardOfUser != null && boardService.isOwner(boardOfUser)) {
            roles.add(new SimpleGrantedAuthority("OWNER"));
        } else if (invitation != null) {
            roles.add(new SimpleGrantedAuthority("INVITATION"));
        } else {
            throw new ItemNotFoundException("active invitation not found");
        }
        return new AuthUser(user.getUsername(), user.getPassword(), roles);
    }

}


