package com.apium.legacy_kata_real;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LiftPassController {

  @PutMapping("/prices")
  public void updatePrice(@RequestParam("cost") int liftPassCost, @RequestParam("type") String liftPassType) throws SQLException {
    final Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lift_pass", "root", "mysql");
    try (PreparedStatement stmt = connection.prepareStatement( //
        "INSERT INTO base_price (type, cost) VALUES (?, ?) ON DUPLICATE KEY UPDATE cost = ?")) {
      stmt.setString(1, liftPassType);
      stmt.setInt(2, liftPassCost);
      stmt.setInt(3, liftPassCost);
      stmt.execute();
    }

    connection.close();
  }

  @GetMapping(value = "/prices", produces = MediaType.APPLICATION_JSON_VALUE)
  public String getPrice(@RequestParam("type") String liftPassType,
      @RequestParam(value = "age", required = false) Integer age,
      @RequestParam(value = "date", required = false) String dateString) throws SQLException, ParseException {

    final Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lift_pass", "root", "mysql");


    try (PreparedStatement costStmt = connection.prepareStatement( //
        "SELECT cost FROM base_price WHERE type = ?")) {
      costStmt.setString(1, liftPassType);
      try (ResultSet result = costStmt.executeQuery()) {
        result.next();

        int reduction;
        boolean isHoliday = false;

        if (age != null && age < 6) {
          connection.close();
          return "{ \"cost\": 0}";
        } else {
          reduction = 0;

          if (!liftPassType.equals("night")) {
            DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");

            try (PreparedStatement holidayStmt = connection.prepareStatement( //
                "SELECT * FROM holidays")) {
              try (ResultSet holidays = holidayStmt.executeQuery()) {

                while (holidays.next()) {
                  Date holiday = holidays.getDate("holiday");
                  if (dateString != null) {
                    Date d = isoFormat.parse(dateString);
                    if (d.getYear() == holiday.getYear() && //
                        d.getMonth() == holiday.getMonth() && //
                        d.getDate() == holiday.getDate()) {
                      isHoliday = true;
                    }
                  }
                }

              }
            }

            if (dateString != null) {
              Calendar calendar = Calendar.getInstance();
              calendar.setTime(isoFormat.parse(dateString));
              if (!isHoliday && calendar.get(Calendar.DAY_OF_WEEK) == 2) {
                reduction = 35;
              }
            }

            // TODO apply reduction for others
            if (age != null && age < 15) {
              int value = result.getInt("cost");
              connection.close();
              return "{ \"cost\": " + (int) Math.ceil(value * .7) + "}";
            } else {
              if (age == null) {
                int value = result.getInt("cost");
                connection.close();
                double cost = value * (1 - reduction / 100.0);
                return "{ \"cost\": " + (int) Math.ceil(cost) + "}";
              } else {
                if (age > 64) {
                  int value = result.getInt("cost");
                  connection.close();
                  double cost = value * .75 * (1 - reduction / 100.0);
                  return "{ \"cost\": " + (int) Math.ceil(cost) + "}";
                } else {
                  int value = result.getInt("cost");
                  connection.close();
                  double cost = value * (1 - reduction / 100.0);
                  return "{ \"cost\": " + (int) Math.ceil(cost) + "}";
                }
              }
            }
          } else {
            if (age != null && age >= 6) {
              if (age > 64) {
                int value = result.getInt("cost");
                connection.close();
                return "{ \"cost\": " + (int) Math.ceil(value * .4) + "}";
              } else {
                int value = result.getInt("cost");
                connection.close();
                return "{ \"cost\": " + value + "}";
              }
            } else {
              connection.close();
              return "{ \"cost\": 0}";
            }
          }
        }
      }
    }
  }

}
