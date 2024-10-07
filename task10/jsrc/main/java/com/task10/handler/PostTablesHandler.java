
package com.task10.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task10.Tables;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Arrays;
import java.util.Map;

public class PostTablesHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DynamoDbTable<Tables> tablesTable;

    public PostTablesHandler(DynamoDbEnhancedClient dynamoDbClient) {
        tablesTable = dynamoDbClient.table(System.getenv("TABLES_TABLE"), TableSchema.fromBean(Tables.class));
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try {
            Tables tables = new ObjectMapper().readValue(requestEvent.getBody(), Tables.class);

            tablesTable.putItem(tables);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new ObjectMapper().writeValueAsString(Map.of("id" , tables.getId())));
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
