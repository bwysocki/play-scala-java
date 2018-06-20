# The soulmates-backend documentation

## Pre Requirements

- Java 1.8.X
- Scala 2.12.2
- SBT 1.0.X
- MongoDB

## Running the application

````text
export JWT_APPLICATION_SECRET=... // production only
export MONGO_HOST=... // production only
export MONGO_PORT=... // production only
export MONGO_DB=... // production only
export MONGO_USERNAME=... // production only
export MONGO_PASSWORD=... // production only
sbt run
````

## Running the application with docker

````javascript
// building
docker build -t soulmates-backend . 

// if you want to run with local mongodb etc.
docker run -it -p 9000:9000 -p 9443:9443 -e MONGO_HOST=docker.for.mac.localhost -e JWT_APPLICATION_SECRET="ddsae316542tggu45532rgd" --rm soulmates-backend

// if you want to install all components (like mongodb)
docker-compose up 
````

## Connection to local MongoDB

After running the application (any of 3 above scenarios), you can connect directly to mongoDB and insert some test records:

````javascript
Bartoszs-MacBook-Pro:scala bartoszwysocki$ mongo
MongoDB shell version v3.6.5
connecting to: mongodb://127.0.0.1:27017
MongoDB server version: 3.6.5
Server has startup warnings: 
2018-06-20T07:19:18.549+0000 I CONTROL  [initandlisten] 
2018-06-20T07:19:18.549+0000 I CONTROL  [initandlisten] ** WARNING: Access control is not enabled for the database.
2018-06-20T07:19:18.549+0000 I CONTROL  [initandlisten] **          Read and write access to data and configuration is unrestricted.
2018-06-20T07:19:18.550+0000 I CONTROL  [initandlisten] 
 db.users.getIndexes()
[
	{
		"v" : 2,
		"key" : {
			"_id" : 1
		},
		"name" : "_id_",
		"ns" : "test.users"
	},
	{
		"v" : 2,
		"key" : {
			"username" : 1
		},
		"name" : "username_1",
		"ns" : "test.users"
	}
]
> db.users.find()
> db.users.insert({username: "bwy", password: "test"})
WriteResult({ "nInserted" : 1 })
> db.users.find()
{ "_id" : ObjectId("5b2a014ff3d350ee0993eb72"), "username" : "bwy", "password" : "test" }

````


## JWT Authentication In Play Framework

JWT verification is done with the library : https://github.com/auth0/java-jwt. The project uses the `Authentication: Bearer` authentication method.

### JWT authentication in Controllers

A convenient helper injected to the controller makes it easy to verify the JWT token. The error can be found in `res.left` and the verified JWT in `res.right`. VerifiedJwt should be customized  to fit your project.

````java
@Inject
private JwtControllerHelper jwtControllerHelper;

public Result requiresJwt() {
    return jwtControllerHelper.verify(request(), res -> {
        if (res.left.isPresent()) {
            return forbidden(res.left.get().toString());
        }

        VerifiedJwt verifiedJwt = res.right.get();
        Logger.debug("{}", verifiedJwt);
        return ok("access granted");
    });
}
````

Inside VerifiedJwt object you can store custom properties like here:

````java
// create
return JWT.create()
    .withIssuer("soulmates-frontend") 
    .withClaim("user_id", userId)
    
// read after token verification
VerifiedJwt verifiedJwt = res.right.get();
verifiedJwt.getUserId();

````


### JWT authentication using a Filter

The filter version makes use of Plays route modifiers and attributes. The filter `JwtFiler` can be used as any other filter in Play. 

The filter automatically puts the VerifiedJwt object in a request attribute. This means that we can retrieve the object in our controller and start using it.

````java
public Result requiresJwtViaFilter() {
    Optional<VerifiedJwt> oVerifiedJwt = request().attrs().getOptional(Attrs.VERIFIED_JWT);
    return oVerifiedJwt.map(verifiedJwt -> {
        Logger.debug(verifiedJwt.getUserId(););
        return ok("access granted via filter");
    }).orElse(forbidden("eh, no verified jwt found"));
}
````

An example of controller that does not require JWT (conf/routes) :

````text
+ noJwtFilter
GET     /login                           controllers.LoginController.login
````

### JWT composite (annotation)

If you add @JWTSecured annotation on filtered method, you can get logged user information directly from context:

````java
@JWTSecured
public Result requiresJwtViaAnnotation() {
	VerifiedJwt verifiedJwt = (VerifiedJwt) ctx().args.get(JWTSecured.VERIFIED_JWT);
	verifiedJwt.getUserId();
}
````

### JWT secret key

The secret key is the most important part of JWT authentication. The application uses default secret key or ENV variable if is set:

````text
jwt.token.secret="dnsau8329ubtyreghujabksadh832huasgdskahd" <-- default one
jwt.token.secret=${?JWT_APPLICATION_SECRET} <-- taken from ENV if present
````

`During production execution, please remember to set JWT_APPLICATION_SECRET!`


### JWT endpoints

To get authentication token use follwing endpoint:

````text
POST /api/login
BODY {
	"username": "abc",
	"password": "Test"
}
````

as a response, you should get:

````json
{
    "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjo3LCJpc3MiOiJzb3VsbWF0ZXMtZnJvbnRlbmQiLCJleHAiOjE1Mjg5NjU3Njl9.2BjhKvVUFbFgs8PjIANmCrF3sa9r9ibq6faS1a6o0II"
}
````

next you can add Authorization header in following way and call protected endpoints:

````text
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjo3LCJpc3MiOiJzb3VsbWF0ZXMtZnJvbnRlbmQiLCJleHAiOjE1Mjg5NjU3Njl9.2BjhKvVUFbFgs8PjIANmCrF3sa9r9ibq6faS1a6o0II
````

## Connection to MongoDB

The application uses MongoDB as a database. Morphia (http://mongodb.github.io/morphia/) has been chosen as ORM library.

By default soulmates-backend uses local MongoDB installation. If you want to connect DB with other address, use following env variables:
- MONGO_HOST (default: localhost)
- MONGO_PORT (default: 27017)
- MONGO_DB (default: test)
- MONGO_USERNAME (default: "")
- MONGO_PASSWORD (default: "")




