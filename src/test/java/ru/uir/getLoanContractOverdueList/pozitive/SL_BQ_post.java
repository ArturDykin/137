package ru.uir.getLoanContractOverdueList.pozitive;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SL_BQ_post {

    @Test
    @DisplayName("-------------------SL-CFT")
    public void postSCOL_BQ() {

        RestAssured.baseURI = "http://usbp-cr-get-repay-by-loan-cntr.ss1-genr01-usbp-credit-integration-ift01.apps.ss1-genr01.test.vtb.ru";
        var test = RestAssured.given()
                .urlEncodingEnabled(false)
                .with()
                .header("Content-Type", "application/json")
                .body("{\"GetLoanByContractFS_input\":{\r\n\"Headerinfo\"{\r\n\"RqUID\":\"17D0D0D76F5D4248845F22CDB643CCB7\"," +
                        "\r\n\"RqType\":\"GetLoanByContractFS_input\",\r\n\"RqTm\": \"02/07/2022 14:24:15\",\r\n\"SystemFrom\": " +
                        "\"SBL_CL\",\r\n\"SystemTo\": \"CFT2RL\",\r\n\"ContextUserinfo\":\"string\",\r\n\"SessionId\": \"string\"\r\n}," +
                        "\r\n\"ListOfRequest\":{\r\n\"Request\":{\r\n\"LoanContractType\": \"string\",\r\n\"LoanContractTypeq\": " +
                        "\"V625/0100-0000020\",\r\n\"BranchCode\": \"00000\",\r\n\"Date\": \"02/07/2022 14:24:15\"\r\n}\r\n}\r\n}\r\n\r\n}")
                .when().post("/api/v1/getLoanContractOverdueList")
                .then().assertThat().statusCode(200).extract().response();
                System.out.println(test.body().prettyPrint());
       // System.out.println(test.jsonPath());
    }
}



//{"GetLoanByContractFS_input":{
//        "Headerinfo"{
//        "RqUID": "17D0D0D76F5D4248845F22CDB643CCB7",
//        "RqType": "GetLoanByContractFS_input",
//        "RqTm": "02/07/2022 14:24:15",
//        "SystemFrom": "SBL_CL",
//        "SystemTo": "CFT2RL",
//        "ContextUserinfo": "string",
//        "SessionId": "string"
//        },
//        "ListOfRequest":{
//        "Request":{
//        "LoanContractType": "string",
//        "LoanContractType": "V625/0100-0000020",
//        "BranchCode": "00000",
//        "Date": "02/07/2022 14:24:15"
//        }
//        }
//        }
//        }


//        RequestSpecification request = given();
//        request.header("Content-Type", "application/json");
//        request.body(requestBody.toString());
//        Response response = request.post("http://usbp-cr-get-repay-by-loan-cntr.ss1-genr01-usbp-credit-integration-ift01.apps.ss1-genr01.test.vtb.ru/api/v1/getLoanContractOverdueList");
//
//        int statusCode = response.getStatusCode();
//        Assert.assertEquals(statusCode, 201);
//        String successCode = response.jsonPath().get("SuccessCode");
//        Assert.assertEquals(successCode, "OPERATION_SUCCESS");
//        System.out.println(response.getBody().asString());
