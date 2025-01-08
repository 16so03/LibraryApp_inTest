package com.libraryApp.step_definitions;

import com.github.javafaker.Faker;
import com.libraryApp.pages.LibraryPage;
import com.libraryApp.utilities.ConfigurationReader;
import com.libraryApp.utilities.DB_Util;
import com.libraryApp.utilities.Driver;
import com.libraryApp.utilities.LibraryUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


public class Library_Step {
    Faker faker = new Faker();
    RequestSpecification givenPart = RestAssured.given().log().params();
    Response response;
    JsonPath jsonPath;
    ValidatableResponse thenPart;
    String email;
    String password;
    String token;
    String paramValueExp = "";
    LibraryPage libraryPage = new LibraryPage();

    private String User_password;


    //=-=-=-=-=-SCENARIO 1-=-=-=-=-=-=-=-=-=-=-=-=-=
    @Given("I logged Library api as a librarian")
    public void i_logged_library_api_as_a_librarian() {

        RestAssured.given().log().uri()
                .header("x-library-token", token)
                .accept(ContentType.JSON)
                .when().get("/dashboard_stats")
                .then().statusCode(200);
    }

    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String string) {
        if (string.equals("librarian")) {
            email = ConfigurationReader.getProperty("librarian_email");
            password = ConfigurationReader.getProperty("librarian_password");
        }
        token = LibraryUtils.GetToken(email, password);
        givenPart.header("x-library-token", token)
                .accept(ContentType.JSON)
                .when().post("/login");
    }

    @Given("Accept header is {string}")
    public void accept_header_is(String acceptHeader) {
        givenPart.accept(acceptHeader);
    }

    @When("I send GET request to “\\/get_all_users” endpoint")
    public void i_send_get_request_to_get_all_users_endpoint() {
        response = givenPart.when().get("/get_all_users").prettyPeek();
        jsonPath = response.jsonPath();
        thenPart = response.then();

    }

    @Then("status code should be {int}")
    public void status_code_should_be(Integer SC) {
        thenPart.statusCode(SC);
    }

    @Then("Response Content type is {string}")
    public void response_content_type_is(String contentType) {
        thenPart.contentType(contentType);
    }

    @Then("{string} field should not be null")
    public void field_should_not_be_null(String field) {
        thenPart.body(field, notNullValue());
    }

    //=-=-=-=-=-SCENARIO 2 -=-=-=-=-=-=-=-=-=-=-==-=-
    @Given("Path param is {string}")
    public void path_param_should_be_and(String paramValue) {
        givenPart.pathParam("id", paramValue);
        paramValueExp = paramValue;

    }

    @When("I send GET request to {string} endpoint")
    public void i_send_get_request_to_endpoint(String endpoint) {
        response = givenPart.when().get(endpoint).prettyPeek();
        jsonPath = response.jsonPath();
        thenPart = response.then();

    }

    @Then("{string} field should be same with path param")
    public void field_should_be_same_with_path_param(String paramName) {
        thenPart.body(paramName, is(paramValueExp));

    }

    @Then("following fields should not be null")
    public void following_fields_should_not_be_null(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> parameters = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : parameters) {
            row.forEach((k, v) -> {
                assertThat(v, notNullValue());
            });

        }
    }

    //=-=-=-=-=-SCENARIO 3-=-=-=-=-=-=-=-=
    @Given("Request Content Type header is {string}")
    public void request_content_type_header_is(String string) {
        if (string.equals("application/x-www-form-urlencoded")) {
            givenPart.accept(ContentType.URLENC);
        }
    }

    @Given("I create a random {string} as request body")
    public void i_create_a_random_as_request_body(String something) {
        User_password = faker.internet().password();
        if (something.contains("book")) {
            givenPart.formParam("name", ConfigurationReader.getProperty("book_name"))
                    .formParam("isbn", ConfigurationReader.getProperty("isbn"))
                    .formParam("year", ConfigurationReader.getProperty("year"))
                    .formParam("author", ConfigurationReader.getProperty("author"))
                    .formParam("book_category_id", ConfigurationReader.getProperty("book_category_id"))
                    .formParam("description", ConfigurationReader.getProperty("description"));
        } else if (something.contains("user")) {
            givenPart.formParam("full_name", faker.name().fullName())
                    .formParam("email", faker.internet().emailAddress())
                    .formParam("password", User_password)
                    .formParam("user_group_id", "2")
                    .formParam("status", "Active")
                    .formParam("start_date", "2005-01-01")
                    .formParam("end_date", "2010-01-01")
                    .formParam("address", faker.address().streetAddress());
        }
    }

    @Given("I logged in Library UI as {string}")
    public void i_logged_in_library_ui_as(String role) {
        Driver.get().get(ConfigurationReader.getProperty("ui_url"));
        if (role.contains("librarian")) {
            libraryPage.libraryLogin(ConfigurationReader.getProperty("librarian_email"), ConfigurationReader.getProperty("librarian_password"));
        }
    }

    @Given("I navigate to {string} page")
    public void i_navigate_to_page(String navigatePage) {
        libraryPage.navigateTo(navigatePage);
    }

    @When("I send POST request to {string} endpoint")
    public void i_send_post_request_to_endpoint(String endpoint) {
        response = givenPart.when().post(endpoint).prettyPeek();
        jsonPath = response.jsonPath();
        thenPart = response.then();
    }

    @Then("the field value for {string} path should be equal to {string}")
    public void the_field_value_for_path_should_be_equal_to(String field, String message) {
        thenPart.body(field, is(equalTo(message)));
    }

    @Then("UI, Database and API created book information must match")
    public void ui_database_and_api_created_book_information_must_match() {
        int api_book_id = jsonPath.getInt("book_id");
        response = givenPart.accept(ContentType.JSON).when().get("/get_book_by_id/" + api_book_id);
        jsonPath = response.jsonPath();
        thenPart = response.then();
        String api_book_name = jsonPath.getString("name");
        //DB check
        DB_Util.createConnection();

        String query = "select name from books where id=" + api_book_id;
        DB_Util.runQuery(query);
        Map<String, String> datamap = DB_Util.getRowMap(1);
        String db_book_name = datamap.get("name");
        System.out.println("db_book_name = " + db_book_name);
        System.out.println("api_book_name = " + api_book_name);

        Assert.assertEquals(api_book_name, db_book_name);
        System.out.println("It does exist in DB");


        //UI check
        libraryPage.SearchForBook(api_book_name);
        WebElement ui_book_name = Driver.get().findElement(By.xpath("//td[.='" + api_book_name + "']"));
        Assert.assertEquals(api_book_name, ui_book_name.getText());

        Driver.closeDriver();

    }

    //Scenario 04
    @Then("“user_id\" field should not be null")
    public void user_id_field_should_not_be_null() {
        thenPart.body("user_id", notNullValue());
    }

    @Then("created user information should match with Database")
    public void created_user_information_should_match_with_database() {
        int user_id = jsonPath.getInt("user_id");
        response = givenPart.accept(ContentType.JSON).when().get("get_user_by_id/" + user_id);
        jsonPath = response.jsonPath();
        thenPart = response.then();

        DB_Util.createConnection();
        String query = "select* from users where id=" + user_id;
        DB_Util.runQuery(query);
        //Retrieve info from DB
        Map<String, String> datamap = DB_Util.getRowMap(1);
        String db_user_name = datamap.get("full_name");
        String db_user_email = datamap.get("email");
        String db_user_password = datamap.get("password");
        int db_user_id = jsonPath.getInt("user_group_id");
        String db_status = datamap.get("status");
        String db_start_date = datamap.get("start_date");
        String db_end_date = datamap.get("end_date");
        String db_address = datamap.get("address");

        //Retrieve info from api
        String api_User_name = jsonPath.getString("full_name");
        String user_email_api = jsonPath.getString("email");
        int api_user_id = jsonPath.getInt("user_group_id");
        String api_User_status = jsonPath.getString("status");
        String api_start_date = jsonPath.getString("start_date");
        String api_end_date = jsonPath.getString("end_date");
        String user_api_address = jsonPath.getString("address");

        Assert.assertEquals(api_User_name, db_user_name);
        Assert.assertEquals(user_email_api, db_user_email);
        Assert.assertEquals(api_user_id, db_user_id);
        Assert.assertEquals(api_User_status, db_status);
        Assert.assertEquals(api_start_date, db_start_date);
        Assert.assertEquals(api_end_date, db_end_date);
        Assert.assertEquals(user_api_address, db_address);


    }

    @Then("created user should be able to login Library UI")
    public void created_user_should_be_able_to_login_library_ui() {
        String user_email = jsonPath.getString("email");

        Driver.get().get(ConfigurationReader.getProperty("ui_url"));
        libraryPage.libraryLogin(user_email, User_password);

    }

    @Then("created user name should appear in Dashboard Page")
    public void created_user_name_should_appear_in_dashboard_page() {
        String api_user_name = jsonPath.getString("full_name");
        WebElement user_name_ui = Driver.get().findElement(By.xpath("//span[text()='" + api_user_name + "']"));
        Assert.assertTrue(user_name_ui.isDisplayed());

    }

//-=-=-=-=-SCENARIO 5-=-=-=-=-=-=-=-=-=-=-=-=-=-
@Given("I logged Library api with credentials {string} and {string}")
public void i_logged_library_api_with_credentials_and(String email, String password) {
    token = LibraryUtils.GetToken(email, password);
}
    @Given("I send token information as request body")
    public void i_send_token_information_as_request_body() {
        givenPart
                .formParam("token",token)
                .when().post("/decode")
                .then().statusCode(200);

    }

}
