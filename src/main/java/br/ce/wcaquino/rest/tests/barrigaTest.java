package br.ce.wcaquino.rest.tests;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class barrigaTest {

    @Test
    public void naoDeveAcessarAPISemToken(){
        given()
                .when()
                .get("/contas")
                .then()
        .statusCode(401)
                ;

    }

    @Test
    public void deveIncluirContaComSucesso (){
        Map<String, String> login = new HashMap<>();
        login.put("email", "marco4jet@hotmail.com");
        login.put("senha", "Ma18");

        String token = given()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
        .extract().path("token");

        given()
                .header("Authorization", "JWT ", token)
                .body("{\"name\": \"conta qualquer\"}")
                .when()
                .post("/contas")
                .then()
        .statusCode(201);
    }
}
