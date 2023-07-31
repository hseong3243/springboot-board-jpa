package com.programmers.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.board.domain.User;
import com.programmers.board.dto.UserDto;
import com.programmers.board.dto.request.UserCreateRequest;
import com.programmers.board.dto.request.UserUpdateRequest;
import com.programmers.board.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureRestDocs
class UserControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    UserDto givenUserDto;

    @BeforeEach
    void setUp() {
        User user = new User("name", 20, "hobby");
        givenUserDto = UserDto.from(user);
        ReflectionTestUtils.setField(givenUserDto, "userId", 1L);
    }

    @Test
    @DisplayName("성공: user 목록 반환 호출")
    void findUsers() throws Exception {
        //given
        int page = 0;
        int size = 1;
        PageRequest pageRequest = PageRequest.of(page, size);
        PageImpl<UserDto> userDtos = new PageImpl<>(List.of(givenUserDto), pageRequest, 1);

        given(userService.findUsers(anyInt(), anyInt())).willReturn(userDtos);

        //when
        ResultActions resultActions = mvc.perform(get("/api/v1/users")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("users-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지"),
                                parameterWithName("size").description("사이즈")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("age").type(JsonFieldType.NUMBER).description("회원 나이"),
                                fieldWithPath("hobby").type(JsonFieldType.STRING).description("회원 취미")
                        )));
    }

    @Test
    @DisplayName("성공: user 생성 호출")
    void createUser() throws Exception {
        //given
        String name = "name";
        int age = 20;
        String hobby = "hobby";
        UserCreateRequest request = new UserCreateRequest(name, age, hobby);

        given(userService.createUser(any(), anyInt(), any())).willReturn(1L);

        //when
        ResultActions resultActions = mvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("user-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("age").type(JsonFieldType.NUMBER).description("회원 나이"),
                                fieldWithPath("hobby").type(JsonFieldType.STRING).description("회원 취미")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.NUMBER).description("회원 ID")
                        )));
    }

    @Test
    @DisplayName("성공: user 단건 조회 호출")
    void findUser() throws Exception {
        //given
        Long userId = 1L;

        given(userService.findUser(any())).willReturn(givenUserDto);

        //when
        ResultActions resultActions = mvc.perform(get("/api/v1/users/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("userId").description("회원 ID")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("age").type(JsonFieldType.NUMBER).description("회원 나이"),
                                fieldWithPath("hobby").type(JsonFieldType.STRING).description("회원 취미")
                        )));
    }

    @Test
    @DisplayName("성공: user 수정 호출")
    void updateUser() throws Exception {
        //given
        Long userId = 1L;
        String name = "updateName";
        int age = 22;
        String hobby = "updateHobby";
        UserUpdateRequest request = new UserUpdateRequest(name, age, hobby);

        //when
        ResultActions resultActions = mvc.perform(patch("/api/v1/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("user-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("userId").description("회원 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("age").type(JsonFieldType.NUMBER).description("회원 나이"),
                                fieldWithPath("hobby").type(JsonFieldType.STRING).description("회원 취미")
                        )));
    }

    @Test
    @DisplayName("성공: user 삭제 호출")
    void deleteUser() throws Exception {
        //given
        Long userId = 1L;

        //when
        ResultActions resultActions = mvc.perform(delete("/api/v1/users/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("user-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("userId").description("회원 ID")
                        )));
    }
}