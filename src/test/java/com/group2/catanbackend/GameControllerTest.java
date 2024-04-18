package com.group2.catanbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.catanbackend.dto.CreateRequestDto;
import com.group2.catanbackend.dto.JoinRequestDto;
import com.group2.catanbackend.dto.JoinResponseDto;
import com.group2.catanbackend.service.GameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = CatanBackendApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameService gameService;


    @Test
    public void testGamesFoundOnceCreated() throws Exception{
        JoinResponseDto response = gameService.createAndJoin(new CreateRequestDto("Player"));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/catan/game/list"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(response.getGameID())));
    }

    @Test
    public void testCannotJoinNotExistingGame() throws Exception {
        JoinRequestDto requestDto = new JoinRequestDto("a", "asdf");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/catan/game/connect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(requestDto))
                )
                .andExpect(status().is(404)

        );
    }

    @Test
    public void testCanCreateGame() throws Exception{
        CreateRequestDto requestDto = new CreateRequestDto("Player");

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/catan/game/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(requestDto))
        ).andExpect(status().isOk());

        Assertions.assertEquals(1, gameService.getLobbies().get(0).getPlayerCount());
    }

    @Test
    public void testCanJoinExistingGame() throws Exception {
        String gameID = gameService.createAndJoin(new CreateRequestDto("Player1")).getGameID();
        JoinRequestDto requestDto = new JoinRequestDto("Player2", gameID);
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/catan/game/connect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(requestDto))
        )
                .andExpect(status().isOk());

        Assertions.assertEquals(1, gameService.getLobbies().size());
        Assertions.assertEquals(2, gameService.getLobbies().get(0).getPlayerCount());
    }

    @Test
    public void testCanStartGameWhenAdmin() throws Exception {
        JoinResponseDto joinResponse = gameService.createAndJoin(new CreateRequestDto("Player1"));

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/catan/game/start")
                        .header(HttpHeaders.AUTHORIZATION, joinResponse.getToken()))
                .andExpect(status().isOk());
        Assertions.assertEquals(0, gameService.getRegisteredGames().size());
        Assertions.assertEquals(1, gameService.getRunningGames().size());
    }

    @Test
    public void testCannotStartGameWhenNoTokenPresent() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/catan/game/start"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void testCannotStartGameWhenNotAdmin() throws Exception{
        JoinResponseDto player1 = gameService.createAndJoin(new CreateRequestDto("Player1"));
        JoinResponseDto player2 = gameService.joinGame(new JoinRequestDto("Player2", player1.getGameID()));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/catan/game/start")
                                .header(HttpHeaders.AUTHORIZATION, player2.getToken()))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
        Assertions.assertEquals(1, gameService.getRegisteredGames().size());
        Assertions.assertEquals(0, gameService.getRunningGames().size());
    }


    private String toJson(final Object obj){
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e){
            throw new RuntimeException();
        }
    }
}
