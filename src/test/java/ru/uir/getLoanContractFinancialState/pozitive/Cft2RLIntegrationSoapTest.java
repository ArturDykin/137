package ru.uir.getLoanContractFinancialState.pozitive;

import io.restassured.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import ru.vtb.uip.getpaymentscheduletesting.tests.utils.BaseTest;
import ru.vtb.uip.getpaymentscheduletesting.tests.utils.CodeValue;
import ru.vtb.uip.getpaymentscheduletesting.tests.utils.Constants;
import ru.vtb.uip.getpaymentscheduletesting.tests.utils.ContractNumber;
import ru.vtb.uip.getpaymentscheduletesting.tests.utils.LoanSystemCode;
import ru.vtb.uip.getpaymentscheduletesting.utils.TestITTestListener;
import ru.vtb.uip.getpaymentscheduletesting.utils.Upload;
import ru.vtb.uip.getpaymentscheduletesting.utils.UploadFiles;
import ru.vtb.uip.msa03.testingframework.testit.annotations.TestITMethod;
import ru.vtb.uip.msa03.testingframework.testit.listeners.TestITSuiteListener;

import javax.jms.JMSException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static ru.vtb.uip.getpaymentscheduletesting.tests.utils.CommonMethods.getCountSymbols;
import static ru.vtb.uip.getpaymentscheduletesting.tests.utils.CommonMethods.getDocumentFromFile;
import static ru.vtb.uip.getpaymentscheduletesting.tests.utils.CommonMethods.getDocumentFromString;
import static ru.vtb.uip.getpaymentscheduletesting.tests.utils.CommonMethods.getDocumentWithUpdatedNodeAttribute;
import static ru.vtb.uip.getpaymentscheduletesting.tests.utils.CommonMethods.getDocumentWithUpdatedNodeValue;
import static ru.vtb.uip.getpaymentscheduletesting.tests.utils.CommonMethods.getDocumentWithoutNode;
import static ru.vtb.uip.getpaymentscheduletesting.tests.utils.CommonMethods.getDocumentWithoutNodeAttribute;
import static ru.vtb.uip.getpaymentscheduletesting.tests.utils.CommonMethods.getNodeValue;
import static ru.vtb.uip.getpaymentscheduletesting.tests.utils.CommonMethods.getRandomStringOfLength;
import static ru.vtb.uip.getpaymentscheduletesting.tests.utils.CommonMethods.getStringFromDocument;

@Listeners({TestITTestListener.class, TestITSuiteListener.class})
public class Cft2RLIntegrationSoapTest extends BaseTest {

