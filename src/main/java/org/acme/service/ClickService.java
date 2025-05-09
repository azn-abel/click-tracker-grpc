package org.acme.service;

import java.util.List;

import org.acme.repository.ClickEntity;

import io.quarkus.example.Click;
import io.quarkus.example.ClickProtoService;
import io.quarkus.example.CreateClickReply;
import io.quarkus.example.CreateClickRequest;
import io.quarkus.example.Empty;
import io.quarkus.example.GetAllClicksResponse;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.transaction.Transactional;

@GrpcService
public class ClickService implements ClickProtoService {

    @Override
    @Transactional
    public Uni<CreateClickReply> createClick(CreateClickRequest request) {
        return Uni.createFrom().item(() -> {
            ClickEntity click = new ClickEntity();
            click.x = request.getX();
            click.y = request.getY();
            click.timestamp = request.getTimestamp();
            click.host = request.getHost();
            click.userId = request.getUserId();
            click.persist();
            return CreateClickReply.newBuilder()
                    .setMessage("Click created with ID: " + click.id)
                    .build();
        })
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @Override
    public Uni<GetAllClicksResponse> getAllClicks(Empty request) {
        return Uni.createFrom().<GetAllClicksResponse>item(() -> {
            List<ClickEntity> clicks = ClickEntity.listAll();

            GetAllClicksResponse.Builder responseBuilder = GetAllClicksResponse.newBuilder();
            for (ClickEntity click : clicks) {
                responseBuilder.addClicks(
                        Click.newBuilder()
                                .setId(click.id.intValue())
                                .setX(click.x)
                                .setY(click.y)
                                .setTimestamp(click.timestamp)
                                .setHost(click.host)
                                .setUserId(click.userId)
                                .build());
            }
            return responseBuilder.build();
        })
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }
}
