package com.github.quickcougar.smart_fridge.chains;

import com.github.quickcougar.smart_fridge.handlers.FridgeBaseHandler;
import com.github.quickcougar.smart_fridge.handlers.FridgeHandler;
import ratpack.func.Action;
import ratpack.handling.Chain;

public class FridgeChain implements Action<Chain> {
    @Override
    public void execute(Chain chain) {
        chain
                .path("fridges", FridgeBaseHandler.class)
                .path("fridges/:id", FridgeHandler.class)
                .get(ctx -> ctx.render("Welcome to the Smart Fridge Service!"));
    }
}
