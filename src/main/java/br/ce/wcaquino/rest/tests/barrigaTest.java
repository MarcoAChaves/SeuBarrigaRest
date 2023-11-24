package br.ce.wcaquino.rest.tests;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class barrigaTest {

    private String TOKEN;

    @Before
    public void login() {
        Map<String, String> login = new HashMap<>();
        login.put("email", "wagner@aquino");
        login.put("senha", "123456");

        TOKEN = given()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token");
    }

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



        given()
                .header("Authorization", "JWT ", TOKEN)
                .body("{\"name\": \"conta qualquer\"}")
                .when()
                .post("/contas")
                .then()
        .statusCode(201);
    }

    @Test
    public void deveAlterarContaComSucesso (){


        given()
                .header("Authorization", "JWT ", TOKEN)
                .body("{\"name\": \"conta alterada\"}")
                .when()
                .put("/contas/17585")
                .then()
                .log().all()
                .statusCode(200)
        .body("name", is("conta alterada"));
    }

    @Test
    public void naoDeveInserirContaComMesmoNome (){


        given()
                .header("Authorization", "JWT ", TOKEN)
                .body("{\"name\": \"conta alterada\"}")
                .when()
                .post("/contas")
                .then()
                .log().all()
                .statusCode(400)
        .body("error", is("Já existe uma conta com esse nome!"));
    }
}
