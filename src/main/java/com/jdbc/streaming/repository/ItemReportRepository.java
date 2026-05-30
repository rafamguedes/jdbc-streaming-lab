package com.jdbc.streaming.repository;

import com.jdbc.streaming.dto.ItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.function.Consumer;

@Repository
@RequiredArgsConstructor
public class ItemReportRepository {

  private static final int FETCH_SIZE = 1000;

  private final JdbcTemplate jdbcTemplate;

  public void streamItems(
      Consumer<ItemDTO> consumer, LocalDateTime startDate, LocalDateTime endDate) {

    String sql =
        """
            SELECT
                i.name AS item_name,
                i.description AS item_description,
                i.quantity AS item_quantity,
                i.buy_price AS item_buy_price,
                i.sell_price AS item_sell_price,
                i.created_at AS item_created_at,
                i.updated_at AS item_updated_at,
                s.name AS supplier_name,
                s.description AS supplier_description,
                s.cnpj AS supplier_cnpj,
                s.email AS supplier_email,
                s.phone AS supplier_phone
            FROM items i
            INNER JOIN supplier s ON s.id = i.supplier_id
            WHERE i.created_at >= ? AND i.created_at <= ?
            ORDER BY i.name
            """;

    jdbcTemplate.query(
        connection -> {
          PreparedStatement ps =
              connection.prepareStatement(
                  sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

          ps.setTimestamp(1, Timestamp.valueOf(startDate));
          ps.setTimestamp(2, Timestamp.valueOf(endDate));
          ps.setFetchSize(FETCH_SIZE);

          return ps;
        },
        rs -> {
          ItemDTO dto =
              new ItemDTO(
                  rs.getString("item_name"),
                  rs.getString("item_description"),
                  rs.getInt("item_quantity"),
                  rs.getBigDecimal("item_buy_price"),
                  rs.getBigDecimal("item_sell_price"),
                  rs.getTimestamp("item_created_at") != null
                      ? rs.getTimestamp("item_created_at").toLocalDateTime()
                      : null,
                  rs.getTimestamp("item_updated_at") != null
                      ? rs.getTimestamp("item_updated_at").toLocalDateTime()
                      : null,
                  rs.getString("supplier_name"),
                  rs.getString("supplier_description"),
                  rs.getString("supplier_cnpj"),
                  rs.getString("supplier_email"),
                  rs.getString("supplier_phone"));

          consumer.accept(dto);
        });
  }
}
