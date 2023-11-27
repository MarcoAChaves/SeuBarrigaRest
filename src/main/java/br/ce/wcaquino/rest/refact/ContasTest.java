package br.ce.wcaquino.rest.refact;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.tests.utils.BarrigaUtils;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ContasTest extends BaseTest {

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
        Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para alterar");

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
}
