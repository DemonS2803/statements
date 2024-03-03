package ru.light.statements;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.light.statements.dto.CreateStatementDTO;
import ru.light.statements.dto.UpdateStatementDTO;
import ru.light.statements.entities.Statement;
import ru.light.statements.entities.User;
import ru.light.statements.enums.StatementStatus;
import ru.light.statements.enums.UserRole;
import ru.light.statements.services.StatementService;
import ru.light.statements.services.UserService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class StatementsServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;
	@Autowired
	private StatementService statementService;
	@Autowired
	private UserService userService;

	// обязательно должен стоять статус DRAFT
	private final Long TESTED_STATEMENT_ID = 1L;
	private final Long TESTED_USER_ID = 2L;

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

		statementService.changeStatus(TESTED_STATEMENT_ID, StatementStatus.DRAFT);
        mockMvc.perform(get("/api/statement/get/1")).andExpect(status().isForbidden());
		statementService.changeStatus(TESTED_STATEMENT_ID, StatementStatus.SEND);
        mockMvc.perform(get("/api/statement/get/1")).andExpect(status().isOk());
		statementService.changeStatus(TESTED_STATEMENT_ID, StatementStatus.DRAFT);
    }
    
    @Test
    @WithMockUser(username = "user", password = "password", authorities = "USER")
    void userCheckGetStatements_Expect200() throws Exception {
        mockMvc.perform(get("/api/statement/get")).andExpect(status().isOk());
        mockMvc.perform(get("/api/statement/get/1")).andExpect(status().isOk());
		mockMvc.perform(get("/api/statement/get/2")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "oper", password = "password", authorities = "OPERATOR")
    void operatorCheckGetStatements_Expect200() throws Exception {
        mockMvc.perform(get("/api/statement/get")).andExpect(status().isOk());

		statementService.changeStatus(TESTED_STATEMENT_ID, StatementStatus.DRAFT);
        mockMvc.perform(get("/api/statement/get/1")).andExpect(status().isForbidden());
		statementService.changeStatus(TESTED_STATEMENT_ID, StatementStatus.SEND);
        mockMvc.perform(get("/api/statement/get/1")).andExpect(status().isOk());
		statementService.changeStatus(TESTED_STATEMENT_ID, StatementStatus.ACCEPTED);
        mockMvc.perform(get("/api/statement/get/1")).andExpect(status().isForbidden());
		statementService.changeStatus(TESTED_STATEMENT_ID, StatementStatus.REJECTED);
        mockMvc.perform(get("/api/statement/get/1")).andExpect(status().isForbidden());
		statementService.changeStatus(TESTED_STATEMENT_ID, StatementStatus.DRAFT);
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
		UpdateStatementDTO dto = new UpdateStatementDTO(TESTED_STATEMENT_ID, "test", "test", "admin", "88005553535");

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
		Statement statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.DRAFT);

        mockMvc.perform(put("/api/statement/send/1"))
			.andExpect(status().isForbidden());

		statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.DRAFT);

		statementService.changeStatus(statement.getId(), StatementStatus.DRAFT);

    }
    
    @Test
    @WithMockUser(username = "user", password = "password", authorities = "USER")
    void userCheckSendStatements_Expect200() throws Exception {
		Statement statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.DRAFT);

        mockMvc.perform(put("/api/statement/send/1"))
			.andExpect(status().isOk());

		statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.SEND);

		mockMvc.perform(put("/api/statement/send/2"))
			.andExpect(status().isForbidden());

		statement = statementService.getStatement(2L);
		assert statement.getStatus().equals(StatementStatus.DRAFT);

		statementService.changeStatus(TESTED_STATEMENT_ID, StatementStatus.DRAFT);
		statementService.changeStatus(2L, StatementStatus.DRAFT);
    }

    @Test
    @WithMockUser(username = "oper", password = "password", authorities = "OPERATOR")
    void operatorCheckSendStatements_Expect403() throws Exception {
        Statement statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.DRAFT);

        mockMvc.perform(put("/api/statement/send/1"))
			.andExpect(status().isForbidden());

		statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.DRAFT);
    }

	// TEST OPERATOR RIGHTS
	@Test
    @WithMockUser(username = "admin", password = "password", authorities = "ADMIN")
    void adminCheckOperatorRigth_Expect403() throws Exception {
		Statement statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.DRAFT);
		statementService.changeStatus(statement.getId(), StatementStatus.SEND);
		statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.SEND);

        mockMvc.perform(put("/api/statement/status/accept/1"))
			.andExpect(status().isForbidden());

		statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.SEND);
		
		mockMvc.perform(put("/api/statement/status/reject/1"))
			.andExpect(status().isForbidden());
		
		statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.SEND);

		statementService.changeStatus(statement.getId(), StatementStatus.DRAFT);

    }
    
    @Test
    @WithMockUser(username = "user", password = "password", authorities = "USER")
    void userCheckOperatorRigth_Expect403() throws Exception {
		Statement statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.DRAFT);
		statementService.changeStatus(statement.getId(), StatementStatus.SEND);
		statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.SEND);

        mockMvc.perform(put("/api/statement/status/accept/1"))
			.andExpect(status().isForbidden());

		statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.SEND);
		
		mockMvc.perform(put("/api/statement/status/reject/1"))
			.andExpect(status().isForbidden());
		
		statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.SEND);

		statementService.changeStatus(statement.getId(), StatementStatus.DRAFT);
    }

    @Test
    @WithMockUser(username = "oper", password = "password", authorities = "OPERATOR")
    void operatorCheckOperatorRigth_Expect200() throws Exception {
        Statement statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.DRAFT);
		statementService.changeStatus(statement.getId(), StatementStatus.SEND);
		statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.SEND);

        mockMvc.perform(put("/api/statement/status/accept/1"))
			.andExpect(status().isOk());

		statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.ACCEPTED);

		statementService.changeStatus(statement.getId(), StatementStatus.SEND);
		statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.SEND);
		
		mockMvc.perform(put("/api/statement/status/reject/1"))
			.andExpect(status().isOk());
		
		statement = statementService.getStatement(TESTED_STATEMENT_ID);
		assert statement.getStatus().equals(StatementStatus.REJECTED);

		statementService.changeStatus(statement.getId(), StatementStatus.DRAFT);
    }

	// TEST MAKE OPERATOR
	@Test
    @WithMockUser(username = "admin", password = "password", authorities = "ADMIN")
    void adminChangeUserRoles_Expect200() throws Exception {
		mockMvc.perform(put("/api/admin/make_operator/2"))
			.andExpect(status().isOk());
		
		User user = userService.getUserById(TESTED_USER_ID);
		assert user.getRole().equals(UserRole.OPERATOR);

		mockMvc.perform(put("/api/admin/make_usual/2"))
			.andExpect(status().isOk());
		
		user = userService.getUserById(TESTED_USER_ID);
		assert user.getRole().equals(UserRole.USER);
    }
    
    @Test
    @WithMockUser(username = "user", password = "password", authorities = "USER")
    void userChangeUserRoles_Expect403() throws Exception {
		mockMvc.perform(put("/api/admin/make_operator/2"))
			.andExpect(status().isForbidden());
		
		User user = userService.getUserById(TESTED_USER_ID);
		assert user.getRole().equals(UserRole.USER);

		mockMvc.perform(put("/api/admin/make_usual/2"))
			.andExpect(status().isForbidden());
		
		user = userService.getUserById(TESTED_USER_ID);
		assert user.getRole().equals(UserRole.USER);
    }

    @Test
    @WithMockUser(username = "oper", password = "password", authorities = "OPERATOR")
    void operatorChangeUserRoles_Expect403() throws Exception {
        mockMvc.perform(put("/api/admin/make_operator/2"))
			.andExpect(status().isForbidden());
		
		User user = userService.getUserById(TESTED_USER_ID);
		assert user.getRole().equals(UserRole.USER);

		mockMvc.perform(put("/api/admin/make_usual/2"))
			.andExpect(status().isForbidden());
		
		user = userService.getUserById(TESTED_USER_ID);
		assert user.getRole().equals(UserRole.USER);
    }

	// TEST GET USERS
	@Test
    @WithMockUser(username = "admin", password = "password", authorities = "ADMIN")
    void adminGetUsers_Expect200() throws Exception {
		mockMvc.perform(get("/api/admin/get_users"))
			.andExpect(status().isOk());

    }
    
    @Test
    @WithMockUser(username = "user", password = "password", authorities = "USER")
    void userGetUsers_Expect403() throws Exception {
		mockMvc.perform(get("/api/admin/get_users"))
			.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "oper", password = "password", authorities = "OPERATOR")
    void operatorGetUsers_Expect403() throws Exception {
        mockMvc.perform(get("/api/admin/get_users"))
			.andExpect(status().isForbidden());
    }
}
