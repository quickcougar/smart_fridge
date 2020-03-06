package com.github.quickcougar.smart_fridge.modules;

import com.github.quickcougar.smart_fridge.handlers.FridgeBaseHandler;
import com.github.quickcougar.smart_fridge.handlers.FridgeHandler;
import com.github.quickcougar.smart_fridge.services.FridgeService;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class FridgeModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FridgeBaseHandler.class);
        bind(FridgeHandler.class);
        bind(FridgeService.class).in(Scopes.SINGLETON);
    }
}
