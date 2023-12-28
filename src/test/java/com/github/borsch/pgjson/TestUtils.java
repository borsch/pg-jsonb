package com.github.borsch.pgjson;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.Base58;

import java.util.Properties;

public class TestUtils {

	private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13.4")
		.withUsername("test")
		.withPassword("test")
		.withDatabaseName("pgjsonb")
		.withNetworkAliases("postgres-" + Base58.randomString(6))
		.withInitScript("seed.sql");

	static {
		POSTGRES.start();
	}

	private static Session session = null;


	public synchronized static Session getSession() {
		if (session == null) {
			Properties properties = new Properties();
			properties.put("hibernate.dialect", "com.github.borsch.pgjson.hibernate.dialect.PostgresJsonSQL94Dialect");
			properties.put("hibernate.hbm2ddl.auto", "update");
			properties.put("hibernate.show_sql", "true");
			properties.put("hibernate.connection.driver_class", "org.postgresql.Driver");
			properties.put("hibernate.connection.url", POSTGRES.getJdbcUrl());
			properties.put("hibernate.connection.username", POSTGRES.getUsername());
			properties.put("hibernate.connection.password", POSTGRES.getPassword());

			SessionFactory sessionFactory = new Configuration()
					.addProperties(properties)
					.addAnnotatedClass(User.class)
					.buildSessionFactory(
							new StandardServiceRegistryBuilder()
									.applySettings(properties)
									.build()
					);
			session = sessionFactory.openSession();
		}
		return session;
	}
}
