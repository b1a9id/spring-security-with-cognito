package com.example.springsecuritywithcognito.repository;

import com.example.springsecuritywithcognito.entity.User;
import com.example.springsecuritywithcognito.enums.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryTest {
	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void beforeEach() {
		User user1 = new User();
		user1.setName("内立 良介");
		user1.setUsername("ruchitate");
		user1.setRole(Role.STAFF);
		entityManager.persist(user1);
	}

	@TestFactory
	Collection<DynamicTest> findByUsername() {
		return Arrays.asList(
				DynamicTest.dynamicTest("1st username test",
						() -> Assertions.assertThat(userRepository.findByUsername("ruchitate"))
								.hasValueSatisfying(u -> {
									assertEquals("内立 良介", u.getName());
									assertEquals("ruchitate", u.getUsername());
									assertNull(u.getLastSignInAt());
									assertEquals(Role.STAFF, u.getRole());})),
				DynamicTest.dynamicTest("2nd username test",
						() -> Assertions.assertThat(userRepository.findByUsername("test"))
								.isEmpty()));
	}
}
