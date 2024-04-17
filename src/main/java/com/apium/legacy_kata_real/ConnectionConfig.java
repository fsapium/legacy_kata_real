package com.apium.legacy_kata_real;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectionConfig {

  @Bean
  public Connection createConnection() throws SQLException {
    return DriverManager.getConnection("jdbc:mysql://localhost:3306/lift_pass", "root", "mysql");
  }

}
