package com.itbangmodkradankanbanapi.db1.service;


import com.itbangmodkradankanbanapi.db1.dto.StatusDTO;
import com.itbangmodkradankanbanapi.db1.entities.Board;
import com.itbangmodkradankanbanapi.db1.entities.CenterStatus;
import com.itbangmodkradankanbanapi.db1.entities.Status;
import com.itbangmodkradankanbanapi.db1.repositories.BoardRepository;
import com.itbangmodkradankanbanapi.db1.repositories.CenterStatusRepository;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import com.itbangmodkradankanbanapi.db1.entities.Task;
import com.itbangmodkradankanbanapi.db1.repositories.StatusRepository;
import com.itbangmodkradankanbanapi.db1.repositories.TaskRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


@Service
public class StatusService {
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private CenterStatusRepository centerStatusRepository;
    @Autowired
    private BoardRepository boardRepository;

    public List<Status> findAllStatus(Board board) {
        String useDefault = board.getDefaultStatus();
        List<CenterStatus> centerStatusList = centerStatusRepository.findAllByOrderByIdAsc();

        List<Status> selectedStatusFromCenter = getSelectedStatusFromCenter(useDefault, centerStatusList);

        List<Status> statusList = statusRepository.findAllByBoard(board);

        selectedStatusFromCenter.addAll(statusList);
        return selectedStatusFromCenter;
    }

    @Transactional
    public StatusDTO createNewStatus(StatusDTO newStatus, Board board) {
        String useDefault = board.getDefaultStatus();
        List<CenterStatus> centerStatusList = centerStatusRepository.findAllByOrderByIdAsc();

        List<Status> selectedStatusFromCenter = getSelectedStatusFromCenter(useDefault, centerStatusList);

        List<Status> statusListOnBoard = statusRepository.findAllByNameIgnoreCaseAndBoard(newStatus.getName(), board);
        if (!statusListOnBoard.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot duplicate name");
        }

        for (Status centerStatus : selectedStatusFromCenter) {
            if (centerStatus.getName().equalsIgnoreCase(newStatus.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot duplicate name");
            }
        }

        Status mapStatus = mapper.map(newStatus, Status.class);
        mapStatus.setBoard(board);
        Status savedStatus = statusRepository.saveAndFlush(mapStatus);
        return mapper.map(savedStatus, StatusDTO.class);
    }

    public Status findStatusById(Board board, int id) throws ItemNotFoundException {
        String useDefault = board.getDefaultStatus();
        List<CenterStatus> centerStatusList = centerStatusRepository.findAllByOrderByIdAsc();

        List<Status> selectedStatusFromCenter = getSelectedStatusFromCenter(useDefault, centerStatusList);
        for (Status centerStatus : selectedStatusFromCenter) {
            if (centerStatus.getId() == id) {
                return statusRepository.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
            }
        }
        return statusRepository.findByBoard_IdAndId(board.getId(), id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
    }

    @Transactional
    public StatusDTO updateStatus(Board board, Integer id, StatusDTO status) {
        boolean statusIsDefault = false;
        String useDefault = board.getDefaultStatus();
        List<CenterStatus> centerStatusList = centerStatusRepository.findAllByOrderByIdAsc();
        StringBuilder updatedUseDefault = new StringBuilder(useDefault);

        for (int i = 0; i < useDefault.length() && i < centerStatusList.size(); i++) {
            if (centerStatusList.get(i).getId() == id && useDefault.charAt(i) == '1') {
                updatedUseDefault.setCharAt(i, '0');
                statusIsDefault = true;
                break;
            }
        }

        Status sourceStatus = fetchStatus(id, board.getId(), statusIsDefault);

        if (statusIsDefault) {
            board.setDefaultStatus(updatedUseDefault.toString());
            boardRepository.save(board);
            StatusDTO newStatusDTO = createNewStatus(status, board);
            Status newStatus = mapper.map(newStatusDTO, Status.class);
            transferTasks(sourceStatus, newStatus);
            return newStatusDTO;
        } else {
            sourceStatus.setName(status.getName());
            sourceStatus.setDescription(status.getDescription());
            Status updateStatus = statusRepository.save(sourceStatus);
            return mapper.map(updateStatus, StatusDTO.class);
        }
    }

    // cant delete if it used as FK
    @Transactional
    public void deleteStatus(Board board, int id) {
        String useDefault = board.getDefaultStatus();
        List<CenterStatus> centerStatusList = centerStatusRepository.findAllByOrderByIdAsc();
        StringBuilder updatedUseDefault = new StringBuilder(useDefault);

        for (int i = 0; i < useDefault.length() && i < centerStatusList.size(); i++) {
            if (centerStatusList.get(i).getId() == id && useDefault.charAt(i) == '1') {
                updatedUseDefault.setCharAt(i, '0');
                break;
            }
        }
        if (updatedUseDefault.toString().equalsIgnoreCase(useDefault)) {
            Status status = statusRepository.findByBoard_IdAndId(board.getId(), id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
            statusRepository.delete(status);
        } else {
            board.setDefaultStatus(updatedUseDefault.toString());
            boardRepository.save(board);
        }
    }


    @Transactional
    public void deleteStatusAndTransfer(Board board, int id, int newId) {
        boolean oldStatusIsDefault = false;
        boolean newStatusIsDefault = false;
        String useDefault = board.getDefaultStatus();
        List<CenterStatus> centerStatusList = centerStatusRepository.findAllByOrderByIdAsc();
        StringBuilder updatedUseDefault = new StringBuilder(useDefault);

        for (int i = 0; i < useDefault.length() && i < centerStatusList.size(); i++) {
            if (centerStatusList.get(i).getId() == id && useDefault.charAt(i) == '1') {
                updatedUseDefault.setCharAt(i, '0');
                System.out.println("new is default");
                oldStatusIsDefault = true;
            } else if (centerStatusList.get(i).getId() == newId) {
                System.out.println("new is default");
                newStatusIsDefault = true;
            }
        }

        if (oldStatusIsDefault) {
            board.setDefaultStatus(updatedUseDefault.toString());
            boardRepository.save(board);
        }

        Status sourceStatus = fetchStatus(id, board.getId(), oldStatusIsDefault);
        Status targetStatus = fetchStatus(newId, board.getId(), newStatusIsDefault);

        transferTasks(sourceStatus, targetStatus);

        if (!oldStatusIsDefault || (!oldStatusIsDefault && !newStatusIsDefault)) {
            statusRepository.delete(sourceStatus);
        }
    }

    private Status fetchStatus(int statusId, String boardId, boolean isDefault) {
        return isDefault
                ? statusRepository.findById(statusId)
                .orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"))
                : statusRepository.findByBoard_IdAndId(boardId, statusId)
                .orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND2"));
    }

    private void transferTasks(Status sourceStatus, Status targetStatus) {
        List<Task> tasks = sourceStatus.getTaskV2s();
        tasks.forEach(task -> task.setStatus(targetStatus));
        taskRepository.saveAll(tasks);
    }


    private List<Status> getSelectedStatusFromCenter(String useDefault, List<CenterStatus> centerStatusList) {
        List<Status> selectedStatusFromCenter = new ArrayList<>();
        for (int i = 0; i < useDefault.length() && i < centerStatusList.size(); i++) {
            if (useDefault.charAt(i) == '1') {
                selectedStatusFromCenter.add(centerStatusList.get(i).getStatus());
            }
        }
        return selectedStatusFromCenter;
    }

}
