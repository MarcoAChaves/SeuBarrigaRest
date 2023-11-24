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
}  
