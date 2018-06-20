package com.theguardian.soulmates.controllers;

import java.util.Optional;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.theguardian.soulmates.jwt.JwtControllerHelper;
import com.theguardian.soulmates.jwt.VerifiedJwt;
import com.theguardian.soulmates.jwt.composite.JWTSecured;
import com.theguardian.soulmates.jwt.filter.Attrs;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.com.theguardian.soulmates.SomeHelper;

public class JWTSampleController extends Controller {
    
	@Inject
    private JwtControllerHelper jwtControllerHelper;

    public Result generateSignedToken() {
    	return ok("Some scala magic key: " + SomeHelper.magicString());
    }

    public Result requiresJwt() {
        return jwtControllerHelper.verify(request(), res -> {
            if (res.left.isPresent()) {
                return forbidden(res.left.get().toString());
            }

            VerifiedJwt verifiedJwt = res.right.get();
            System.out.println(verifiedJwt.getUserId());
            Logger.debug("{}", verifiedJwt);

            ObjectNode result = Json.newObject();
            result.put("access", "granted");
            result.put("secret_data", "birds fly");
            return ok(result);
        });
    }

    public Result requiresJwtViaFilter() {
        Optional<VerifiedJwt> oVerifiedJwt = request().attrs().getOptional(Attrs.VERIFIED_JWT);
        return oVerifiedJwt.map(verifiedJwt -> {
            Logger.debug(verifiedJwt.toString());
            System.out.println(verifiedJwt.getUserId());
            return ok("access granted via filter");
        }).orElse(forbidden("eh, no verified jwt found"));
    }
    
    @JWTSecured
    public Result requiresJwtViaAnnotation() {
    	VerifiedJwt verifiedJwt = (VerifiedJwt) ctx().args.get(JWTSecured.VERIFIED_JWT);
    	return ok("Some scala magic key: " + SomeHelper.magicString() + ", id=" + verifiedJwt.getUserId());
    }
}
