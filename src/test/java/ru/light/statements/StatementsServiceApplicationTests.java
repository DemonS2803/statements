package ru.light.statements;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.light.statements.dto.CreateStatementDTO;
import ru.light.statements.dto.UpdateStatementDTO;
import ru.light.statements.entities.Statement;
import ru.light.statements.enums.StatementStatus;
import ru.light.statements.services.StatementService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class StatementsServiceApplicationTests {

	// @Autowired
    // private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;
	@Autowired
	private StatementService statementService;

    // TEST AUTH PAGE

    // @Test
    // @WithMockUser(username = "admin", password = "password", authorities = "ADMIN")
    // void contextLoads() throws Exception {
    //     mockMvc.perform(post("/api/auth/login")).andExpect(status().isOk());

    // }

    // TEST ADMIN PAGE

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = "ADMIN")
    void adminCheckAdminPage_Expect200() throws Exception {
        mockMvc.perform(get("/api/admin/")).andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(username = "user", password = "password", authorities = "USER")
    void userCheckAdminPage_Expect404() throws Exception {
        mockMvc.perform(get("/api/admin/")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "oper", password = "password", authorities = "OPERATOR")
    void operatorCheckAdminPage_Expect404() throws Exception {
        mockMvc.perform(get("/api/admin/")).andExpect(status().isForbidden());
    }

    // TEST GET STATEMENT

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = "ADMIN")
    void adminCheckGetStatements_Expect200() throws Exception {
		mockMvc.perform(get("/api/statement/get")).andExpect(status().isOk());

		statementService.changeStatus(1L, StatementStatus.DRAFT);
        mockMvc.perform(get("/api/statement/get/1")).andExpect(status().isForbidden());
		statementService.changeStatus(1L, StatementStatus.SEND);
        mockMvc.perform(get("/api/statement/get/1")).andExpect(status().isOk());
		statementService.changeStatus(1L, StatementStatus.DRAFT);
    }
    
    @Test
    @WithMockUser(username = "user", password = "password", authorities = "USER")
    void userCheckGetStatements_Expect200() throws Exception {
        mockMvc.perform(get("/api/statement/get")).andExpect(status().isOk());
        mockMvc.perform(get("/api/statement/get/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "oper", password = "password", authorities = "OPERATOR")
    void operatorCheckGetStatements_Expect200() throws Exception {
        mockMvc.perform(get("/api/statement/get")).andExpect(status().isOk());

		statementService.changeStatus(1L, StatementStatus.DRAFT);
        mockMvc.perform(get("/api/statement/get/1")).andExpect(status().isForbidden());
		statementService.changeStatus(1L, StatementStatus.SEND);
        mockMvc.perform(get("/api/statement/get/1")).andExpect(status().isOk());
		statementService.changeStatus(1L, StatementStatus.ACCEPTED);
        mockMvc.perform(get("/api/statement/get/1")).andExpect(status().isForbidden());
		statementService.changeStatus(1L, StatementStatus.REJECTED);
        mockMvc.perform(get("/api/statement/get/1")).andExpect(status().isForbidden());
		statementService.changeStatus(1L, StatementStatus.DRAFT);
    }

    // TEST CREATE AND EDIT STATEMENT

	@Test
    @WithMockUser(username = "admin", password = "password", authorities = "ADMIN")
    void adminCheckEditStatement_Expect403() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		CreateStatementDTO dto = new CreateStatementDTO("test", "test", "admin", "88005553535");
        mockMvc.perform(post("/api/statement/edit")
			.content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());

		// Statement test = new Statement()
		mockMvc.perform(post("/api/statement/edit")
			.content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(username = "user", password = "password", authorities = "USER")
    void userCheckEditStatements_Expect200() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		UpdateStatementDTO dto = new UpdateStatementDTO(1L, "test", "test", "admin", "88005553535");

		mockMvc.perform(put("/api/statement/edit")
			.content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "oper", password = "password", authorities = "OPERATOR")
    void operatorCheckEditStatements_Expect403() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
		CreateStatementDTO dto = new CreateStatementDTO("test", "test", "admin", "88005553535");
        mockMvc.perform(post("/api/statement/edit")
			.content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());

		// Statement test = new Statement()
		mockMvc.perform(post("/api/statement/edit")
			.content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
    }

	// TEST SEND DRAFT
	@Test
    @WithMockUser(username = "admin", password = "password", authorities = "ADMIN")
    void adminCheckSendStatement_Expect403() throws Exception {
		Statement statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.DRAFT);

        mockMvc.perform(put("/api/statement/send/1"))
			.andExpect(status().isForbidden());

		statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.DRAFT);

		statementService.changeStatus(statement.getId(), StatementStatus.DRAFT);

    }
    
    @Test
    @WithMockUser(username = "user", password = "password", authorities = "USER")
    void userCheckSendStatements_Expect200() throws Exception {
		Statement statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.DRAFT);

        mockMvc.perform(put("/api/statement/send/1"))
			.andExpect(status().isOk());

		statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.SEND);

		statementService.changeStatus(statement.getId(), StatementStatus.DRAFT);
    }

    @Test
    @WithMockUser(username = "oper", password = "password", authorities = "OPERATOR")
    void operatorCheckSendStatements_Expect403() throws Exception {
        Statement statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.DRAFT);

        mockMvc.perform(put("/api/statement/send/1"))
			.andExpect(status().isForbidden());

		statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.DRAFT);
    }

	// TEST OPERATOR RIGHTS
	@Test
    @WithMockUser(username = "admin", password = "password", authorities = "ADMIN")
    void adminCheckOperatorRigth_Expect403() throws Exception {
		Statement statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.DRAFT);
		statementService.changeStatus(statement.getId(), StatementStatus.SEND);
		statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.SEND);

        mockMvc.perform(put("/api/statement/status/accept/1"))
			.andExpect(status().isForbidden());

		statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.SEND);
		
		mockMvc.perform(put("/api/statement/status/reject/1"))
			.andExpect(status().isForbidden());
		
		statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.SEND);

		statementService.changeStatus(statement.getId(), StatementStatus.DRAFT);

    }
    
    @Test
    @WithMockUser(username = "user", password = "password", authorities = "USER")
    void userCheckOperatorRigth_Expect403() throws Exception {
		Statement statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.DRAFT);
		statementService.changeStatus(statement.getId(), StatementStatus.SEND);
		statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.SEND);

        mockMvc.perform(put("/api/statement/status/accept/1"))
			.andExpect(status().isForbidden());

		statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.SEND);
		
		mockMvc.perform(put("/api/statement/status/reject/1"))
			.andExpect(status().isForbidden());
		
		statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.SEND);

		statementService.changeStatus(statement.getId(), StatementStatus.DRAFT);
    }

    @Test
    @WithMockUser(username = "oper", password = "password", authorities = "OPERATOR")
    void operatorCheckOperatorRigth_Expect200() throws Exception {
        Statement statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.DRAFT);
		statementService.changeStatus(statement.getId(), StatementStatus.SEND);
		statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.SEND);

        mockMvc.perform(put("/api/statement/status/accept/1"))
			.andExpect(status().isOk());

		statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.ACCEPTED);

		statementService.changeStatus(statement.getId(), StatementStatus.SEND);
		statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.SEND);
		
		mockMvc.perform(put("/api/statement/status/reject/1"))
			.andExpect(status().isOk());
		
		statement = statementService.getStatement(1L);
		assert statement.getStatus().equals(StatementStatus.REJECTED);

		statementService.changeStatus(statement.getId(), StatementStatus.DRAFT);
    }

	// TEST MAKE OPERATOR
	
}
