package br.ce.wcaquino.rest.refact;

import br.ce.wcaquino.rest.core.BaseTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ContasTest extends BaseTest {

    @BeforeClass
    public static void login() {
        Map<String, String> login = new HashMap<>();
        login.put("email", "wagner@aquino");
        login.put("senha", "123456");

        String TOKEN = given()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token");

        RestAssured.requestSpecification.header("Authorization", "JWT" +  TOKEN);

        RestAssured.get("/reset").then().statusCode(200);
    }

    @Test
    public void deveIncluirContaComSucesso (){

        given()
                .body("{\"name\": \"Conta inserida\"}")
                .when()
                .post("/contas")
                .then()
                .statusCode(201)
                .extract().path("id");
    }
    @Test
    public void deveAlterarContaComSucesso (){
        Integer CONTA_ID = getIdContaPeloNome("Conta para alterar");

        given()
                .body("{\"name\": \"Conta alterada\"}")
                .pathParam("id", CONTA_ID)
                .when()
                .put("/contas/{id}")
                .then()
                .statusCode(200)
                .body("name", is("Conta alterada"));
    }

    @Test
    public void naoDeveInserirContaComMesmoNome (){

        given()
                .body("{\"name\": \"Conta mesmo nome\" }")
                .when()
                .post("/contas")
                .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"));
    }

    public Integer getIdContaPeloNome(String nome){
        return RestAssured.get("/contas?nome=" +nome).then().extract().path("id[0]");
    }
}
