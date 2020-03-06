package com.github.quickcougar.smart_fridge.handlers;

import com.google.inject.Inject;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import com.github.quickcougar.smart_fridge.domain.entities.Fridge;
import com.github.quickcougar.smart_fridge.services.FridgeService;

import static ratpack.jackson.Jackson.json;

public class FridgeHandler implements Handler {
    public final FridgeService service;

    @Inject
    FridgeHandler(FridgeService service) {
        this.service = service;
    }

    public void handle(Context ctx) throws Exception {
        ctx.byMethod(m -> m
                .get(() -> {
                    service.getFridge(Integer.valueOf(ctx.getPathTokens().get("id"))).onNull(() -> {
                        ctx.getResponse().status(404).send();
                    }).then(f -> {
                        ctx.render(json(f));
                    });
                })
                .delete(() -> {
                    service.getFridge(Integer.valueOf(ctx.getPathTokens().get("id"))).onNull(() -> {
                        ctx.getResponse().status(404).send();
                    }).then(f -> {
                        service.deleteFridge(Integer.valueOf(ctx.getPathTokens().get("id"))).then(() ->
                                ctx.getResponse().status(200).send()
                        );
                    });
                })
                .patch(() -> {
                    service.getFridge(Integer.valueOf(ctx.getPathTokens().get("id"))).onNull(() -> {
                        ctx.getResponse().status(404).send();
                    }).then(f -> {
                        ctx.parse(Fridge.class).then(fridge -> {
                            f.merge(fridge);
                            service.updateFridge(f, true).then(updatedFridge -> {
                                ctx.render(json(updatedFridge));
                            });
                        });
                    });
                })
                .put(() -> {
                    service.getFridge(Integer.valueOf(ctx.getPathTokens().get("id"))).onNull(() -> {
                        ctx.getResponse().status(404).send();
                    }).then(f -> {
                        ctx.parse(Fridge.class).then(fridge -> {
                            fridge.setId(f.getId());
                            fridge.setId(Integer.valueOf(ctx.getAllPathTokens().get("id")));
                            service.updateFridge(fridge, false).then(updatedFridge -> {
                                service.getFridge(updatedFridge.getId()).then(gotFridge -> {
                                    ctx.render(json(gotFridge));
                                });
                            });
                        });
                    });
                })
        );
    }
}