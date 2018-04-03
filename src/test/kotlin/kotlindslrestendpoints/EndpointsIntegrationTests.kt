package kotlindslrestendpoints

import io.restassured.RestAssured
import io.restassured.http.ContentType.JSON
import io.restassured.response.ValidatableResponse
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItems
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class EndpointsIntegrationTests {

    val http = RestAssured
            .given()
            .log()
            .all()
            .port(8080)
            .accept(JSON)
            .contentType(JSON)

    @Test
    fun createNewOrder() {
        http.body(order("PIZZA FOR LAUNCH"))
                .post("/api/orders")
                .then()
                .statusCode(OK.value())
                .body("detail", `is`("PIZZA FOR LAUNCH"))
    }

    @Test
    fun getOrders() {
        this.orderInitializer("PIZZA", "MEAT")
        http.get("/api/orders")
                .then()
                .statusCode(OK.value())
                .body("detail", hasItems("PIZZA", "MEAT"))
    }

    @Test
    fun getOrdersById() {
        val orderIdentifiers = this.orderInitializer("PIZZA", "MEAT")
        getOrderById(orderIdentifiers[0]).statusCode(OK.value())
                .body("detail", `is`("PIZZA"))
        getOrderById(orderIdentifiers[1]).statusCode(OK.value())
                .body("detail", `is`("MEAT"))
    }

    @Test
    fun updateOrdersById() {
        val orderIdentifiers = this.orderInitializer("PIZZA")
        http.body(order("egg omelette"))
                .put("/api/orders/${orderIdentifiers[0]}")
                .then().statusCode(OK.value())

        getOrderById(orderIdentifiers[0])
                .statusCode(OK.value()).body("detail", `is`("egg omelette"))
    }

    @Test
    fun deleteOrderById() {
        val orderIdentifiers = this.orderInitializer("PIZZA")
        http
                .delete("/api/orders/${orderIdentifiers[0]}")
                .then().statusCode(OK.value())

        getOrderById(orderIdentifiers[0])
                .statusCode(NOT_FOUND.value())
    }

    private fun getOrderById(id: Int): ValidatableResponse = http.get("/api/orders/$id").then()

    private fun orderInitializer(vararg detail: String): List<Int> =
            detail.map {
                http.body(order(it)).post("/api/orders").then().extract().path<Int>("id")
            }


    private fun order(detail: String): String {
        return "{\"detail\": \"$detail\"}"
    }

}
