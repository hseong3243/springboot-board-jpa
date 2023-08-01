package com.programmers.board.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalControllerAdviceTest.ExceptionController.class)
@Import(GlobalControllerAdviceTest.ExceptionController.class)
@AutoConfigureRestDocs
class GlobalControllerAdviceTest {
    @Autowired
    MockMvc mvc;

    @RestController
    static class ExceptionController {
        @GetMapping("/no-such")
        public void noSuch() {
            throw new NoSuchElementException("no such element ex");
        }

        @GetMapping("/ex")
        public void ex() {
            throw new RuntimeException("unexpected ex");
        }
    }

    @Test
    @DisplayName("성공: NoSuchElementException 예외 처리")
    void noSuchElementExHandle() throws Exception {
        //given
        //when
        ResultActions resultActions = mvc.perform(get("/no-such"));

        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("ex-no-such-element",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                        )));
    }

    @Test
    @DisplayName("성공: Exception 예외 처리")
    void exHandle() throws Exception {
        //given
        //when
        ResultActions resultActions = mvc.perform(get("/ex"));

        //then
        resultActions.andExpect(status().isInternalServerError())
                .andDo(print())
                .andDo(document("ex",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                        )));
    }
}