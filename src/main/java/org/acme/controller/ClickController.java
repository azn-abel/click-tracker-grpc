package org.acme.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.acme.repository.ClickEntity;

import io.quarkus.example.Click;
import io.quarkus.example.ClickProtoService;
import io.quarkus.example.CreateClickRequest;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/clicks")
public class ClickController {

    @GrpcClient
    ClickProtoService clicks;

    @GET
    public Uni<List<ClickResponse>> listAllClicks() {
        return clicks.getAllClicks(null)
                .onItem()
                .transform(response -> response.getClicksList()
                        .stream()
                        .map(this::mapGrpcClickToDto)
                        .collect(Collectors.toList()));

    }

    @POST
    public Uni<String> addClick(ClickEntity click) {
        CreateClickRequest grpcRequest = CreateClickRequest.newBuilder()
                .setUserId(click.userId)
                .setX(click.x)
                .setY(click.y)
                .setTimestamp(click.timestamp)
                .setHost(click.host)
                .build();

        return clicks.createClick(grpcRequest)
                .onItem().transform(response -> response.getMessage());
    }

    private ClickResponse mapGrpcClickToDto(Click click) {
        ClickResponse dto = new ClickResponse();
        dto.id = click.getId();
        dto.x = click.getX();
        dto.y = click.getY();
        dto.timestamp = click.getTimestamp();
        dto.host = click.getHost();
        dto.userId = click.getUserId();
        return dto;
    }

    public class ClickResponse {
        public int id;
        public double x;
        public double y;
        public String timestamp;
        public String host;
        public String userId;
    }
}
