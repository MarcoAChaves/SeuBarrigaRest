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

    @Test
    public void deveInserirMovimentacaoComSucesso (){

        Movimentacao mov = new Movimentacao();
        mov.setConta_id(17585);
        //mov.setUsuario_id(usuario_id);
        mov.setDescricao("Descricao da movimentacao");
        mov.setEnvolvido("envlvido na mov");
        mov.setTipo("REC");
        mov.setData_transacao("01/01/2000");
        mov.setData_pagamento("10/05/2010");
        mov.setValor(100f);
        mov.setStatus(true);


        given()
                .header("Authorization", "JWT ", TOKEN)
                .body(mov)
                .when()
                .post("/transacoes")
                .then()
                .log().all()
                .statusCode(201);
    }

    @Test
    public void deveValidarCamposObrigatoriosNaMovimentacao() {

        given()
                .header("Authorization", "JWT ", TOKEN)
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
        mov.setData_transacao("20/05/2025");


        given()
                .header("Authorization", "JWT ", TOKEN)
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

        given()
                .header("Authorization", "JWT ", TOKEN)
                .when()
                .delete("contas/17585")
                .then()
                .log().all()
                .statusCode(500)
        .body("constraint", is("transacoes_conta_id_foreign"));
    }

    @Test
    public void deveCalcularSaldoContas(){

        given()
                .header("Authorization", "JWT ", TOKEN)
                .when()
                .get("saldo")
                .then()
                .log().all()
                .statusCode(200)
        .body("find{it.conta_id == 17585}.saldo", is("100.00"));
    }

    @Test
    public void deveRemoverMovimentacao(){

        given()
                .header("Authorization", "JWT ", TOKEN)
                .when()
                .delete("/transacoes/11588")
                .then()
                .log().all()
                .statusCode(204);
    }

    private Movimentacao getMovimentacaoValida (){

        Movimentacao mov = new Movimentacao();
        mov.setConta_id(17585);
        //mov.setUsuario_id(usuario_id);
        mov.setDescricao("Descricao da movimentacao");
        mov.setEnvolvido("envolvido na mov");
        mov.setTipo("REC");
        mov.setData_transacao("01/01/2000");
        mov.setData_pagamento("10/05/2010");
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }


}
