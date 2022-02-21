package spd.trello.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.*;
import spd.trello.repository.*;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class IntegrationHelper {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CardListRepository cardListRepository;


    public User getNewUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName("first name");
        user.setLastName("last name");
        user.setTimeZone(ZoneId.systemDefault().toString());
        return userRepository.save(user);
    }

    public List<User> getUsersArray(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
    }

    public Member getNewMember(String email) {
        User user = getNewUser(email);
        Member member = new Member();
        member.setCreatedBy(user.getEmail());
        member.setCreatedDate(Date.valueOf(LocalDate.now()));
        member.setUserId(user.getId());
        return memberRepository.save(member);
    }

    public List<Member> getMembersArray(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
    }

    public Workspace getNewWorkspace(String email) {
        Member member = getNewMember(email);
        Workspace workspace = new Workspace();
        workspace.setCreatedBy(member.getCreatedBy());
        workspace.setCreatedDate(Date.valueOf(LocalDate.now()));
        workspace.setName("name");
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(member.getId());
        workspace.setMembersIds(membersIds);
        return workspaceRepository.save(workspace);
    }

    public List<Workspace> getWorkspacesArray(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
    }

    public Board getNewBoard(String email) {
        Workspace workspace = getNewWorkspace(email);
        Board board = new Board();
        board.setCreatedBy(workspace.getCreatedBy());
        board.setCreatedDate(Date.valueOf(LocalDate.now()));
        board.setName("name");
        board.setWorkspaceId(workspace.getId());
        board.setMembersIds(workspace.getMembersIds());
        return boardRepository.save(board);
    }

    public List<Board> getBoardsArray(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
    }

    public CardList getNewCardList(String email) {
        Board board = getNewBoard(email);
        CardList cardList = new CardList();
        cardList.setBoardId(board.getId());
        cardList.setCreatedBy(board.getCreatedBy());
        cardList.setCreatedDate(Date.valueOf(LocalDate.now()));
        return cardListRepository.save(cardList);
    }

    public List<CardList> getCardListsArray(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
    }

    public Set<UUID> getIdsFromJson(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, new TypeReference<>() {});
    }
}
