package com.apium.legacy_kata_real;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LiftPassController {

  @Autowired
  private JdbcTemplate jdbcTemplate;

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
      @RequestParam(value = "date", required = false) String dateString) {

    String sql = "SELECT cost FROM base_price WHERE type = ?";

    try {
      List<Integer> resultList = jdbcTemplate.queryForList(sql, Integer.class, liftPassType);
      for (Integer baseCost : resultList) {
        int reduction;
        boolean isHoliday = false;

        if (age != null && age < 6) {
          return "{ \"cost\": 0}";
        } else {
          reduction = 0;

          if (!liftPassType.equals("night")) {
            DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = isoFormat.parse(dateString);
            sql = "SELECT * FROM holidays WHERE holiday = ?";
            Map<String, Object> holiday = jdbcTemplate.queryForMap(sql, date);

            Date holidayDate = (Date) holiday.get("holiday");
            if (date != null && holidayDate != null ) {
              isHoliday = true;
            }

            if (dateString != null) {
              Calendar calendar = Calendar.getInstance();
              calendar.setTime(isoFormat.parse(dateString));
              if (!isHoliday && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                reduction = 35;
              }
            }

            // TODO apply reduction for others
            if (age != null && age < 15) {
              return "{ \"cost\": " + (int) Math.ceil(baseCost * .7) + "}";
            } else {
              if (age == null) {
                double cost = baseCost * (1 - reduction / 100.0);
                return "{ \"cost\": " + (int) Math.ceil(cost) + "}";
              } else {
                if (age > 64) {
                  double cost = baseCost * .75 * (1 - reduction / 100.0);
                  return "{ \"cost\": " + (int) Math.ceil(cost) + "}";
                } else {
                  double cost = baseCost * (1 - reduction / 100.0);
                  return "{ \"cost\": " + (int) Math.ceil(cost) + "}";
                }
              }
            }
          } else {
            if (age != null && age >= 6) {
              if (age > 64) {
                return "{ \"cost\": " + (int) Math.ceil(baseCost * .4) + "}";
              } else {
                return "{ \"cost\": " + baseCost + "}";
              }
            } else {
              return "{ \"cost\": 0}";
            }
          }
        }
      }
    } catch (DataAccessException | ParseException e) {
      throw new RuntimeException(e);
    }
    return "";
  }

}
