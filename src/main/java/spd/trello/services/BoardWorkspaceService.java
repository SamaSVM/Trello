package spd.trello.services;

import spd.trello.domain.Board;
import spd.trello.repository.BoardWorkspaceRepository;

import java.util.List;
import java.util.UUID;

public class BoardWorkspaceService {
    public BoardWorkspaceService(BoardWorkspaceRepository repository) {
        this.repository = repository;
    }

    private final BoardWorkspaceRepository repository;

    public List<Board> getAllBoardsForWorkspace(UUID workspaceId) {
        return repository.findAllByWorkspaceId(workspaceId);
    }
}