    @Test(testName = "[Позитивный] CRM -> CFT2RL. Ответ от CFT2RL без тега PaymentDateEnd")
    @TestITMethod(testCaseGlobalId = "712759")
    public void responseWithoutPaymentDateEndTagSoapTestR()
        throws ParserConfigurationException,
        SAXException, XPathExpressionException, IOException, TransformerException {

        int code = Upload.upload("/cft2RlSuccessResponseWithoutPaymentDate.xml", UploadFiles.CFT2RL,
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

    @Test(testName = "[Позитивный] CRM -> CFT2RL. Ответ от CFT2RL с пустым тегом PaymentDateEnd")
    @TestITMethod(testCaseGlobalId = "712760")
    public void responseWithEmptyPaymentDateEndTagSoapTestR()
        throws ParserConfigurationException,
        SAXException, XPathExpressionException, IOException, TransformerException {

        int code = Upload.upload("/cft2RlSuccessResponseEmptyPaymentDate.xml", UploadFiles.CFT2RL,
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
        //Если PaymentDateEnd не заполнено, то передать только значение тега PaymentDate и дату преобразовать в формат MM/DD/YYYY
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/PaymentDate"), "08/06/2021");
        checksForPaymentDate(response);
    }


    @Test(testName = "[Позитивный] CRM -> CFT2RL. Ответ от CFT2RL с заполненным тегом PaymentDateEnd")
    @TestITMethod(testCaseGlobalId = "712761")
    public void responseWithFilledPaymentDateEndTagSoapTestR()
        throws ParserConfigurationException,
        SAXException, XPathExpressionException, IOException, TransformerException {

        int code = Upload.upload("/cft2RlSuccessResponsePaymentDate.xml", UploadFiles.CFT2RL,
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
        //Если PaymentDateEnd - произвести склейку дат в формате PaymentDate - PaymentDateEnd и date преобразовать в формат MM/DD/YYYY.
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/PaymentDate"), "08/06/2021-08/07/2021");
        checksForPaymentDate(response);
    }


    @Test(testName = "[Позитивный] CRM -> CFT2RL. Ответ от CFT2RL с заполненными тегами DateBegCalc и DateEndCalc")
    @TestITMethod(testCaseGlobalId = "712762")
    public void responseWithFilledDateCalcTagsSoapTestR()
        throws ParserConfigurationException,
        SAXException, XPathExpressionException, IOException, TransformerException {

        int code = Upload.upload("/cft2RlSuccessResponseWithFilledDateCalc.xml", UploadFiles.CFT2RL,
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
        //если заполнены теги DateBegCalc или DateEndCalc,то заполняем STTotal, STPrincipalAmount,
        //STAccruedInterestAmount, STPrincipalBalance
        //CIT_REQUEST/DATA/Message/BEGIN_/PaymentScheduleList/BEGIN_/TotalAmount --> STTotal
        //CIT_REQUEST/DATA/Message/BEGIN_/PaymentScheduleList/BEGIN_/PrincipalAmount --> STPrincipalAmount
        //CIT_REQUEST/DATA/Message/BEGIN_/PaymentScheduleList/BEGIN_/InterestAmount --> STAccruedInterestAmount
        //CIT_REQUEST/DATA/Message/BEGIN_/PaymentScheduleList/BEGIN_/OverdueAmount --> STPrincipalBalance
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/AgreementNumber"), "10");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/PaymentDate"), "05/05/2021");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/AmountPayment"), "");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/PrincipalDebt"), "");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/SumInterest"), "");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/AmountCommission"), "900.01");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/BalanceDebt"), "");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/Currency"), "RUB");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/PaymentsTotal"), "11");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/PrincipalPaymentTotal"), "55555.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/InterestPaymentsTotal"), "3115.19");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/FeeAndOtherPaymentsTotal"), "8888.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/InterestCalcPeriod"), "05.05.2021-09.09.2021");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/STTotal"), "9999.00");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/STPrincipalAmount"), "55555.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/STAccruedInterestAmount"), "7777.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/STSubsidizedInterestAmount"), "8888.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/STPrincipalBalance"), "6666.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/CTTotal"), "4444.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/CTPrincipalAmount"), "3333.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/CTAccruedInterestAmount"), "2222.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/CTPrincipalBalance"), "1111.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfDateGraph/DateGraph/Date"), "12/12/2020");
    }

    @Test(testName = "[Позитивный] CRM -> CFT2RL. Ответ от CFT2RL без тегов DateBegCalc и DateEndCalc")
    @TestITMethod(testCaseGlobalId = "712763")
    public void responseWithoutDateCalcTagsSoapTestR()
        throws ParserConfigurationException,
        SAXException, XPathExpressionException, IOException, TransformerException {

        int code = Upload.upload("/cft2RlSuccessResponseWithoutDateCalc.xml", UploadFiles.CFT2RL,
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
        //если отсутствуют теги DateBegCalc или DateEndCalc,то заполняем AmountPayment, PrincipalDebt,
        //SumInterest BalanceDebt
        //CIT_REQUEST/DATA/Message/BEGIN_/PaymentScheduleList/BEGIN_/TotalAmount --> AmountPayment
        //CIT_REQUEST/DATA/Message/BEGIN_/PaymentScheduleList/BEGIN_/PrincipalAmount --> PrincipalDebt
        //CIT_REQUEST/DATA/Message/BEGIN_/PaymentScheduleList/BEGIN_/InterestAmount --> SumInterest
        //CIT_REQUEST/DATA/Message/BEGIN_/PaymentScheduleList/BEGIN_/OverdueAmount --> BalanceDebt
        checksForDateCalc(response);
    }

    @Test(testName = "[Позитивный] CRM -> CFT2RL. Ответ от CFT2RL с путыми тегами DateBegCalc и DateEndCalc")
    @TestITMethod(testCaseGlobalId = "712764")
    public void responseWithEmptyDateCalcTagsSoapTestR()
        throws ParserConfigurationException,
        SAXException, XPathExpressionException, IOException, TransformerException {

        int code = Upload.upload("/cft2RlSuccessResponseWithEmptyDateCalc.xml", UploadFiles.CFT2RL,
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
        //если не заполнены теги DateBegCalc или DateEndCalc,то заполняем AmountPayment, PrincipalDebt,
        //SumInterest BalanceDebt
        //CIT_REQUEST/DATA/Message/BEGIN_/PaymentScheduleList/BEGIN_/TotalAmount --> AmountPayment
        //CIT_REQUEST/DATA/Message/BEGIN_/PaymentScheduleList/BEGIN_/PrincipalAmount --> PrincipalDebt
        //CIT_REQUEST/DATA/Message/BEGIN_/PaymentScheduleList/BEGIN_/InterestAmount --> SumInterest
        //CIT_REQUEST/DATA/Message/BEGIN_/PaymentScheduleList/BEGIN_/OverdueAmount --> BalanceDebt
        checksForDateCalc(response);
    }


    @DataProvider(name = "maxFieldLengthProvider", parallel = false)
    public Object[][] maxFieldLengthProviderMethod() {
        return new Object[][]{
            {"RqUID", 32, "712720"},
            {"RqType", 50, "712721"},
            {"RqTm", 30, "712722"},
            {"SystemFrom", 30, "712723"},
            {"ContextUserInfo", 50, "712724"},
            {"ContextId", 32, "712725"},
            {"SessionId", 32, "712726"},
            {"IPAddress", 30, "712727"},
        };
    }

    @Test(testName = "[Позитивный] CRM -> CFT2RL. Запрос с длиной поля N, равной максимальной",
        dataProvider = "maxFieldLengthProvider")
    @TestITMethod(testCaseGlobalIdParameterNumber = 2)
    public void oneFieldHasMaxLengthCFT2RLSoapTestR(String fieldName, int maxLength, String testCaseGlobalId)
        throws ParserConfigurationException,
        SAXException, XPathExpressionException, IOException, TransformerException, JMSException {

        Upload.upload("/cft2RlSuccessResponse.xml", UploadFiles.CFT2RL,
            LoanSystemCode.CFT2RL.getShortSystemCode().toLowerCase());

        File etalonRqFile = new File(Objects.requireNonNull(this.getClass()
            .getClassLoader()
            .getResource("requests/get-payment-schedule-request.xml")).getFile());
        Document documentRq = getDocumentFromFile(etalonRqFile);
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='LoanSystemCode']",
            LoanSystemCode.CFT2RL.getSystemCode());
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='AgreementNumber']",
            ContractNumber.CFT2RL.getContractNumber());
        if (fieldName.equals("RqUID")) {
            String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
            documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
                fieldName, rqUid + "1");
        } else {
            String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
            documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
                "RqUID", rqUid);
            documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
                fieldName, getRandomStringOfLength(maxLength));
        }

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
        checkNumberIsPresentInField(response);
    }

    @DataProvider(name = "minFieldLengthProvider", parallel = false)
    public Object[][] minFieldLengthProviderMethod() {
        return new Object[][]{
            {"RqUID", "712728"},
            {"RqType", "712729"},
            {"RqTm", "712730"},
            {"SystemFrom", "712731"},
            {"ContextUserInfo", "712732"},
            {"ContextId", "712733"},
            {"SessionId", "712734"},
            {"IPAddress", "712735"},
        };
    }

    @Test(testName = "[Позитивный] CRM -> CFT2RL. Запрос с длиной поля N, равной минимальной",
        dataProvider = "minFieldLengthProvider")
    @TestITMethod(testCaseGlobalIdParameterNumber = 1)
    public void oneFieldHasMinLengthCFT2RLSoapTestR(String fieldName, String testCaseGlobalId)
        throws ParserConfigurationException,
        SAXException, XPathExpressionException, IOException, TransformerException {

        Upload.upload("/cft2RlSuccessResponse.xml", UploadFiles.CFT2RL,
            LoanSystemCode.CFT2RL.getShortSystemCode().toLowerCase());

        File etalonRqFile = new File(Objects.requireNonNull(this.getClass()
            .getClassLoader()
            .getResource("requests/get-payment-schedule-request.xml")).getFile());
        Document documentRq = getDocumentFromFile(etalonRqFile);

        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='LoanSystemCode']",
            LoanSystemCode.CFT2RL.getSystemCode());
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='AgreementNumber']",
            ContractNumber.CFT2RL.getContractNumber());
        if (fieldName.equals("RqUID")) {
            String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
            documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
                fieldName, rqUid + "1");
        } else {
            String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
            documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
                "RqUID", rqUid);
            documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
                fieldName, getRandomStringOfLength(1));
        }

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
        checkNumberIsPresentInField(response);
        Assert.assertTrue(getCountSymbols(response, ".//GetPaymentSchedule_Output/HeaderInfo/@SystemTo") < 31);
        Assert.assertTrue(getCountSymbols(response, ".//GetPaymentSchedule_Output/HeaderInfo/@SystemFrom") < 31);
        Assert.assertTrue(getCountSymbols(response, ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorCode") < 31);
        Assert.assertTrue(getCountSymbols(response, ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorText") < 2001);
        Assert.assertTrue(getCountSymbols(response, ".//GetPaymentSchedule_Output/HeaderInfo/@RqType") < 51);
        Assert.assertTrue(getCountSymbols(response, ".//GetPaymentSchedule_Output/HeaderInfo/@RqTm") < 31);
        Assert.assertTrue(getCountSymbols(response, ".//GetPaymentSchedule_Output/HeaderInfo/@RqID") < 33);
        //фактически проверяю значения из заглушки
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
                ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule[1]/PrincipalDebt"),
            "55555.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
                ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule[1]/AgreementNumber"),
            "1");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
                ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule[1]/PaymentDate"),
            "03/04/2021");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
                ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule[1]/AmountPayment"),
            "6000.00");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
                ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule[1]/SumInterest"),
            "3115.19");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
                ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule[1]/AmountCommission"),
            "900.01");
    }


    @Test(testName = "[Негативный] CRM -> CFT2RL. 500. Запрос  со списками ListOfGetPaymentSchedule")
    @TestITMethod(testCaseGlobalId = "712736")
    public void listOfGetPaymentScheduleToCFT2RLSoapTestR() throws TransformerException, IOException, ParserConfigurationException,
        SAXException, XPathExpressionException {

        Upload.upload("/cft2RlListSuccessResponse.xml", UploadFiles.CFT2RL,
            LoanSystemCode.CFT2RL.getShortSystemCode().toLowerCase());

        File etalonRqFile = new File(Objects.requireNonNull(this.getClass()
            .getClassLoader()
            .getResource("requests/list-of-get-payment-schedule-request.xml")).getFile());
        Document documentRq = getDocumentFromFile(etalonRqFile);

        String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
        documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            "RqUID", rqUid);
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='LoanSystemCode']",
            LoanSystemCode.CFT2RL.getSystemCode());
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='AgreementNumber']",
            ContractNumber.CFT2RL.getContractNumber());

        String response = given()
            .spec(requestSpecification)
            .baseUri(microservice.getBaseUri())
            .header(new Header("SOAPAction", "GetPaymentSchedule"))
            .body(getStringFromDocument(documentRq))
            .when()
            .post(microservice.getGetPaymentsScheduleEndpoint())
            .then()
            .spec(responseSpecification)
            .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            .extract()
            .body()
            .asString();

        Assert.assertNotNull(response, "Response is empty");
        Document documentRs = getDocumentFromString(response);
        Assert.assertTrue(getNodeValue(documentRs,
            ".//Envelope/Body/Fault/faultcode/text()").contains("soap:Client"));
        Assert.assertTrue(getNodeValue(documentRs, ".//Envelope/Body/Fault/faultstring/text()")
            .contains("Unmarshalling Error: cvc-complex-type.2.4.d: Invalid content was found starting with element " +
                "'ns2:GetPaymentSchedule'. No child element is expected at this point."));
    }


    @Test(testName = "[Негативный] CRM -> CFT2RL. ULBS-012. Запрос без необязательного " +
        "элемента ListOfGetPaymentSchedule")
    @TestITMethod(testCaseGlobalId = "712737")
    public void oneOptionalFieldIsAbsentCFT2RLSoapTestR() throws TransformerException,
        IOException, ParserConfigurationException,
        SAXException, XPathExpressionException {

        Upload.upload("/cft2RlSuccessResponse.xml", UploadFiles.CFT2RL,
            LoanSystemCode.CFT2RL.getShortSystemCode().toLowerCase());

        File etalonRqFile = new File(Objects.requireNonNull(this.getClass()
            .getClassLoader()
            .getResource("requests/get-payment-schedule-request.xml")).getFile());
        Document documentRq = getDocumentFromFile(etalonRqFile);
        String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
        documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            "RqUID", rqUid);
        documentRq = getDocumentWithoutNode(documentRq, String.format(".//*[local-name()='%s']",
            "ListOfGetPaymentSchedule"));

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

        Assert.assertNotNull(response, "Response is empty");
        Document documentRs = getDocumentFromString(response);
        Assert.assertEquals(getNodeValue(documentRs,
                ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorCode"),
            CodeValue.ULBS_012.getValue());
        Assert.assertEquals(getNodeValue(documentRs,
                ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorText"),
            "Не указан номер кредитного договора");
    }

    @DataProvider(name = "attributesProvider")
    public Object[][] attributesProviderMethod() {
        return new Object[][]{
            {"ContextUserInfo", "712738"},
            {"ContextId", "712739"},
            {"SessionId", "712740"},
            {"IPAddress", "712741"},
        };
    }

    @Test(testName = "[Позитивный] CRM -> CFT2RL. Запрос без необязательного элемента N в HeaderInfo",
        dataProvider = "attributesProvider")
    @TestITMethod(testCaseGlobalIdParameterNumber = 1)
    public void oneNotRequiredAttributeIsAbsentProfileSoapTestR(String requiredAttribute, String testCaseGlobalId)
        throws ParserConfigurationException,
        SAXException, XPathExpressionException, IOException, TransformerException {

        Upload.upload("/cft2RlSuccessResponse.xml", UploadFiles.CFT2RL,
            LoanSystemCode.CFT2RL.getShortSystemCode().toLowerCase());

        File etalonRqFile = new File(Objects.requireNonNull(this.getClass()
            .getClassLoader()
            .getResource("requests/get-payment-schedule-request.xml")).getFile());
        Document documentRq = getDocumentFromFile(etalonRqFile);
        String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
        documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            "RqUID", rqUid);
        documentRq = getDocumentWithoutNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            requiredAttribute);
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='LoanSystemCode']",
            LoanSystemCode.CFT2RL.getSystemCode());
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='AgreementNumber']",
            ContractNumber.CFT2RL.getContractNumber());

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
        checkNumberIsPresentInField(response);
    }


    @DataProvider(name = "requiredAttributesProvider")
    public Object[][] requiredAttributesProviderMethod() {
        return new Object[][]{
            {"RqUID", "712742"},
            {"RqType", "712743"},
            {"RqTm", "712744"},
            {"SystemFrom", "712745"},
            {"ContextUserInfo", "712746"},
            {"ContextId", "712747"},
            {"SessionId", "712748"},
            {"IPAddress", "712749"},
        };
    }

    //minLength не указано
    @Test(testName = "[Позитивный] CRM -> CFT2RL. Запрос с пустым значением атрибута N",
        dataProvider = "requiredAttributesProvider")
    @TestITMethod(testCaseGlobalIdParameterNumber = 1)
    public void oneRequiredAttributeValueIsAbsentCFT2RLSoapTestR(String requiredAttribute, String testCaseGlobalId)
        throws ParserConfigurationException, SAXException, XPathExpressionException, IOException, TransformerException {

        Upload.upload("/cft2RlSuccessResponse.xml", UploadFiles.CFT2RL,
            LoanSystemCode.CFT2RL.getShortSystemCode().toLowerCase());

        File etalonRqFile = new File(Objects.requireNonNull(this.getClass()
            .getClassLoader()
            .getResource("requests/get-payment-schedule-request.xml")).getFile());
        Document documentRq = getDocumentFromFile(etalonRqFile);
        String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
        documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            "RqUID", rqUid);
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='LoanSystemCode']",
            LoanSystemCode.CFT2RL.getSystemCode());
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='AgreementNumber']",
            ContractNumber.CFT2RL.getContractNumber());
        documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            requiredAttribute, "");

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
        checkNumberIsPresentInField(response);
    }


    @Test(testName = "[Негативный] CRM -> CFT2RL. ULBS-012. Договор не найден")
    @TestITMethod(testCaseGlobalId = "712750")
    public void notExistContractResponseCFTSoapTestR() throws ParserConfigurationException, SAXException,
        XPathExpressionException, IOException, TransformerException {

        Upload.upload("/cft2RlErrorResponse.xml", UploadFiles.CFT2RL,
            LoanSystemCode.CFT2RL.getShortSystemCode().toLowerCase());

        File etalonRqFile = new File(Objects.requireNonNull(this.getClass()
            .getClassLoader()
            .getResource("requests/get-payment-schedule-request.xml")).getFile());
        Document documentRq = getDocumentFromFile(etalonRqFile);

        String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
        documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            "RqUID", rqUid);
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='LoanSystemCode']",
            LoanSystemCode.CFT2RL.getSystemCode());
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='AgreementNumber']",
            "623000/0100");

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

        Assert.assertNotNull(response, "Response is empty");
        Document documentRs = getDocumentFromString(response);
        Assert.assertEquals(getNodeValue(documentRs,
                ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorCode"),
            CodeValue.ULBS_012.getValue());
        Assert.assertEquals(getNodeValue(documentRs,
                ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorText"),
            "[CFT2RL] Код ошибки: 678, Текст ошибки: не найден номер кредитного договора");
    }

    @Test(testName = "[Негативный] CRM -> CFT2RL.ULBS-003. Превышен таймаут ожидания")
    @TestITMethod(testCaseGlobalId = "712751")
    public void timeoutExceededCFT2RLSoapTestR() throws ParserConfigurationException,
        SAXException, XPathExpressionException, IOException, TransformerException {

        //set delay for emulating provider unavailable
        Upload.setDelay(65000L,
            LoanSystemCode.CFT2RL.getShortSystemCode().toLowerCase());

        File etalonRqFile = new File(Objects.requireNonNull(this.getClass()
            .getClassLoader()
            .getResource("requests/get-payment-schedule-request.xml")).getFile());
        Document documentRq = getDocumentFromFile(etalonRqFile);

        String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
        documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            "RqUID", rqUid);
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='LoanSystemCode']",
            LoanSystemCode.CFT2RL.getSystemCode());
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='AgreementNumber']",
            ContractNumber.CFT2RL.getContractNumber());

        String response = given()
            .spec(requestSpecification)
            .baseUri(microservice.getBaseUri())
            .header(new Header("SOAPAction", "GetPaymentSchedule"))
            .log().all()
            .body(getStringFromDocument(documentRq))
            .when()
            .post(microservice.getGetPaymentsScheduleEndpoint())
            .then()
            .statusCode(HttpStatus.SC_OK)
            .log().all()
            .extract()
            .body()
            .asString();

        Assert.assertNotNull(response, "Response is empty");
        Document documentRs = getDocumentFromString(response);

        Assert.assertEquals(getNodeValue(documentRs,
                ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorCode"),
            CodeValue.ULBS_003.getValue());
        Assert.assertTrue(getNodeValue(documentRs,
            ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorText")
            .contains("." + rqUid));
    }

    @Test(testName = "[Негативный] CRM -> CFT2RL. ULBS-003. Запрос со значением в формате строки в CreateDate")
    @TestITMethod(testCaseGlobalId = "712752")
    public void dateInStringFormatToCFT2RLSoapTestR() throws TransformerException, IOException,
        ParserConfigurationException,
        SAXException, XPathExpressionException {

        Upload.upload("/cft2RlSuccessResponse.xml", UploadFiles.CFT2RL,
            LoanSystemCode.CFT2RL.getShortSystemCode().toLowerCase());

        File etalonRqFile = new File(Objects.requireNonNull(this.getClass()
            .getClassLoader()
            .getResource("requests/get-payment-schedule-request.xml")).getFile());
        Document documentRq = getDocumentFromFile(etalonRqFile);

        String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
        documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            "RqUID", rqUid);
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='LoanSystemCode']",
            LoanSystemCode.CFT2RL.getSystemCode());
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='AgreementNumber']",
            ContractNumber.CFT2RL.getContractNumber());
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='CreateDate']",
            rqUid);

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

        Assert.assertNotNull(response, "Response is empty");
        Document documentRs = getDocumentFromString(response);
        Assert.assertEquals(getNodeValue(documentRs,
                ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorCode"),
            CodeValue.ULBS_003.getValue());
        Assert.assertTrue(getNodeValue(documentRs, ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorText")
            .contains(String.format("Unparseable date: \"%s\"", rqUid)));
        Assert.assertTrue(getNodeValue(documentRs,
            ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorText").contains("." + rqUid));
    }

    @Test(testName = "[Негативный] CRM -> CFT2RL. ULBS-003. Запрос с датой в неверном формате")
    @TestITMethod(testCaseGlobalId = "712753")
    public void dateInWrongFormatToCFT2RLSoapTestR() throws TransformerException, IOException,
        ParserConfigurationException,
        SAXException, XPathExpressionException {

        Upload.upload("/cft2RlSuccessResponse.xml", UploadFiles.CFT2RL,
            LoanSystemCode.CFT2RL.getShortSystemCode().toLowerCase());

        File etalonRqFile = new File(Objects.requireNonNull(this.getClass()
            .getClassLoader()
            .getResource("requests/get-payment-schedule-request.xml")).getFile());
        Document documentRq = getDocumentFromFile(etalonRqFile);

        String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
        documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            "RqUID", rqUid);
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='LoanSystemCode']",
            LoanSystemCode.CFT2RL.getSystemCode());
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='AgreementNumber']",
            ContractNumber.CFT2RL.getContractNumber());
