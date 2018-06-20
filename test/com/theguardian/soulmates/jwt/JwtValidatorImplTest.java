package com.theguardian.soulmates.jwt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.theguardian.soulmates.jwt.JwtValidator.Error;
import com.typesafe.config.Config;

import play.libs.F;

public class JwtValidatorImplTest {
	
	private JwtValidator validator;
	private String correctToken;
	
	@Before
	public void beforeEach() throws Exception {
		Config config = mock(Config.class);
		when(config.getString("jwt.token.secret")).thenReturn("secret");
		validator = new JwtValidatorImpl(config);
		
		String secret = config.getString("jwt.token.secret");
        Integer tokenTimeInMinutes = 1;
        
        Algorithm algorithm = Algorithm.HMAC256(secret);
        this.correctToken = JWT.create()
            .withIssuer("soulmates-frontend")
            .withClaim("user_id", "1")
            .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(tokenTimeInMinutes).toInstant()))
            .sign(algorithm);
	}
	
	@Test
	public void testCorrectValidation() {
		F.Either<Error, VerifiedJwt> result = validator.verify(this.correctToken);
		assertFalse(result.left.isPresent());
		assertEquals("soulmates-frontend", result.right.get().getIssuer());
	}
	
	@Test
	public void testWrongValidation() {
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2l"
				+ "kIjoiNWIyYTAxNGZmM2QzNTBlZTA5OTNlYjcyIiwiaXNzIjoic291b"
				+ "G1hdGVzLWZyb250ZW5kIiwiZXhwIvoxNTI5NDg2NzE5fQ.rTx7xPoYl"
				+ "TYB66Q0erU3Q4s3ixjF39eoCoo_m2vf5pU";
		
		F.Either<Error, VerifiedJwt> result = validator.verify(token);
		assertFalse(result.right.isPresent());
		assertEquals(Error.ERR_INVALID_SIGNATURE_OR_CLAIM, result.left.get());
		
	}
	
}
