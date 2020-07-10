package com.b1a9idps.springsecuritywithcognito.service;

import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.b1a9idps.springsecuritywithcognito.entity.User;
import com.b1a9idps.springsecuritywithcognito.enums.Role;
import com.b1a9idps.springsecuritywithcognito.repository.UserRepository;
import com.b1a9idps.springsecuritywithcognito.service.dto.request.ChangePasswordReqeustDto;
import com.b1a9idps.springsecuritywithcognito.service.dto.response.AuthenticatedResponse;
import com.b1a9idps.springsecuritywithcognito.service.dto.response.UserResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTest {
	@Autowired
	private UserService userService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private CognitoService cognitoService;

	@DisplayName("UserService#getの正常系")
	@Test
	void getSuccess() {
		User user = createUser();

		given(userRepository.findById(anyInt())).willReturn(Optional.of(user));
		given(cognitoService.adminGetUser(anyString())).willReturn(Optional.of(createAdminGetUserResult()));

		Assertions.assertThat(userService.get(1))
				.extracting(
						UserResponse::getName,
						UserResponse::getUsername,
						UserResponse::getCreatedAt,
						UserResponse::getLastSignInAt)
				.containsExactly(
						"内立 良介",
						"ruchitate",
						LocalDateTime.of(2000, 1, 1,10, 0),
						null);
	}

	@DisplayName("@PreAuthorize(\"isAnonymous()\")が効いていることの検証。STAFFユーザでメソッド呼ぶ。")
	@Test
	@WithMockUser(username = "ruchitate", roles = "STAFF")
	void changeTmpPasswordIsNotAnonymous() {
		ChangePasswordReqeustDto request = new ChangePasswordReqeustDto();
		request.setUsername("ruchitate");
		request.setPassword("abcd1234");

		Assertions.assertThatThrownBy(() -> userService.changeTmpPassword(request))
				.isInstanceOf(AccessDeniedException.class);
	}

	@DisplayName("@PreAuthorize(\"isAnonymous()\")が効いていることの検証。匿名ユーザでメソッド呼ぶ。")
	@Test
	@WithAnonymousUser
	void changeTmpPasswordUsAnonymous() {
		ChangePasswordReqeustDto request = new ChangePasswordReqeustDto();
		request.setUsername("ruchitate");
		request.setPassword("abcd1234");
		request.setSession("session");

		given(userRepository.findByUsername(anyString())).willReturn(Optional.of(createUser()));
		given(cognitoService.adminRespondToAuthChallenge(anyString(), anyString(), anyString()))
				.willReturn(Optional.of(createAdminRespondToAuthChallengeResult()));

		Assertions.assertThat(userService.changeTmpPassword(request))
				.extracting(AuthenticatedResponse::getAccessToken)
				.isEqualTo("access-token");
	}

	private User createUser() {
		User user = new User();
		user.setId(1);
		user.setName("内立 良介");
		user.setUsername("ruchitate");
		user.setRole(Role.STAFF);
		user.setCreatedAt(LocalDateTime.of(2000, 1, 1, 10, 0));
		user.setUpdatedAt(LocalDateTime.of(2000, 1, 1, 10, 0));
		return user;
	}

	private AdminGetUserResult createAdminGetUserResult() {
		AdminGetUserResult result = new AdminGetUserResult();
		result.withUsername("ruchitate");
		return result;
	}

	private AdminRespondToAuthChallengeResult createAdminRespondToAuthChallengeResult() {
		AdminRespondToAuthChallengeResult result = new AdminRespondToAuthChallengeResult();
		AuthenticationResultType type = new AuthenticationResultType();
		type.withAccessToken("access-token");
		result.withAuthenticationResult(type);
		return result;
	}
}
