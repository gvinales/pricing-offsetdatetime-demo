package org.acme.pricing.api;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.pricing.dto.RatedPriceDTO;
import org.acme.pricing.exception.ErrorInfo;
import org.acme.pricing.exception.PlatformHttpException;
import org.acme.pricing.exception.ProblemInfo;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class PricesControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    /**
     * Test 1: petición a las 10:00 del día 14/jun/2020 del producto 35455 para la brand 1
     * Assuming current Madrid time: GMT+2
     */
    @Test
    @Order(value = 1)
    void testOne() {
        Integer brandId = 1;
        Integer productId = 35455;
        OffsetDateTime appliedAt = OffsetDateTime.parse("2020-06-14T10:00:00+02:00");
        ResponseEntity<RatedPriceDTO> response = restTemplate.getForEntity(
                createURLWithPort("/api/prices/search-rates?appliedAt={appliedAt}&productId={productId}&brandId={brandId}"),
                RatedPriceDTO.class, appliedAt, productId, brandId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        RatedPriceDTO price = response.getBody();

        assertEquals(productId, price.getProductId());
        assertEquals(brandId, price.getBrandId());

        log.debug("Price 1: " + price.getPrice());
        assertThat(BigDecimal.valueOf(35.5), Matchers.comparesEqualTo(price.getPrice()));
    }

    /**
     * Test 2: petición a las 16:00 del día 14/jun/2020 del producto 35455 para la brand 1
     * Assuming current Madrid time: GMT+2
     */
    @Test
    @Order(value = 2)
    void testTwo() {
        Integer brandId = 1;
        Integer productId = 35455;
        OffsetDateTime appliedAt = OffsetDateTime.parse("2020-06-14T16:00:00+02:00");
        ResponseEntity<RatedPriceDTO> response = restTemplate.getForEntity(
                createURLWithPort("/api/prices/search-rates?appliedAt={appliedAt}&productId={productId}&brandId={brandId}"),
                RatedPriceDTO.class, appliedAt, productId, brandId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        RatedPriceDTO price = response.getBody();

        assertEquals(productId, price.getProductId());
        assertEquals(brandId, price.getBrandId());

        log.debug("Price 2: " + price.getPrice());
        assertThat(BigDecimal.valueOf(25.45), Matchers.comparesEqualTo(price.getPrice()));
    }

    /**
     * Test 3: petición a las 21:00 del día 14/jun/2020 del producto 35455 para la brand 1
     * Assuming current Madrid time: GMT+2
     */
    @Test
    @Order(value = 3)
    void testThree() {
        Integer brandId = 1;
        Integer productId = 35455;
        OffsetDateTime appliedAt = OffsetDateTime.parse("2020-06-14T21:00:00+02:00");
        ResponseEntity<RatedPriceDTO> response = restTemplate.getForEntity(
                createURLWithPort("/api/prices/search-rates?appliedAt={appliedAt}&productId={productId}&brandId={brandId}"),
                RatedPriceDTO.class, appliedAt, productId, brandId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        RatedPriceDTO price = response.getBody();

        assertEquals(productId, price.getProductId());
        assertEquals(brandId, price.getBrandId());

        log.debug("Price 3: " + price.getPrice());
        assertThat(BigDecimal.valueOf(35.5), Matchers.comparesEqualTo(price.getPrice()));
    }

    /**
     * Test 4: petición a las 10:00 del día 15/jun/2020 del producto 35455 para la brand 1
     * Assuming current Madrid time: GMT+2
     */
    @Test
    @Order(value = 4)
    void testFour() {
        Integer brandId = 1;
        Integer productId = 35455;
        OffsetDateTime appliedAt = OffsetDateTime.parse("2020-06-15T10:00:00+02:00");
        ResponseEntity<RatedPriceDTO> response = restTemplate.getForEntity(
                createURLWithPort("/api/prices/search-rates?appliedAt={appliedAt}&productId={productId}&brandId={brandId}"),
                RatedPriceDTO.class, appliedAt, productId, brandId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        RatedPriceDTO price = response.getBody();

        assertEquals(productId, price.getProductId());
        assertEquals(brandId, price.getBrandId());

        log.debug("Price 4: " + price.getPrice());
        assertThat(BigDecimal.valueOf(30.5), Matchers.comparesEqualTo(price.getPrice()));
    }

    /**
     * Test 5: petición a las 21:00 del día 16/jun/2020 del producto 35455 para la brand 1
     * Assuming current Madrid time: GMT+2
     */
    @Test
    @Order(value = 5)
    void testFive() {
        Integer brandId = 1;
        Integer productId = 35455;
        OffsetDateTime appliedAt = OffsetDateTime.parse("2020-06-16T21:00:00+02:00");
        ResponseEntity<RatedPriceDTO> response = restTemplate.getForEntity(
                createURLWithPort("/api/prices/search-rates?appliedAt={appliedAt}&productId={productId}&brandId={brandId}"),
                RatedPriceDTO.class, appliedAt, productId, brandId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        RatedPriceDTO price = response.getBody();

        assertEquals(productId, price.getProductId());
        assertEquals(brandId, price.getBrandId());

        log.debug("Price 5: " + price.getPrice());
        assertThat(BigDecimal.valueOf(38.95), Matchers.comparesEqualTo(price.getPrice()));
    }


    @Test
    @Order(value = 6)
    void shouldReturn400_OnInvalidDate() {
        Integer brandId = 1;
        Integer productId = 35455;
        String appliedAt = "2020-06-15T10:00:00";
        ResponseEntity<ProblemInfo> response = restTemplate.getForEntity(
                createURLWithPort("/api/prices/search-rates?appliedAt={appliedAt}&productId={productId}&brandId={brandId}"),
                ProblemInfo.class, appliedAt, productId, brandId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        ProblemInfo error = response.getBody();
        assertNotNull(error);
        log.debug("Error: " + error.getTitle());
        assertNotNull(error.getErrors());
        for (ErrorInfo err : error.getErrors()) {
            log.debug(String.format("%s", err.getDetail()));
        }
        assertTrue(error.getErrors().size() > 0);
    }

    @Test
    @Order(value = 7)
    void shouldReturn404_OnUnknownProductId() {
        Integer brandId = 1;
        Integer productId = 1;
        String appliedAt = "2020-06-15T10:00:00Z";
        ResponseEntity<ProblemInfo> response = restTemplate.getForEntity(
                createURLWithPort("/api/prices/search-rates?appliedAt={appliedAt}&productId={productId}&brandId={brandId}"),
                ProblemInfo.class, appliedAt, productId, brandId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());

        ProblemInfo error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getTitle());
        log.debug("Problem: " + error.getTitle());
    }

    /**
     * Needs order and needs to run last because it wipes the DB which would impact other tests.
     */
    @Test
    @Order(value = 999)
    void shouldReturn500_DatabaseProblem() {
        Integer brandId = 1;
        Integer productId = 1;
        String appliedAt = "2020-06-15T10:00:00Z";

        dropAllDbObjects();

        ResponseEntity<ProblemInfo> response = restTemplate.getForEntity(
                createURLWithPort("/api/prices/search-rates?appliedAt={appliedAt}&productId={productId}&brandId={brandId}"),
                ProblemInfo.class, appliedAt, productId, brandId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        ProblemInfo error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getTitle());
        log.debug("Problem: " + error.getTitle());
    }

    private void dropAllDbObjects() {
        String query = "DROP ALL OBJECTS;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PlatformHttpException("Failed to retrieve prices from the database", Response.Status.INTERNAL_SERVER_ERROR);
        }

    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
