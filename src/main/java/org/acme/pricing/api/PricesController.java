package org.acme.pricing.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.acme.pricing.dto.RatedPriceDTO;
import org.acme.pricing.data.PriceDAO;
import org.acme.pricing.exception.ProblemInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/prices")
public class PricesController {

    private final PriceDAO dao;

    @Autowired
    public PricesController(PriceDAO dao) {
        this.dao = dao;
    }

    @Operation(summary = "Find prices")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RatedPriceDTO.class),
                    examples = @ExampleObject(value = """
                            {
                              "productId": 12345,
                              "brandId": 999,
                              "rateListId": 111,
                              "appliedStartAt": "2023-06-08T00:00:00Z",
                              "appliedEndAt": "2023-06-08T23:59:59Z",
                              "price": 29.99,
                              "currency": "USD"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters provided", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemInfo.class))
            }),
            @ApiResponse(responseCode = "404", description = "Price not found for the given parameters", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemInfo.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemInfo.class))
            })

    })
    @GetMapping("/search-rates")
    public ResponseEntity<Object> searchRates(
            @Parameter(example = "2020-07-08T10:00:00Z") OffsetDateTime appliedAt,
            @Parameter(example = "35455") Integer productId,
            @Parameter(example = "1") Integer brandId) {
        RatedPriceDTO price = dao.findPriceByDate(appliedAt, productId, brandId);
        if (price != null) {
            return ResponseEntity.ok(price);
        }
        ProblemInfo problem = ProblemInfo.builder().status(404).title("Price not found for the given parameters").build();
        return ResponseEntity.status(404).body(problem);
    }

}
