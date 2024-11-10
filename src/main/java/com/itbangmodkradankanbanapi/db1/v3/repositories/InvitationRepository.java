package com.itbangmodkradankanbanapi.db1.v3.repositories;

import com.itbangmodkradankanbanapi.db1.v3.entities.Board;
import com.itbangmodkradankanbanapi.db1.v3.entities.BoardOfUser;
import com.itbangmodkradankanbanapi.db1.v3.entities.Invitation;
import com.itbangmodkradankanbanapi.db1.v3.entities.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface InvitationRepository extends JpaRepository<Invitation, Invitation.PendingId> {

    List<Invitation> findAllByBoard_Id(String boardId);

    List<Invitation> findAllByLocalUser(LocalUser localUser);

    Invitation findInvitationByLocalUserAndBoard(LocalUser localUser, Board board);
}
