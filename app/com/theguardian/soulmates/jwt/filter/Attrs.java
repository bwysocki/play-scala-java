package com.theguardian.soulmates.jwt.filter;

import com.theguardian.soulmates.jwt.VerifiedJwt;

import play.libs.typedmap.TypedKey;

public class Attrs {
    public static final TypedKey<VerifiedJwt> VERIFIED_JWT = TypedKey.<VerifiedJwt>create("verifiedJwt");
}
