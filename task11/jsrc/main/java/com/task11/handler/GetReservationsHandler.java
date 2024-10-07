
package com.task11.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task11.Reservations;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Arrays;
import java.util.Map;

public class GetReservationsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DynamoDbTable<Reservations> reservationsTable;

    public GetReservationsHandler(DynamoDbEnhancedClient dynamoDbClient) {
        reservationsTable = dynamoDbClient.table(System.getenv("RESERVATIONS_TABLE"), TableSchema.fromBean(Reservations.class));
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new ObjectMapper().writeValueAsString(Map.of("reservations" , reservationsTable.scan().items().stream().toList())));
        } catch (Exception e) {
            context.getLogger().log("Error. "+ e.getMessage() + " " + e.getCause() + " " + Arrays.toString(e.getStackTrace()));
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody(
                            """
                                  {
                                      "message" : "Internal server error."
                                  }
                                  """);
        }
    }

}
