package com.aditya.learn.function;

import com.aditya.learn.dto.Order;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateOrderLambda {

    private final ObjectMapper objectMapper = new ObjectMapper();
    // This Particular Order needs to be saved to the DynamoDB.

    private final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());

    public APIGatewayProxyResponseEvent createOrder(final APIGatewayProxyRequestEvent input,
                                                    final Context context)
            throws JsonProcessingException {

        Order thisOrder = objectMapper.readValue(input.getBody(), Order.class);

        Table table = dynamoDB.getTable(System.getenv("ORDERS_TABLE"));
        Item item = new Item().withPrimaryKey("id", thisOrder.getId())
                .withString("itemName", thisOrder.getItemName())
                .withInt("quantity", thisOrder.getQuantity());

        table.putItem(item);

        return new APIGatewayProxyResponseEvent().withStatusCode(200)
                .withBody("Order Id created : " + thisOrder.getId());
    }

}