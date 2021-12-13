package spd.trello.domain;

import java.util.ArrayList;
import java.util.List;

public class Workspace {
    private String name;
    private List<Board> boards;
    private List<Member> members = new ArrayList<>();
    private String description;
    private WorkspaceVisibility visibility = WorkspaceVisibility.PRIVATE;
}