//        ММ/DD/YYYY HH24:MI:SS - right format
        String dateValue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='CreateDate']",
            dateValue);

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

        Assert.assertNotNull(response, "Response is empty");
        Document documentRs = getDocumentFromString(response);
        Assert.assertEquals(getNodeValue(documentRs,
                ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorCode"),
            CodeValue.ULBS_003.getValue());
        Assert.assertTrue(getNodeValue(documentRs, ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorText")
            .contains(String.format("Unparseable date: \"%s\"", dateValue)));
        Assert.assertTrue(getNodeValue(documentRs,
            ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorText").contains("." + rqUid));
    }

    @Test(testName = "[Позитивный] CRM -> CFT2RL. Минимально возможное число обязательных полей")
    @TestITMethod(testCaseGlobalId = "712754")
    public void minNumberOfFieldsFilledCft2RlSoapTestR() throws IOException, ParserConfigurationException,
        SAXException, XPathExpressionException, TransformerException {

        Upload.upload("/cft2RlSuccessResponse.xml", UploadFiles.CFT2RL,
            LoanSystemCode.CFT2RL.getShortSystemCode().toLowerCase());

        File etalonRqFile = new File(Objects.requireNonNull(this.getClass()
            .getClassLoader()
            .getResource("requests/get-payment-schedule-request.xml")).getFile());
        Document documentRq = getDocumentFromFile(etalonRqFile);
        String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
        documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            "RqUID", rqUid);
        documentRq = getDocumentWithoutNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            "ContextUserInfo");
        documentRq = getDocumentWithoutNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            "ContextId");
        documentRq = getDocumentWithoutNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            "SessionId");
        documentRq = getDocumentWithoutNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            "IpAddress");
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='LoanSystemCode']",
            LoanSystemCode.CFT2RL.getSystemCode());
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='AgreementNumber']",
            ContractNumber.CFT2RL.getContractNumber());

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
        checkNumberIsPresentInField(response);
    }

    @DataProvider(name = "requiredElementsProvider")
    public Object[][] requiredElementsProviderMethod() {
        return new Object[][]{
            {"DivisionID", "712755"},
            {"CreateDate", "712756"},
            {"AgreementType", "712757"},
        };
    }

    @Test(testName = "[Позитивный] CRM -> CFT2RL. Запрос с пустым значением элемента N",
        dataProvider = "requiredElementsProvider")
    @TestITMethod(testCaseGlobalIdParameterNumber = 1)
    public void elementValueIsEmptyCFT2RLSoapTestR(String requiredElement, String testCaseGlobalId)
        throws ParserConfigurationException,
        SAXException, XPathExpressionException, IOException, TransformerException {

        Upload.upload("/cft2RlSuccessResponse.xml", UploadFiles.CFT2RL,
            LoanSystemCode.CFT2RL.getShortSystemCode().toLowerCase());
        File etalonRqFile = new File(Objects.requireNonNull(this.getClass()
            .getClassLoader()
            .getResource("requests/get-payment-schedule-request.xml")).getFile());

        Document documentRq = getDocumentFromFile(etalonRqFile);
        String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
        documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            "RqUID", rqUid);
        if (!requiredElement.equals("LoanSystemCode")) {
            documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='LoanSystemCode']",
                LoanSystemCode.CFT2RL.getSystemCode());
        }
        if (!requiredElement.equals("AgreementNumber")) {
            documentRq = getDocumentWithUpdatedNodeValue(documentRq, ".//*[local-name()='AgreementNumber']",
                ContractNumber.CFT2RL.getContractNumber());
        }
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, String.format(".//*[local-name()='%s']", requiredElement),
            "");

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
        checkNumberIsPresentInField(response);
    }

    @Test(testName = "[Негативный] CRM -> CFT2RL. ULBS_012. Запрос с пустым значением элемента AgreementNumber")
    @TestITMethod(testCaseGlobalId = "712758")
    public void agreementNumberIsEmptyCFT2RLSoapTestR()
        throws ParserConfigurationException,
        SAXException, XPathExpressionException, IOException, TransformerException {

        int code = Upload.upload("/cft2RlEmptyAgreementNumberErrorResponse.xml", UploadFiles.CFT2RL,
            LoanSystemCode.CFT2RL.getShortSystemCode().toLowerCase());

        File etalonRqFile = new File(Objects.requireNonNull(this.getClass()
            .getClassLoader()
            .getResource("requests/get-payment-schedule-request.xml")).getFile());

        Document documentRq = getDocumentFromFile(etalonRqFile);
        String rqUid = UUID.randomUUID().toString().replaceAll("-", "");
        documentRq = getDocumentWithUpdatedNodeAttribute(documentRq, ".//*[local-name()='HeaderInfo']",
            "RqUID", rqUid);
        documentRq = getDocumentWithUpdatedNodeValue(documentRq, String.format(".//*[local-name()='%s']", "AgreementNumber"),
            "");

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

        Assert.assertNotNull(response, "Response is empty");
        Document documentRs = getDocumentFromString(response);
        Assert.assertEquals(getNodeValue(documentRs,
                ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorCode"),
            CodeValue.ULBS_012.getValue());
        Assert.assertTrue(getNodeValue(documentRs,
            ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorText")
            .contains("101 непредвиденная ошибка"));
    }


    public void assertCheck(String response) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        Assert.assertNotNull(response, "Response is empty");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorCode"), "00");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/HeaderInfo/@ErrorText"), "");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/HeaderInfo/@RqType"), "GetPaymentScheduleRs_Input");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/HeaderInfo/@SystemFrom"), "CFT2RL");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response), ".//GetPaymentSchedule_Output/HeaderInfo/@RqUID"),
            getNodeValue(getDocumentFromString(response),
                ".//GetPaymentSchedule_Output/HeaderInfo/@CorrelationUID"));
    }

    public void checkNumberIsPresentInField(String response) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        Assert.assertTrue(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule[1]/AgreementNumber")
            .matches(Constants.NUMBER_MATCHER));
        Assert.assertTrue(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule[1]/AmountPayment")
            .matches(Constants.NUMBER_MATCHER));
        Assert.assertTrue(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule[1]/PrincipalDebt")
            .matches(Constants.NUMBER_MATCHER));
        Assert.assertTrue(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule[1]/SumInterest")
            .matches(Constants.NUMBER_MATCHER));
        Assert.assertTrue(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule[1]/AmountCommission")
            .matches(Constants.NUMBER_MATCHER));
        Assert.assertTrue(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule[1]/BalanceDebt")
            .matches(Constants.NUMBER_MATCHER));
        Assert.assertTrue(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule[1]/Currency")
            .matches("^[a-zA-Z]{3}$"));
        Assert.assertTrue(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule[1]/PaymentDate")
            .matches("^([0-3]?[0-9].[0-3]?[0-9].(?:[0-9]{2})?[0-9]{2}-[0-3]?[0-9].[0-3]?[0-9].(?:[0-9]{2})?[0-9]{2})|([0-3]?[0-9].[0-3]?[0-9].(?:[0-9]{2})?[0-9]{2})$"));
    }

    public void checksForPaymentDate(String response) throws ParserConfigurationException,
        IOException, SAXException, XPathExpressionException {
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/AgreementNumber"), "10");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/AmountPayment"), "");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/PrincipalDebt"), "");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/SumInterest"), "");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/AmountCommission"), "900.01");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/BalanceDebt"), "");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/Currency"), "RUB");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/PaymentsTotal"), "11");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/PrincipalPaymentTotal"), "55555.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/InterestPaymentsTotal"), "3115.19");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/FeeAndOtherPaymentsTotal"), "8888.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/InterestCalcPeriod"), "05.05.2021-09.09.2021");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/STTotal"), "9999.00");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/STPrincipalAmount"), "55555.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/STAccruedInterestAmount"), "7777.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/STSubsidizedInterestAmount"), "8888.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/STPrincipalBalance"), "6666.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/CTTotal"), "4444.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/CTPrincipalAmount"), "3333.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/CTAccruedInterestAmount"), "2222.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/CTPrincipalBalance"), "1111.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfDateGraph/DateGraph/Date"), "12/12/2020");
    }

    public void checksForDateCalc(String response) throws ParserConfigurationException,
        IOException, SAXException, XPathExpressionException {
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/AgreementNumber"), "10");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/PaymentDate"), "05/05/2021");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/AmountPayment"), "9999.00");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/PrincipalDebt"), "55555.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/SumInterest"), "7777.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/AmountCommission"), "900.01");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/BalanceDebt"), "6666.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/Currency"), "RUB");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/PaymentsTotal"), "11");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/PrincipalPaymentTotal"), "55555.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/InterestPaymentsTotal"), "3115.19");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/FeeAndOtherPaymentsTotal"), "8888.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/InterestCalcPeriod"), "");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/STTotal"), "");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/STPrincipalAmount"), "");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/STAccruedInterestAmount"), "");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/STSubsidizedInterestAmount"), "8888.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/STPrincipalBalance"), "");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/CTTotal"), "4444.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/CTPrincipalAmount"), "3333.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/CTAccruedInterestAmount"), "2222.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfGetPaymentSchedule/GetPaymentSchedule/CTPrincipalBalance"), "1111.55");
        Assert.assertEquals(getNodeValue(getDocumentFromString(response),
            ".//GetPaymentSchedule_Output/ListOfDateGraph/DateGraph/Date"), "12/12/2020");
    }

    @AfterTest
    public void resetCft2Rl() throws IOException {
        HttpGet reset = new HttpGet(microservice.getGetPaymentStubUri() + "/cft/reset");
        CloseableHttpResponse resp = HttpClients.createDefault().execute(reset);
        Assert.assertEquals(HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());

        HttpGet httpGet = new HttpGet(microservice.getGetPaymentStubUri() + "/cft/resetDelay");
        CloseableHttpResponse response = HttpClients.createDefault().execute(httpGet);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }
}
