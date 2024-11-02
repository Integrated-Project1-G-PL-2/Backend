package com.itbangmodkradankanbanapi.db1.v3.repositories;

import com.itbangmodkradankanbanapi.db1.v3.entities.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface InvitationRepository extends JpaRepository<Invitation, Invitation.PendingId> {

    List<Invitation> findAllByBoard_Id(String boardId);
}
