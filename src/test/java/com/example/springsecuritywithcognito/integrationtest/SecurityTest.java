package com.example.springsecuritywithcognito.integrationtest;

import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.example.springsecuritywithcognito.TestHelper;
import com.example.springsecuritywithcognito.controller.dto.request.LoginRequest;
import com.example.springsecuritywithcognito.enums.Role;
import com.example.springsecuritywithcognito.repository.UserRepository;
import com.example.springsecuritywithcognito.service.CognitoService;
import com.example.springsecuritywithcognito.service.dto.response.AuthenticatedResponse;
import com.example.springsecuritywithcognito.service.dto.response.FirstLoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.ADMIN_NO_SRP_AUTH;
import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.NEW_PASSWORD_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("セキュリティ周りのインテグレーションテスト")
@ExtendWith(SpringExtension.class)
class SecurityTest {
	private MockMvc mockMvc;

	@DisplayName("パスのアクセス制御ができているかの検証")
	@Nested
	@SpringBootTest
	class UrlMatchers {
		@Autowired
		private WebApplicationContext context;

		@BeforeEach
		void beforeEach() {
			mockMvc = MockMvcBuilders
					.webAppContextSetup(context)
					.apply(SecurityMockMvcConfigurers.springSecurity())
					.build();
		}

		@DisplayName("ADMIN、匿名ユーザでリクエスト")
		@Test
		void forbidden() {
			assertAll(
					() -> mockMvc.perform(get("/users/{id}", 1)
							.with(user("ruchitate").roles(Role.ADMIN.name())))
							.andExpect(status().isForbidden()),
					() -> mockMvc.perform(get("/users/{id}", 1)
							.with(anonymous()))
							.andExpect(status().isForbidden()));
		}

		@DisplayName("@WithMockUserを使って、ADMINユーザでリクエスト")
		@Test
		@WithMockUser(roles = "ADMIN")
		void forbiddenWithAnnotationForAdmin() throws Exception {
			mockMvc.perform(get("/users/{id}", 1))
					.andExpect(status().isForbidden());
		}

		@DisplayName("@WithMockUserを使って、匿名ユーザでリクエスト")
		@Test
		@WithAnonymousUser
		void forbiddenAnnotationForAnonymous() throws Exception {
			mockMvc.perform(get("/users/{id}", 1))
					.andExpect(status().isForbidden());
		}
	}

	@DisplayName("ログインの検証")
	@Nested
	@SpringBootTest
	@Sql(scripts = {"classpath:/db/migration/drop-table.sql", "classpath:/db/migration/initial.sql"})
	class Authentication {
		@Autowired
		private WebApplicationContext context;

		@Autowired
		private UserRepository userRepository;

		@MockBean
		private CognitoService cognitoService;

		private ObjectMapper mapper = new ObjectMapper();

		@BeforeEach
		void beforeEach() {
			mockMvc = MockMvcBuilders
					.webAppContextSetup(context)
					.apply(SecurityMockMvcConfigurers.springSecurity())
					.build();
		}

		@DisplayName("ログイン成功")
		@Test
		void success() throws Exception {
			AuthenticationResultType type = new AuthenticationResultType();
			type.withAccessToken("access-token")
					.withRefreshToken("refresh-token");
			AdminInitiateAuthResult adminInitiateAuthResult = new AdminInitiateAuthResult();
			adminInitiateAuthResult.withChallengeName(ADMIN_NO_SRP_AUTH)
					.withAuthenticationResult(type);

			given(cognitoService.adminInitiateAuth(anyString(), anyString()))
					.willReturn(Optional.of(adminInitiateAuthResult));

			MvcResult result = mockMvc.perform(post("/login")
					.content(mapper.writeValueAsString(createLoginRequest("ruchitate", "abcd1234")))
					.headers(TestHelper.createHttpHeader("access-token")))
					.andExpect(status().isOk())
					.andReturn();

			AuthenticatedResponse response = new AuthenticatedResponse("ruchitate", "access-token");
			Assertions.assertThat(result.getResponse().getContentAsString())
					.isEqualTo(mapper.writeValueAsString(response));
			Assertions.assertThat(userRepository.findById(1))
					.hasValueSatisfying(u -> assertNotNull(u.getLastSignInAt()));
		}

		@DisplayName("usernameがnullのとき")
		@Test
		void usernameEmpty() throws Exception {
			mockMvc.perform(post("/login")
					.content(mapper.writeValueAsString(createLoginRequest(null, "abcd1234")))
					.headers(TestHelper.createHttpHeader("access-token")))
					.andExpect(status().isBadRequest());
		}

		@DisplayName("passwordがnullのとき")
		@Test
		void passwordEmpty() throws Exception {
			mockMvc.perform(post("/login")
					.content(mapper.writeValueAsString(createLoginRequest("ruchitate", null)))
					.headers(TestHelper.createHttpHeader("access-token")))
					.andExpect(status().isBadRequest());
		}

		@DisplayName("初回ログインのとき")
		@Test
		void firstLogin() throws Exception {
			AdminInitiateAuthResult adminInitiateAuthResult = new AdminInitiateAuthResult();
			adminInitiateAuthResult.withChallengeName(NEW_PASSWORD_REQUIRED).withSession("session-test");

			given(cognitoService.adminInitiateAuth(anyString(), anyString()))
					.willReturn(Optional.of(adminInitiateAuthResult));

			MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
					.content(mapper.writeValueAsString(createLoginRequest("ruchitate", "1234abcd")))
					.headers(TestHelper.createHttpHeader("access-token")))
					.andExpect(status().isUnauthorized())
					.andReturn();

			FirstLoginResponse response = new FirstLoginResponse("session-test");

			Assertions.assertThat(result.getResponse().getContentAsString())
					.isEqualTo(mapper.writeValueAsString(response));
		}

		private LoginRequest createLoginRequest(String username, String password) {
			LoginRequest request = new LoginRequest();
			request.setUsername(username);
			request.setPassword(password);
			return request;
		}
	}
}
