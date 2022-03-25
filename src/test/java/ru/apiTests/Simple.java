package ru.apiTests;

public class Simple {
    //Пример использования pathParam ():

// Here the key name 'country' must match the url parameter {country}
            RestAssured.given()
            .pathParam("country", "Finland")
        .when()
            .get("http://restcountries.eu/rest/v1/name/{country}")
        .then()
            .body("capital", containsString("Helsinki"));
  //  Пример использования переменной:

    String cty = "Finland";

// Here the name of the variable have no relation with the URL parameter {country}
RestAssured.given()
        .when()
            .get("http://restcountries.eu/rest/v1/name/{country}", cty)
        .then()
            .body("capital", containsString("Helsinki"));
  //  Теперь, если вам нужно вызвать разные службы, вы также можете параметризовать "службу" следующим образом:

    // Search by name
    String val = "Finland";
    String svc = "name";

RestAssured.given()
        .when()
            .get("http://restcountries.eu/rest/v1/{service}/{value}", svc, val)
        .then()
            .body("capital", containsString("Helsinki"));


// Search by ISO code (alpha)
    val = "CH"
    svc = "alpha"

            RestAssured.given()
            .when()
            .get("http://restcountries.eu/rest/v1/{service}/{value}", svc, val)
        .then()
            .body("capital", containsString("Bern"));

// Search by phone intl code (callingcode)
    val = "359"
    svc = "callingcode"

            RestAssured.given()
            .when()
            .get("http://restcountries.eu/rest/v1/{service}/{value}", svc, val)
        .then()
            .body("capital", containsString("Sofia"));
}

   // сначала определить спецификацию запроса (скажем, в @Before ):

        RequestSpecification requestSpecification = new RequestSpecBuilder()
        .setBaseUri (BASE_URL)
        .setBasePath (BASE_PATH)
        .addPathParam(
        "pathParamName", "pathParamValue",
        "anotherPathParamName", "anotherPathParamValue")
        .addQueryParam(
        "queryparamName", "queryParamValue",
        "anotherQueryParamName", "anotherQueryParamValue")
        .setAuth(basic(USERNAME, PASSWORD))
        .setContentType(ContentType.JSON)
        .setAccept(ContentType.JSON)
        .log(LogDetail.ALL)
        .build();
       // а затем повторно использовать его несколько раз следующим образом:

        given()
        .spec(requestSpecification)
        .get("someResource/{pathParamName}/{anotherPathParamName}")
        .then()
        .statusCode(200);

        //Вы можете определенно использовать pathParam (String arg0, Object arg1) для достижения параметризации. Используйте приведенный ниже пример, если вы используете @DataProvider для предоставления данных.

      //  Таким образом, вы можете предоставить несколько данных с помощью DataProvider, а также можете использовать APache POI для получения данных из таблицы Excel.

@DataProvider(name="countryDataProvider")
public String[][] getCountryData(){
        String countryData[][] = {{"Finland"}, {"India"}, {"Greenland"}};
        return (countryData);
        }
@Test(dataProvider="countryDataProvider")
public void getCountryDetails(String countryName){
        given().
        pathParam("country", countryName).
        when().
        get("http://restcountries.eu/rest/v1/name/{country}").
        then().
        assertThat().
        .body("capital", containsString("Helsinki"));
        }
