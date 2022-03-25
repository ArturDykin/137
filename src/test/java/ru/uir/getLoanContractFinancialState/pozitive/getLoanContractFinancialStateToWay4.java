package ru.uir.getLoanContractFinancialState.pozitive;

import io.restassured.http.Header;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.message.MessageFactory;
import org.junit.jupiter.api.DisplayName;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class getLoanContractFinancialStateToWay4 {

    @Test
    @DisplayName("getLoanContractFinancialState to Way4")
    public void postSCOL_BQ() {
    throws ParserConfigurationException,
                SAXException, XPathExpressionException, IOException, TransformerException {

            int code = Upload.upload("/getLoanContractOverdueListToWAY4", UploadFiles.CFT2RL,
                    LoanSystemCode.CFT2RL.getShortSystemCode().toLowerCase());
            Assert.assertEquals(code, HttpStatus.SC_OK);

            File etalonRqFile = new File(Objects.requireNonNull(this.getClass()
                    .getClassLoader()
                    .getResource("requests/get-payment-schedule-request.xml")).getFile());
            Document documentRq = getDocumentFromFile(etalonRqFile);
            documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='LoanSystemCode']",
                    LoanSystemCode.CFT2RL.getSystemCode());
            documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='AgreementNumber']",
                    ContractNumber.CFT2RL.getContractNumber());
            String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
            documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
                    "RqUID", rqUid);

            String response = given()
                    .spec(requestSpecification)
                    .baseUri(microservice.getBaseUri())
                    .header(new Header("SOAPAction", "GetPaymentSchedule"))
                    .body(getStringFromDocument(documentRq))
                    .when()
                    .post(microservice.getGetPaymentsScheduleEndpoint())
                    .then()
                    .spec(responseSpecification)
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .body()
                    .asString();

            assertCheck(response);
            //фактически проверяю значения из заглушки
            //CIT_REQUEST/DATA/Message/BEGIN_/PaymentScheduleList/BEGIN_/PaymentDate
            //CIT_REQUEST/DATA/Message/BEGIN_/PaymentScheduleList/BEGIN_/PaymentDateEnd
            //Если PaymentDateEnd отсутствует, то передать только значение тега PaymentDate и дату преобразовать в формат MM/DD/YYYY
            Assert.assertEquals(getNodeValue(getDocumentFromString(response),
                    ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/PaymentDate"), "08/06/2021");
            checksForPaymentDate(response);
        }
    }
}
