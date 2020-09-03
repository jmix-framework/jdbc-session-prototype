package org.example.jdbcsession;

import io.jmix.core.DataManager;
import io.jmix.core.Metadata;
import io.jmix.core.security.Authenticator;
import io.jmix.core.security.UserRepository;
import io.jmix.core.security.impl.CoreUser;
import io.jmix.core.security.impl.InMemoryUserRepository;
import io.jmix.security.role.assignment.InMemoryRoleAssignmentProvider;
import io.jmix.securitydata.entity.RoleAssignmentEntity;
import io.jmix.securitydata.entity.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@SpringBootApplication
@EnableJdbcHttpSession
@EnableWebMvc
@ComponentScan("org.example.jdbcsession")
public class SampleApplication {

	@Autowired
	private DataManager dataManager;

	@Autowired
	private Authenticator authenticator;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private Metadata metadata;

	@Autowired
	protected DataSource dataSource;

	@Autowired
	protected AuthenticationKeyGenerator keyGenerator;

	@Autowired
	private InMemoryRoleAssignmentProvider inMemoryRoleAssignmentProvider;

	public static void main(String[] args) {
		SpringApplication.run(SampleApplication.class, args);
	}

	@Bean
	@Primary
	@ConfigurationProperties(prefix="main.datasource")
	DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@EventListener(ApplicationStartedEvent.class)
	private void onStartup() {
		authenticator.withSystem(() -> {
			initDatabaseRoles();

			return null;
		});
		initUsers();
	}

	private void initDatabaseRoles() {
		RoleEntity dbRole1 = metadata.create(RoleEntity.class);
		dbRole1.setName("DB role 1");
		dbRole1.setCode("dbRole1");

		RoleAssignmentEntity dbRole1AdminAssignment = metadata.create(RoleAssignmentEntity.class);
		dbRole1AdminAssignment.setUserKey("admin");
		dbRole1AdminAssignment.setRoleCode(dbRole1.getCode());

		dataManager.save(dbRole1, dbRole1AdminAssignment);
	}

	@Primary
	@Bean(name = "rest_tokenStore")
	protected TokenStore tokenStore() {
		JdbcTokenStore tokenStore = new JdbcTokenStore(dataSource);
		tokenStore.setAuthenticationKeyGenerator(keyGenerator);
		return tokenStore;
	}

	@Primary
	@Bean
	protected PersistentTokenRepository rememberMeRepository() {
		JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
		jdbcTokenRepository.setDataSource(dataSource());
		return jdbcTokenRepository;
	}

	private void initUsers() {
		if (userRepository instanceof InMemoryUserRepository) {
			CoreUser admin = new CoreUser("admin", "{noop}admin", "Admin");
			CoreUser user1 = new CoreUser("user1", "{noop}1", "User 1");
			((InMemoryUserRepository) userRepository).createUser(admin);
			((InMemoryUserRepository) userRepository).createUser(user1);
		}
	}
}
