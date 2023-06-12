package org.acme.pricing.data;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.pricing.dto.CurrencyEnum;
import org.acme.pricing.dto.RatedPriceDTO;
import org.acme.pricing.exception.PlatformHttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Component
@Slf4j
public class PriceDAO {

    private final DataSource dataSource;

    @Autowired
    public PriceDAO(DataSource dataSource) {

        this.dataSource = dataSource;
    }

    public RatedPriceDTO findPriceByDate(OffsetDateTime date, Integer productId, Integer brandId) {
        String query = "SELECT * " +
                "FROM PRICES " +
                "WHERE PRODUCT_ID = ?" +
                "  AND BRAND_ID = ?" +
                "  AND START_DATE <= ?" +
                "  AND END_DATE >= ? " +
                "ORDER BY PRIORITY DESC " +
                "LIMIT 1;";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            statement.setInt(2, brandId);
            statement.setTimestamp(3, Timestamp.from(date.toInstant()));
            statement.setTimestamp(4, Timestamp.from(date.toInstant()));

            log.debug("Executing statement: " + statement);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToRatedPrice(resultSet);
            } else {
                return null; // No matching records found
            }
        } catch (SQLException e) {
            log.debug("Problem executing the SQL statement", e);
            throw new PlatformHttpException("Failed to retrieve prices from the database", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }


    private RatedPriceDTO mapResultSetToRatedPrice(ResultSet resultSet) throws SQLException {
        RatedPriceDTO ratedPrice = new RatedPriceDTO();
        ratedPrice.setId(resultSet.getObject("ID", UUID.class));
        ratedPrice.setProductId(resultSet.getInt("PRODUCT_ID"));
        ratedPrice.setBrandId(resultSet.getInt("BRAND_ID"));
        ratedPrice.setRateListId(resultSet.getInt("PRICE_LIST"));
        ratedPrice.setPrice(resultSet.getBigDecimal("PRICE"));
        ratedPrice.setCurrency(CurrencyEnum.valueOf(resultSet.getString("CURRENCY")));

        // Handle database timestamps + time zone
        Timestamp startTimestamp = resultSet.getTimestamp("START_DATE");
        Timestamp endTimestamp = resultSet.getTimestamp("END_DATE");
        ZoneId timeZone = ZoneId.of(resultSet.getString("TIME_ZONE"));
        OffsetDateTime appliedStartAt = startTimestamp.toLocalDateTime().atZone(timeZone).toOffsetDateTime();
        OffsetDateTime appliedEndAt = endTimestamp.toLocalDateTime().atZone(timeZone).toOffsetDateTime();
        ratedPrice.setAppliedStartAt(appliedStartAt);
        ratedPrice.setAppliedEndAt(appliedEndAt);

        return ratedPrice;
    }

}
