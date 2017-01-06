package ua.rd.cm.web.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.rd.cm.domain.Talk;
import ua.rd.cm.domain.User;
import ua.rd.cm.domain.UserInfo;
import ua.rd.cm.services.*;
import ua.rd.cm.web.controller.dto.MessageDto;
import ua.rd.cm.web.controller.dto.TalkDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/talk")
public class TalkController {

	private ModelMapper mapper;
	private UserService userService;
	private TalkService talkService;
	private StatusService statusService;
	private TypeService typeService;
	private LanguageService languageService;
	private LevelService levelService;
	private TopicService topicService;

	private static final String DEFAULT_TALK_STATUS = "New";
	public static final String NEW = "New";
	public static final String IN_PROGRESS = "In Progress";
	public static final String REJECTED = "Rejected";
	public static final String APPROVED= "Approved";
	@Autowired
	public TalkController(ModelMapper mapper, UserService userService, TalkService talkService,
						  StatusService statusService, TypeService typeService, LevelService levelService,
						  LanguageService languageService, TopicService topicService) {
		this.mapper = mapper;
		this.userService = userService;
		this.talkService = talkService;
		this.statusService = statusService;
		this.topicService = topicService;
		this.typeService = typeService;
		this.languageService = languageService;
		this.levelService = levelService;
	}

	@PreAuthorize("hasRole(ROLE_ORGANISER)")
	@GetMapping("/{talkId}")
	public ResponseEntity getTalk(@PathVariable Long talkId){
		TalkDto talkDto = entityToDto(talkService.findTalkById(talkId));
		HttpStatus status = HttpStatus.OK;
		if(talkDto == null){
			status = HttpStatus.NOT_FOUND;
		}
		return new ResponseEntity<>(talkDto, status);
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping
	public ResponseEntity submitTalk(@Valid @RequestBody TalkDto dto, BindingResult bindingResult, HttpServletRequest request) {
		MessageDto messageDto = new MessageDto();
		HttpStatus httpStatus;

		if (bindingResult.hasFieldErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fields_error");
		}

		User currentUser = userService.getByEmail(request.getRemoteUser());

		if (!checkForFilledUserInfo(currentUser)) {
			httpStatus = HttpStatus.FORBIDDEN;
		} else {
			saveNewTalk(dto, currentUser);
			httpStatus = HttpStatus.OK;
		}
		return ResponseEntity.status(httpStatus).body(messageDto);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping
	public ResponseEntity<List<TalkDto>> getTalks(HttpServletRequest request) {
		List<TalkDto> userTalkDtoList;
		
		if(request.isUserInRole("ORGANISER")){
			userTalkDtoList = getTalksForOrganiser();
		}else {
			userTalkDtoList = getTalksForSpeaker(request.getRemoteUser());
		}
		return new ResponseEntity<>(userTalkDtoList, HttpStatus.OK);
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/reject")
	public ResponseEntity rejectTalk(@Valid @RequestBody TalkDto dto, HttpServletRequest request) {
		if(!request.isUserInRole("ORGANISER")){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
		}
		if (dto.getOrganiserComment()==null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("empty_comment");
		}
		if(!(dto.getStatusName().equals(NEW) || dto.getStatusName().equals(IN_PROGRESS))){
			return ResponseEntity.status(HttpStatus.CONFLICT).body("wrong_status");
		}
		Talk talk=talkService.findTalkById(dto.getId());

		if(talk==null){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no_talk");
		}

		talk.setOrganiserComment(dto.getOrganiserComment());
		talk.setStatus(statusService.getByName(REJECTED));
		talkService.update(talk);
		return ResponseEntity.status(HttpStatus.OK).body("successfully_rejected");
	}

	private List<TalkDto> getTalksForSpeaker(String userEmail){
		User currentUser = userService.getByEmail(userEmail);

		return talkService.findByUserId(currentUser.getId())
				.stream()
				.map(this::entityToDto)
				.collect(Collectors.toList());
	}

	private List<TalkDto> getTalksForOrganiser(){
		return talkService.findAll()
				.stream()
				.map(this::entityToDto)
				.collect(Collectors.toList());
	}

	private void saveNewTalk(TalkDto dto, User currentUser) {
		dto.setStatusName(DEFAULT_TALK_STATUS);
		Talk currentTalk = dtoToEntity(dto);
		currentTalk.setUser(currentUser);
		talkService.save(currentTalk);
	}

	private TalkDto entityToDto(Talk talk) {
		TalkDto dto = mapper.map(talk, TalkDto.class);
		dto.setSpeakerFullName(talk.getUser().getFirstName() + " " + talk.getUser().getLastName());
		dto.setDate(talk.getTime().toString());
		return dto;
	}

	private Talk dtoToEntity(TalkDto dto) {
		Talk talk = mapper.map(dto, Talk.class);
		talk.setTime(LocalDateTime.now());
		talk.setStatus(statusService.getByName(dto.getStatusName()));
		talk.setLanguage(languageService.getByName(dto.getLanguageName()));
		talk.setLevel(levelService.getByName(dto.getLevelName()));
		talk.setType(typeService.getByName(dto.getTypeName()));
		talk.setTopic(topicService.getByName(dto.getTopicName()));
		return talk;
	}

	private boolean checkForFilledUserInfo(User currentUser) {
		UserInfo currentUserInfo = currentUser.getUserInfo();
		return !(currentUserInfo.getShortBio().isEmpty() ||
				currentUserInfo.getJobTitle().isEmpty() ||
				currentUserInfo.getCompany().isEmpty());
	}
}