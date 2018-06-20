package com.theguardian.soulmates.jwt.composite;

import java.util.concurrent.CompletionStage;

import com.theguardian.soulmates.jwt.VerifiedJwt;
import com.theguardian.soulmates.jwt.filter.Attrs;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

public class JWTSecuredAction extends Action<JWTSecured> {
	
    public CompletionStage<Result> call(Http.Context ctx) {
    	VerifiedJwt verifiedJwt = ctx.request().attrs().get(Attrs.VERIFIED_JWT);
    	ctx.args.put(JWTSecured.VERIFIED_JWT, verifiedJwt);
        return delegate.call(ctx);
    }
    
}