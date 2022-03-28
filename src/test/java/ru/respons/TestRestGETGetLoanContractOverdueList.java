package ru.respons;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

import java.util.List;

import static io.restassured.RestAssured.given;

public class TestRestGETGetLoanContractOverdueList {
    private final static String URL = "http://usbp-cr-get-repay-by-loan-cntr.ss1-genr01-usbp-credit-integration-ift01.apps.ss1-genr01.test.vtb.ru";

    @Test
    @DisplayName("RestGetLoanContractOverdueList to CFT Пример")
    public void RestGetLoanContractOverdueListToCFTPrimer(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        //Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());
        List<ResponseLoanContractOverdueList> responseLoanContractOverdueList = given()
                .when()
                .get("/api/v1/getLoanContractOverdueList")
                .then().log().all()
                .extract().body().jsonPath().getList("LoanContractID", ResponseLoanContractOverdueList.class);
    }
    @Test
    @DisplayName("RestGetLoanContractOverdueList to CFT, договор найден")
    public void RestGetLoanContractOverdueListToCFT(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK201());
        ResponseLoanContractOverdueList test = given()
                .with()
                .header("Content-Type", "application/json")
                .body("{\"GetLoanByContractFS_input\":{\r\n\"Headerinfo\"{\r\n\"RqUID\":\"17D0D0D76F5D4248845F22CDB643CCB7\"," +
                        "\r\n\"RqType\":\"GetLoanByContractFS_input\",\r\n\"RqTm\": \"02/07/2022 14:24:15\",\r\n\"SystemFrom\": " +
                        "\"SBL_CL\",\r\n\"SystemTo\": \"CFT2RL\",\r\n\"ContextUserinfo\":\"string\",\r\n\"SessionId\": \"string\"\r\n}," +
                        "\r\n\"ListOfRequest\":{\r\n\"Request\":{\r\n\"LoanContractType\": \"string\",\r\n\"LoanContractTypeq\": " +
                        "\"V625/0100-0000020\",\r\n\"BranchCode\": \"00000\",\r\n\"Date\": \"02/07/2022 14:24:15\"\r\n}\r\n}\r\n}\r\n\r\n}")
                .when()
                .post(URL + "/api/v1/getLoanContractOverdueList")
                .then().log().all()
                .extract().as(ResponseLoanContractOverdueList.class);
        Assert.assertNotNull(ResponseLoanContractOverdueList.getLoanContractID());
    }

    @Test
    @DisplayName("RestGetLoanContractOverdueList to CFT, договор не найден")
    public void RestGetLoanContractOverdueListToCFTBed(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK201());
        ResponseLoanContractOverdueList test = given()
                .with()
                .header("Content-Type", "application/json")
                .body("{\"GetLoanByContractFS_input\":{\r\n\"Headerinfo\"{\r\n\"RqUID\":\"17D0D0D76F5D4248845F22CDB643CCB7\"," +
                        "\r\n\"RqType\":\"GetLoanByContractFS_input\",\r\n\"RqTm\": \"02/07/2022 14:24:15\",\r\n\"SystemFrom\": " +
                        "\"SBL_CL\",\r\n\"SystemTo\": \"CFT2RL\",\r\n\"ContextUserinfo\":\"string\",\r\n\"SessionId\": \"string\"\r\n}," +
                        "\r\n\"ListOfRequest\":{\r\n\"Request\":{\r\n\"LoanContractType\": \"string\",\r\n\"LoanContractTypeq\": " +
                        "\"V625/0100-XXXXXXX\",\r\n\"BranchCode\": \"00000\",\r\n\"Date\": \"02/07/2022 14:24:15\"\r\n}\r\n}\r\n}\r\n\r\n}")
                .when()
                .post(URL + "/api/v1/getLoanContractOverdueList")
                .then().log().all()
                .extract().as(ResponseLoanContractOverdueList.class);
       //Assert.assertNotNull(ResponseLoanContractOverdueList.getLoanContractID());
        Assert.assertEquals("Договор не найден", ResponseLoanContractOverdueList.getLoanContractID());
    }
}
