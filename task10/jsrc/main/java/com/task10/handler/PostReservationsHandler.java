
package com.task10.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task10.Reservations;
import com.task10.Tables;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class PostReservationsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DynamoDbTable<Reservations> reservationsTable;
    private DynamoDbTable<Tables> tablesTable;

    public PostReservationsHandler(DynamoDbEnhancedClient dynamoDbClient) {
        reservationsTable = dynamoDbClient.table(System.getenv("RESERVATIONS_TABLE"), TableSchema.fromBean(Reservations.class));
        tablesTable = dynamoDbClient.table(System.getenv("TABLES_TABLE"), TableSchema.fromBean(Tables.class));
    }

    private Pattern datePattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private Pattern timePattern = Pattern.compile("^(?:[01]\\d|2[0-3]):[0-5]\\d$");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try {
            Reservations reservations = new ObjectMapper().readValue(requestEvent.getBody(), Reservations.class);
            if (!datePattern.matcher(reservations.getDate()).matches() || !timePattern.matcher(reservations.getSlotTimeStart()).matches()
            || !timePattern.matcher(reservations.getSlotTimeEnd()).matches()) {
                new APIGatewayProxyResponseEvent()
                        .withStatusCode(400)
                        .withBody(
                                """
                                      {
                                          "message" : "Bad Request"
                                      }
                                      """);
            }
            tablesTable.scan().items().stream().filter(t -> t.getNumber() == reservations.getTableNumber()).findAny().orElseThrow(() -> new RuntimeException("Table not found."));
            String reservationsId = UUID.randomUUID().toString();
            reservations.setId(reservationsId);

            if (!isTableAvailable(reservations)) {
                throw new RuntimeException("Table is not available.");
            }

            reservationsTable.putItem(reservations);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new ObjectMapper().writeValueAsString(Map.of("reservationId" , reservationsId)));
        } catch (Exception e) {
            context.getLogger().log("Error. "+ e.getMessage() + " " + e.getCause() + " " + Arrays.toString(e.getStackTrace()));
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(
                            """
                                  {
                                      "message" : "Internal server error."
                                  }
                                  """);
        }
    }

    private boolean isTableAvailable(Reservations reservations) {
        LocalTime startTime = LocalTime.parse( reservations.getSlotTimeStart());
        LocalTime endTime = LocalTime.parse( reservations.getSlotTimeEnd());
        return reservationsTable.scan().items().stream().noneMatch(r -> {
            if (r.getTableNumber() == reservations.getTableNumber() || r.getDate().equals(reservations.getDate())) {
                return false;
            }
            LocalTime anotherStartTime = LocalTime.parse( r.getSlotTimeStart());
            LocalTime anotherEndTime = LocalTime.parse( r.getSlotTimeEnd());
            boolean comienzaDuranteOtro = !startTime.isAfter(anotherEndTime) && !startTime.isBefore(anotherStartTime);
            boolean terminaDuranteOtro = !endTime.isAfter(anotherEndTime) && !endTime.isBefore(anotherStartTime);
            boolean cubreOtro = startTime.isBefore(anotherStartTime) && endTime.isAfter(anotherEndTime);

            return comienzaDuranteOtro || terminaDuranteOtro || cubreOtro;

        });
    }

}
