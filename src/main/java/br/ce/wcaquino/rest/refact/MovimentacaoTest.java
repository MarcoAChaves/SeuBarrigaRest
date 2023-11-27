package br.ce.wcaquino.rest.refact;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.tests.Movimentacao;
import br.ce.wcaquino.rest.tests.utils.DataUtils;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MovimentacaoTest extends BaseTest {

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
    public void deveInserirMovimentacaoComSucesso (){

        Movimentacao mov = getMovimentacaoValida();

         given()
                .body(mov)
                .when()
                .post("/transacoes")
                .then()
                .statusCode(201)
                .extract().path("id");
    }

    private Movimentacao getMovimentacaoValida (){

        Movimentacao mov = new Movimentacao();
        mov.setConta_id(getIdContaPeloNome("Conta para movimentacoes"));
        //mov.setUsuario_id(usuario_id);
        mov.setDescricao("Descricao da movimentacao");
        mov.setEnvolvido("envolvido na mov");
        mov.setTipo("REC");
        mov.setData_transacao(DataUtils.getDataComDiferencaDias(-1));
        mov.setData_pagamento(DataUtils.getDataComDiferencaDias(5));
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }

    @Test
    public void deveValidarCamposObrigatoriosNaMovimentacao() {

        given()
                .body("{}")
                .when()
                .post("/transacoes")
                .then()
                .log().all()
                .statusCode(400)
                .body("$", hasSize(8))
                .body("msg", hasItems(
                        "Data da movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"));
    }

    @Test
    public void naoDeveInserirMovimentacaoComDataFutura (){

        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao(DataUtils.getDataComDiferencaDias(2));


        given()
                .body(mov)
                .when()
                .post("/transacoes")
                .then()
                .log().all()
                .statusCode(400)
                .body("$", hasSize(1))
                .body("msg", hasItem("Data da movimentação ser menor ou igual a data atual"));
    }

    @Test
    public void naoDeveRemoverContaComMovimentacao(){
        Integer CONTA_ID = getIdContaPeloNome("Conta com movimentacao");

        given()
                .pathParam("id", CONTA_ID)
                .when()
                .delete("contas/{id}")
                .then()
                .log().all()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign"));
    }

    @Test
    public void deveRemoverMovimentacao(){
        Integer MOV_ID = getIdMovPelaDescricao("Movimentacao para exclusao");

        given()
                .pathParam("id", MOV_ID)
                .when()
                .delete("/transacoes/{id}")
                .then()
                .log().all()
                .statusCode(204);
    }

    public Integer getIdContaPeloNome(String nome){
        return RestAssured.get("/contas?nome=" +nome).then().extract().path("id[0]");
    }

    public Integer getIdMovPelaDescricao(String desc){
        return RestAssured.get("/transacoes?descricao=" +desc).then().extract().path("id[0]");
    }
}
