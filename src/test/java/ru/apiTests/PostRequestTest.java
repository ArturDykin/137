package ru.apiTests;

import io.restassured.RestAssured;
import org.apache.hc.core5.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.model.UserDTO;
import ru.specifications.Specifications;

public class PostRequestTest {

    @Test
    @DisplayName("Тестирование тестового запроса Post с проверкой status code = 201")
    public void postRequestCheckStatusCode() {
        RestAssured.given()
                .spec(Specifications.requestSpecification())//---> Указание RequestSpecification для формирования request
                .body(new UserDTO("morpheus", "leader"))//---> body для запроса с методом POST
                .post("/api/users")//---> Endpoint для выполнения запроса GET
                .then()
                .statusCode(HttpStatus.SC_CREATED);//---> Проверка статус код
    }

    @Test
    @DisplayName("Тестирование тестового запроса Post c проверкой key/value по полям name, job")
    public void postRequestCheckResponseJsonBody() {
        RestAssured.given()
                .spec(Specifications.requestSpecification())//---> Указание RequestSpecification для формирования request
                .body(new UserDTO("morpheus", "leader"))//---> body для запроса с методом POST
                .post("/api/users")//---> Endpoint для выполнения запроса GET
                .then()
                .statusCode(HttpStatus.SC_CREATED)//---> Проверка статус код
                .assertThat()
                .body("name", Matchers.is("morpheus"))//---> Проверка Body по key и value в json
                .body("job", Matchers.is("leader"));//---> Проверка Body по key и value в json
    }

}
