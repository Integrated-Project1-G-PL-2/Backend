package com.itbangmodkradankanbanapi.db1.v3.service;

import com.itbangmodkradankanbanapi.db1.v3.dto.CollabDTOResponse;
import com.itbangmodkradankanbanapi.db1.v3.entities.Board;
import com.itbangmodkradankanbanapi.db1.v3.entities.BoardOfUser;
import com.itbangmodkradankanbanapi.db1.v3.entities.Invitation;
import com.itbangmodkradankanbanapi.db1.v3.entities.LocalUser;
import com.itbangmodkradankanbanapi.db1.v3.repositories.BoardOfUserRepository;
import com.itbangmodkradankanbanapi.db1.v3.repositories.BoardRepository;
import com.itbangmodkradankanbanapi.db1.v3.repositories.InvitationRepository;
import com.itbangmodkradankanbanapi.db1.v3.repositories.LocalUserRepository;
import com.itbangmodkradankanbanapi.db2.entities.User;
import com.itbangmodkradankanbanapi.db2.repositories.UserRepository;
import com.itbangmodkradankanbanapi.db2.services.JwtTokenUtil;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InvitationService {
    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private BoardOfUserRepository boardOfUserRepository;

    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;


    @Transactional
    public Invitation addInvitation(Invitation invitation) {
        return invitationRepository.save(invitation);
    }

    public Invitation findInvitationById(Invitation.PendingId id) {
        return invitationRepository.findById(id).orElse(null);
    }

    public List<Invitation> findAllInvitationFormBoardId(String boardId) {
        return invitationRepository.findAllByBoard_Id(boardId);
    }

    public void deleteInvitationFormId(String token, String id) {
        String oid = getOidFromToken(token);
        Invitation invitation = invitationRepository.findById(new Invitation.PendingId(id, oid)).orElse(null);
        if (invitation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found");
        }
        invitationRepository.delete(invitation);
    }

    public List<Invitation> findAllByLocalUser(LocalUser localUser) {
        return invitationRepository.findAllByLocalUser(localUser);
    }


    public CollabDTOResponse checkInvitation(String token, String id) {
        String oid = getOidFromToken(token);
        Invitation invitation = invitationRepository.findById(new Invitation.PendingId(id, oid)).orElse(null);
        if (invitation != null) {
            LocalUser localUser = getOwner(invitation.getBoard());
            return new CollabDTOResponse(invitation.getBoard().getName(), localUser.getName(), invitation.getRole().toString());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found");
    }

    private String getOidFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtTokenUtil.getOidFromToken(token);
    }

    private LocalUser getOwner(Board board) {
        return boardOfUserRepository.findBoardOfUserByBoardAndRole(board, BoardOfUser.Role.OWNER)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Owner not found"))
                .getLocalUser();
    }

    public Invitation validateUserAndBoard(String token, String boardId) {
        LocalUser localUser = getLocalUserFromToken(token);
        Board board = getBoardById(boardId);
        return invitationRepository.findInvitationByLocalUserAndBoard(localUser, board);
    }

    public LocalUser getLocalUserFromToken(String token) {
        User user = getUserFromToken(token);
        String userOid = user.getOid();
        return localUserRepository.findById(userOid).orElseThrow(() -> new ItemNotFoundException("User not found"));
    }

    public Board getBoardById(String boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board id '" + boardId + "' not found"));
    }

    private User getUserFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtTokenUtil.getUsernameFromToken(token);
        return userRepository.findByUsername(username);
    }

}
