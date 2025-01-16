package es.ufv.dis.invent2025.mab;


import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class PlayerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Player samplePlayer;

    @BeforeEach
    public void setUp() {
        samplePlayer = new Player();
        samplePlayer.setName("Cristiano Ronaldo");
        samplePlayer.setNumber(7);
        samplePlayer.setValue(100.0);
        samplePlayer.setPosition("Forward");
        samplePlayer.setMatchesPlayedThisSeason(30);
        samplePlayer.setPreviousTeams(List.of("Sporting CP", "Manchester United", "Real Madrid", "Juventus"));
    }

    @Test
    public void testGetAllPlayers() throws Exception {
        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testAddPlayer() throws Exception {
        String playerJson = objectMapper.writeValueAsString(samplePlayer);

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Player added successfully."));
    }

    @Test
    public void testUpdatePlayer() throws Exception {
        // Add a player to update
        String playerJson = objectMapper.writeValueAsString(samplePlayer);
        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk());

        // Retrieve player ID
        String response = mockMvc.perform(get("/api/players"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Player[] players = objectMapper.readValue(response, Player[].class);
        String playerId = players[0].getId();

        // Update player
        samplePlayer.setName("Updated Player");
        playerJson = objectMapper.writeValueAsString(samplePlayer);

        mockMvc.perform(put("/api/players/" + playerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Player updated successfully."));
    }

    @Test
    public void testDeletePlayer() throws Exception {
        // Add a player to delete
        String playerJson = objectMapper.writeValueAsString(samplePlayer);
        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk());

        // Retrieve player ID
        String response = mockMvc.perform(get("/api/players"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Player[] players = objectMapper.readValue(response, Player[].class);
        String playerId = players[0].getId();

        // Delete player
        mockMvc.perform(delete("/api/players/" + playerId))
                .andExpect(status().isOk())
                .andExpect(content().string("Player deleted successfully."));
    }

    @Test
    public void testGetTeamValue() throws Exception {
        // Create first player
        Player player1 = new Player();
        player1.setName("Cristiano Ronaldo");
        player1.setNumber(7);
        player1.setValue(300.0);
        player1.setPosition("Forward");
        player1.setMatchesPlayedThisSeason(30);
        player1.setPreviousTeams(List.of("Sporting CP", "Manchester United", "Real Madrid", "Juventus"));

        // Create second player
        Player player2 = new Player();
        player2.setName("Lionel Messi");
        player2.setNumber(10);
        player2.setValue(162.0);
        player2.setPosition("Forward");
        player2.setMatchesPlayedThisSeason(25);
        player2.setPreviousTeams(List.of("Barcelona", "PSG"));

        // Add both players
        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player2)))
                .andExpect(status().isOk());

        // Get team value
        mockMvc.perform(get("/api/players/team-value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamValue").value(462.0));
    }


    @Test
    public void testGeneratePdf() throws Exception {
        // Add a player
        String playerJson = objectMapper.writeValueAsString(samplePlayer);
        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk());

        // Generate PDF
        mockMvc.perform(post("/api/players/generate-pdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Cristiano Ronaldo\""))
                .andExpect(status().isOk())
                .andExpect(content().string("PDF generated successfully for Cristiano Ronaldo"));
    }

    @Test
    public void testExportCsv() throws Exception {
        mockMvc.perform(get("/api/players/export-csv"))
                .andExpect(status().isOk())
                .andExpect(content().string("CSV exported successfully."));
    }
}
