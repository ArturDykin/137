package ru.tests.rest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import static io.restassured.RestAssured.*;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.is;

public class Get {

    @Test
    public void postmanFirstGetTest(){
        RestAssured.
                when().get("https://postman-echo.com/get?foo1=bar1&foo2=bar2").
                then().assertThat().statusCode(200).
                and().body("args.foo2", is("bar2"));
    }

    @Test
    public void getRequestExampleTest() throws JSONException {
        Response response = get("http://restcountries.eu/rest/v1/name/russia");
        JSONArray jsonResponse = new JSONArray(response.asString());
        String capital = jsonResponse.getJSONObject(0).getString("capital");
        Assert.assertEquals(capital, "Moscow");
    }

    @Test
    public void test() {
        given()
                .get("http://restcountries.eu/rest/v1/name/russia")
                .then()
                .body("[0].capital", Matchers.equalTo("Moscow"));
    }
}