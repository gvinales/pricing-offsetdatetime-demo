package org.acme.pricing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class RatedPriceDTO {

    private UUID id;

    private Integer productId;

    private Integer brandId;

    private Integer rateListId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime appliedStartAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime appliedEndAt;

    @Schema(type = "number", format = "decimal")
    private BigDecimal price;

    private CurrencyEnum currency;

}

