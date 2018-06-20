package com.theguardian.soulmates.config;
import com.google.inject.AbstractModule;
import com.theguardian.soulmates.jwt.JwtControllerHelper;
import com.theguardian.soulmates.jwt.JwtControllerHelperImpl;
import com.theguardian.soulmates.jwt.JwtValidator;
import com.theguardian.soulmates.jwt.JwtValidatorImpl;

public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(JwtValidator.class).to(JwtValidatorImpl.class).asEagerSingleton();
        bind(JwtControllerHelper.class).to(JwtControllerHelperImpl.class).asEagerSingleton();
        bind(MongoDb.class).asEagerSingleton();
    }
}
