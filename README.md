# Pricing with OffsetDateTime Demo

This demo backend application provides a RESTful endpoint for querying product prices from a external system database.
Prices are rated by date/time.

The timestamps in the database do not have time zone information, so there is a special mapping performed to present
them properly in the RESTful endpoints.

## Tech Stack

- Spring Boot 3
- Maven
- Java 17
- H2 DB

## How to use the code

```
mvn clean test

mvn spring-boot:run

# test the endpoint
curl -X 'GET' \
  'http://localhost:18080/api/prices/search-rates?appliedAt=2020-09-08T10%3A00%3A00Z&productId=35455&brandId=1' \
  -H 'accept: application/json'
```

Open browser in http://localhost:18080

H2 DB Console: http://localhost:18080/h2-console
While the app is running, the H2 database can also be accessed with external clients.
JDBC URL: `jdbc:h2:tcp://localhost:9090/mem:pricing_module`

## Implementation

The existing dataset doesn't have dates with the time zone information.

```
Table PRICES
-------
BRAND_ID  START_DATE             END_DATE               PRICE_LIST  PRODUCT_ID  PRIORITY    PRICE     CURR
----------------------------------------------------------------------------------------------------------
1         2020-06-14-00.00.00    2020-12-31-23.59.59    1            35455      0           35.50     EUR
1         2020-06-14-15.00.00    2020-06-14-18.30.00    2            35455      1           25.45     EUR
1         2020-06-15-00.00.00    2020-06-15-11.00.00    3            35455      1           30.50     EUR
1         2020-06-15-16.00.00    2020-12-31-23.59.59    4            35455      1           38.95     EUR
```

### Timestamp Handling

A good approach to handle timestamps in distributed systems is:

1. to persist all database datetimes/timestamps as GMT0/UTC.
2. to configure all backend systems to use GMT0/UTC.
3. for Java backends to code logic using OffsetDateTime.
4. to have the API endpoints consume/produce GMT0/UTC (OffsetDateTime).
5. to let the clients/presentation/UIs map the time zone to the final date/time according to the use cases, use
   preferences, location, etc.

The format of a Java OffsetDateTime in the ISO-8601 standard is: `yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX`, where each component
is:

* yyyy: Four-digit year
* MM: Two-digit month (01-12)
* dd: Two-digit day of the month (01-31)
* 'T': Letter 'T' as a separator between date and time
* HH: Two-digit hour of the day in 24-hour format (00-23)
* mm: Two-digit minute of the hour (00-59)
* ss: Two-digit second of the minute (00-59)
* .SSS: Milliseconds (optional)
* XXXXX: Time zone offset in the format `±hh:mm` or `Z` for UTC

For example, an OffsetDateTime in the ISO-8601 format could look like this: `2020-06-08T12:30:45+02:00` or
`2020-06-08T12:30:45Z` for UTC.

## Implementation

We have some options here:

1. Update all dates in the database to be GMT0/UTC. This requires all data client to update their code. Might not be
   feasible in complex enterprise environments.
2. Add a new column to handle the time zone and do the conversion in our application code. This allows our
   backend to be time zone aware and consume/product OffsetDateTime.
3. We can have a data pipeline (online if possible) that reads (or listens) new data from the original table, converts
   timestamps to GMT0/UTC and persists the data in a new table for our system with the timestamps with timezone
   information.
4. Others?

Option 2 appears to be more straightforward for a real use case as it impacts just our system.

### Database updates

We'll add a new column to the PRICES table to handle the time zone.

> We assume that time zone is GMT+2.

```sql
ALTER TABLE PRICES
    ADD COLUMN TIME_ZONE VARCHAR(255);

UPDATE PRICES
SET TIME_ZONE = 'GMT+2';
```

### SpringBoot database initialization

We disable the Hibernate db init:

```
spring.jpa.hibernate.ddl-auto=none
```

We place the `schema.sql` and `data.sql` files in the classpath (`resources` directory) to let Spring Boot initialize
our database on startup.

### API Design

#### REST Endpoint
GET /api/prices/search-rates

#### Request Query Parameters

- appliedAt (OffsetDateTime) with format `yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX`
- productId (Integer)
- brandId (Integer)

#### Responses
Body: `application/json`
200: body with the RatedPriceDTO
400: bad request returns a custom Problem object built with a custom exception handler.
404: prices not found
500: server error

#### Response Body
For a successful result the body contains a RatedPriceDTO:
- productId (Integer)
- brandId (Integer)
- rateListId (Integer)
- appliedStartAt (OffsetDateTime)
- appliedEndAt (OffsetDateTime)
- price (BigDecimal)

### Microservice design

Given the simple use case we'll use a plain DAO with a native SQL query. We could have leveraged on Spring JPA and use entity
and repository patterns.

The REST endpoint is provided by a Spring Boot RestController. We inject the DAO to the controller and call the query
method.

A custom exception handling mechanism is implemented for hiding system errors from the REST output and returning
user-friendly error responses.

A Spring Boot test is provided with the requested, plus tests for the different expected response status codes.

We add Swagger UI from SpringDocs to have the option to test the endpoint with alternative parameters.

As this is a new project, we use the latest versions available of Java, Spring Boot, etc.

Enjoy!
