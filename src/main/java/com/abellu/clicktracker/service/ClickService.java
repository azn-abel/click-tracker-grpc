package com.abellu.clicktracker.service;

import java.util.List;

import com.abellu.clicktracker.grpc.Click;
import com.abellu.clicktracker.grpc.ClickProtoService;
import com.abellu.clicktracker.grpc.CreateClickReply;
import com.abellu.clicktracker.grpc.CreateClickRequest;
import com.abellu.clicktracker.grpc.Empty;
import com.abellu.clicktracker.grpc.GetAllClicksResponse;
import com.abellu.clicktracker.repository.ClickEntity;

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
