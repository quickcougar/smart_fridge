package com.github.quickcougar.smart_fridge.handlers;

import com.github.quickcougar.smart_fridge.domain.entities.Fridge;
import com.github.quickcougar.smart_fridge.services.FridgeService;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Operation;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;

public class FridgeBaseHandler implements Handler {

    Logger log = LoggerFactory.getLogger(FridgeBaseHandler.class);

    public final FridgeService service;

    @Inject
    FridgeBaseHandler(FridgeService service) {
        this.service = service;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        final String errorMsg = "{\"message\": \"Bad content\"}";
        ctx.byMethod(m -> m
                .post(() -> {
                            ctx.parse(Fridge.class).onNull(() -> {
                                ctx.getResponse().status(400);
                                ctx.render(errorMsg);
                            }).onError(error -> {
                                ctx.getResponse().status(400);
                                log.warn(error.toString());
                                ctx.render(errorMsg);
                            }).then(fridge -> {
                                service.addFridge(fridge).then(f -> {
                                    ctx.getResponse().status(201);
                                    ctx.render(Jackson.json(f));
                                });
                            });
                        }
                )
                .get(() -> {
                    service.listFridges().then(list -> ctx.render(Jackson.json(list)));
                })
                .delete(() -> {
                    Operation.of(() -> {
                                service.deleteFridge(Integer.valueOf(ctx.getAllPathTokens().get("id")));
                            }
                    ).then(() -> {
                        ctx.getResponse().status(200).send();
                    });
                }));
    }
}
