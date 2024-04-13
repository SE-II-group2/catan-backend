package com.group2.catanbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.catanbackend.dto.JoinRequestDto;
import com.group2.catanbackend.model.Player;
import com.group2.catanbackend.service.GameService;
import com.group2.catanbackend.service.TokenService;
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

    @Autowired
    private TokenService tokenService;


    @Test
    public void testGamesFoundOnceCreated() throws Exception{
        String createdGameID = gameService.createGame();
        mockMvc.perform(
                MockMvcRequestBuilders.get("/catan/game/list"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(createdGameID)));
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
        JoinRequestDto requestDto = new JoinRequestDto();
        requestDto.setPlayerName("player");

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
        String gameID = gameService.createGame();
        JoinRequestDto requestDto = new JoinRequestDto("player", gameID);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/catan/game/connect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(requestDto))
        )
                .andExpect(status().isOk());

        Assertions.assertEquals(1, gameService.getLobbies().size());
        Assertions.assertEquals(1, gameService.getLobbies().get(0).getPlayerCount());
    }

    @Test
    public void testCanStartGameWhenAdmin() throws Exception {
        String gameID = gameService.createGame();
        JoinRequestDto requestDto = new JoinRequestDto("player1", gameID);
        String token = tokenService.generateToken();
        Player p = gameService.joinGame(token, requestDto);
        tokenService.pushToken(token, p);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/catan/game/start")
                        .header(HttpHeaders.AUTHORIZATION, token))
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
        String gameID = gameService.createGame();

        JoinRequestDto requestDto1 = new JoinRequestDto("player1", gameID);
        String token1 = tokenService.generateToken();
        Player p1 = gameService.joinGame(token1, requestDto1);
        tokenService.pushToken(token1, p1);

        JoinRequestDto requestDto2 = new JoinRequestDto("player2", gameID);
        String token2 = tokenService.generateToken();
        Player p2 = gameService.joinGame(token2, requestDto2);
        tokenService.pushToken(token2, p2);


        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/catan/game/start")
                                .header(HttpHeaders.AUTHORIZATION, token2))
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
