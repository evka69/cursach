package ru.flamexander.spring.security;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import ru.flamexander.spring.security.jwt.controllers.AuthController;
import ru.flamexander.spring.security.jwt.dtos.JwtRequest;
import ru.flamexander.spring.security.jwt.dtos.RegistrationUserDto;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.repositories.UserRepository;
import ru.flamexander.spring.security.jwt.service.AuthService;
import ru.flamexander.spring.security.jwt.service.UserService;
import ru.flamexander.spring.security.jwt.utils.JwtTokenUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = ru.flamexander.spring.security.jwt.SecurityJwtApplication.class)
class SecurityJwtApplicationTests {

	// Модульные тесты
	@Nested
	@ExtendWith(MockitoExtension.class)
	class UnitTests {
		@Mock
		private UserService userService;
		@Mock
		private JwtTokenUtils jwtTokenUtils;
		@Mock
		private AuthenticationManager authenticationManager;
		@InjectMocks
		private AuthController authController;



		// Интеграционные тесты
		@Autowired
		private MockMvc mockMvc;

		@Autowired
		private UserRepository userRepository;

		@Autowired
		private PasswordEncoder passwordEncoder;

		@Autowired
		private AuthService authService;

		@Test
		void contextLoads() {
			assertNotNull(mockMvc);
		}

		@Test
		void testRegistrationIntegration_Success() throws Exception {
			mockMvc.perform(post("/reg")
							.param("name", "testuser")
							.param("password", "ValidPass123!")
							.param("confirmPassword", "ValidPass123!")
							.param("email", "test@example.com")
							.contentType(MediaType.APPLICATION_FORM_URLENCODED))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/login"));
		}


		@Test
		void testRegistrationIntegration_ValidationErrors() throws Exception {
			mockMvc.perform(post("/reg")
							.param("name", "")
							.param("password", "short")
							.param("confirmPassword", "different")
							.param("email", "invalid")
							.contentType(MediaType.APPLICATION_FORM_URLENCODED))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/"))
					.andExpect(flash().attributeExists("errors"));
		}

		@Test
		void testRegistrationIntegration() throws Exception {
			mockMvc.perform(post("/reg")
							.param("name", "testuser")
							.param("password", "Test123!")
							.param("confirmPassword", "Test123!")
							.param("email", "test@example.com")
							.contentType(MediaType.APPLICATION_FORM_URLENCODED))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/login"));
		}







		@Test
		void testAuthService() {
			RegistrationUserDto dto = new RegistrationUserDto();
			dto.setUsername("service_test");
			dto.setPassword("Pass123!");
			dto.setConfirmPassword("Pass123!");
			dto.setEmail("service_test@example.com");

			assertDoesNotThrow(() -> authService.createNewUser(dto));
		}





		@Test
		void testPasswordEncoder() {
			PasswordEncoder encoder = mock(PasswordEncoder.class);
			when(encoder.encode("raw")).thenReturn("encoded");

			assertEquals("encoded", encoder.encode("raw"));
		}

		@Test
		void testUserEntity() {
			User user = new User();
			user.setUsername("entity_test");
			user.setEmail("test@test.com");

			assertEquals("entity_test", user.getUsername());
			assertEquals("test@test.com", user.getEmail());
		}

		@Test
		void testRegistrationDto() {
			RegistrationUserDto dto = new RegistrationUserDto();
			dto.setUsername("dto_test");
			dto.setPassword("pass");

			assertEquals("dto_test", dto.getUsername());
			assertEquals("pass", dto.getPassword());
		}

		@Test
		void testJwtRequest() {
			JwtRequest request = new JwtRequest();
			request.setUsername("jwt_user");
			request.setPassword("jwt_pass");

			assertEquals("jwt_user", request.getUsername());
			assertEquals("jwt_pass", request.getPassword());
		}

	}
}