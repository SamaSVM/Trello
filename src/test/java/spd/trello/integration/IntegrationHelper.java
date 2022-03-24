package spd.trello.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.*;
import spd.trello.repository.*;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

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
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private LabelRepository labelRepository;

    public User getNewUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName("first name");
        user.setLastName("last name");
        user.setTimeZone(ZoneId.systemDefault().toString());
        return userRepository.save(user);
    }

    public List<User> getUsersArray(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
    }

    public Member getNewMember(String email) {
        User user = getNewUser(email);
        Member member = new Member();
        member.setCreatedBy(user.getEmail());
        member.setCreatedDate(Date.valueOf(LocalDate.now()));
        member.setUserId(user.getId());
        return memberRepository.save(member);
    }

    public List<Member> getMembersArray(MvcResult mvcResult)
            throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
    }

    public Workspace getNewWorkspace(String email) {
        Member member = getNewMember(email);
        Workspace workspace = new Workspace();
        workspace.setCreatedBy(member.getCreatedBy());
        workspace.setCreatedDate(Date.valueOf(LocalDate.now()));
        workspace.setName("name");
        Set<UUID> membersId = new HashSet<>();
        membersId.add(member.getId());
        workspace.setMembersId(membersId);
        return workspaceRepository.save(workspace);
    }

    public List<Workspace> getWorkspacesArray(MvcResult mvcResult)
            throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
    }

    public Board getNewBoard(String email) {
        Workspace workspace = getNewWorkspace(email);
        Board board = new Board();
        board.setCreatedBy(workspace.getCreatedBy());
        board.setCreatedDate(Date.valueOf(LocalDate.now()));
        board.setName("name");
        board.setWorkspaceId(workspace.getId());
        board.setMembersId(workspace.getMembersId());
        return boardRepository.save(board);
    }

    public List<Board> getBoardsArray(MvcResult mvcResult)
            throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
    }

    public CardList getNewCardList(String email) {
        Board board = getNewBoard(email);
        CardList cardList = new CardList();
        cardList.setBoardId(board.getId());
        cardList.setCreatedBy(board.getCreatedBy());
        cardList.setCreatedDate(Date.valueOf(LocalDate.now()));
        cardList.setName("name");
        return cardListRepository.save(cardList);
    }

    public List<CardList> getCardListsArray(MvcResult mvcResult)
            throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
    }

    public Card getNewCard(String email) {
        CardList cardList = getNewCardList(email);

        Reminder reminder = new Reminder();
        reminder.setCreatedBy(cardList.getCreatedBy());
        reminder.setCreatedDate(Date.valueOf(LocalDate.now()));
        reminder.setRemindOn(Date.valueOf(LocalDate.now()));
        reminder.setStart(Date.valueOf(LocalDate.now()));
        reminder.setEnd(Date.valueOf(LocalDate.now()));

        Card card = new Card();
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(Date.valueOf(LocalDate.now()));
        card.setCardListId(cardList.getId());
        card.setName("name");
        card.setReminder(reminder);
        card.setMembersId(getMembersIdFromCardList(cardList));
        return cardRepository.save(card);
    }

    public List<Card> getCardsArray(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
    }

    public Comment getNewComment(String email) {
        Card card = getNewCard(email);
        Comment comment = new Comment();
        comment.setCardId(card.getId());
        comment.setCreatedBy(card.getCreatedBy());
        comment.setCreatedDate(card.getCreatedDate());
        comment.setText("text");
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsArray(MvcResult mvcResult)
            throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
    }

    public Attachment getNewAttachment(String email) {
        Card card = getNewCard(email);
        Attachment attachment = new Attachment();
        attachment.setCardId(card.getId());
        attachment.setCreatedBy(card.getCreatedBy());
        attachment.setCreatedDate(card.getCreatedDate());
        attachment.setName("name");
        attachment.setLink("link");
        return attachmentRepository.save(attachment);
    }

    public List<Attachment> getAttachmentsArray(MvcResult mvcResult)
            throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
    }

    public Label getNewLabel(String email) {
        Card card = getNewCard(email);
        Label label = new Label();
        label.setCardId(card.getId());
        label.setName("name");
        label.setColor(getNewColor());
        return labelRepository.save(label);
    }

    public List<Label> getLabelsArray(MvcResult mvcResult)
            throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
    }

    public Color getNewColor() {
        Color color = new Color();
        color.setRed(1);
        color.setGreen(2);
        color.setBlue(3);
        return color;
    }

    public Reminder getNewReminder(String email) {
        Reminder reminder = new Reminder();
        reminder.setCreatedBy(email);
        reminder.setCreatedDate(Date.valueOf(LocalDate.now()));
        reminder.setRemindOn(Date.valueOf(LocalDate.now()));
        reminder.setStart(Date.valueOf(LocalDate.now()));
        reminder.setEnd(Date.valueOf(LocalDate.now()));
        return reminder;
    }

    public Set<UUID> getMembersIdFromCardList(CardList cardList) {
        Optional<Board> board = boardRepository.findById(cardList.getBoardId());
        return board.orElseThrow().getMembersId();
    }

    public Set<UUID> getIdsFromJson(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, new TypeReference<>() {
        });
    }
}
