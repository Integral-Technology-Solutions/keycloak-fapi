package au.com.integraltech.keycloakfapitestrunner;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
class FapiConformanceTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(FapiConformanceTest.class);

	private static final String BASE_PATH = "https://localhost:8443/api";
	private static final String PLAN_NAME = "fapi-rw-id2-client-test-plan";
	private static final String VARIANT = "{client_auth_type:'private_key_jwt',fapi_profile:'plain_fapi'}";

    // Parameters method - Get test plan and filter into list of test params (test plan id, test module name)
    private static Collection getTestModules() throws IOException {
		LOGGER.info(String.format("Getting test plan. Plan name: %s, Variant: %s", PLAN_NAME, VARIANT));
		Response planResponse = getTestPlan(PLAN_NAME, VARIANT,
				new ClassPathResource("/fapi-rw-id2-with-private-key-RS256-PS256.json").getFile());
		String testPlanId = planResponse.jsonPath().getString("id");
		List<String> moduleNames = planResponse.jsonPath().getList("modules.testModule");
		List<Object[]> paramsList = new ArrayList<>();
        moduleNames.forEach(moduleName -> paramsList.add(new Object[] {testPlanId, moduleName}));
    	return paramsList;
	}

	// Parameterized test to be run for each test module in the test plan - test name is the test module name
	@ParameterizedTest(name = "{1}")
	@MethodSource("getTestModules")
    void test(String testPlanId, String testModuleName) throws JSONException {
    	LOGGER.info(String.format("Running test module %s - test plan id: %s", testModuleName, testPlanId));
		Response runTestResponse = runTest(testPlanId, testModuleName, VARIANT);
		LOGGER.info("Getting " + testModuleName + " test log results");
		Response testLogResponse = getTestLog(runTestResponse.jsonPath().getString("id"));
		processResults(testModuleName, testLogResponse);
	}

	// HELPER METHODS
	private static Response getTestPlan(String planName, String variant, File configFile) {
		return given().baseUri(BASE_PATH).relaxedHTTPSValidation().log().all()
				.body(configFile)
				.queryParam("planName", planName)
				.queryParam("variant", variant)
				.headers(getHeaders())
			.when()
				.post("/plan")
			.then()
				.log().all()
				.statusCode(201)
				.contentType(ContentType.JSON)
				.body("id", notNullValue(),
						"modules", notNullValue())
				.extract().response();
	}

	private Response runTest(String testPlanId, String testModuleName, String variant) {
		return given().baseUri(BASE_PATH).relaxedHTTPSValidation().log().all()
				.headers(getHeaders())
				.queryParam("test", testModuleName)
				.queryParam("plan", testPlanId)
				.queryParam("variant", variant)
			.when()
				.post("/runner")
			.then()
				.log().all()
				.statusCode(201)
				.contentType(ContentType.JSON)
				.body("name", Matchers.equalToCompressingWhiteSpace(testModuleName),
						"id", notNullValue())
				.extract().response();
	}

	private Response getTestLog(String testId) {
		return given().baseUri(BASE_PATH).relaxedHTTPSValidation()
				.log().all()
				.headers(getHeaders())
			.when()
				.get("/log/{testId}", testId)
			.then()
				.log().all()
				.statusCode(200)
				.contentType(ContentType.JSON)
				.body("$.", notNullValue())
				.extract().response();
	}

	private static Headers getHeaders() {
		return new Headers(new Header("Content-Type", "application/json"));
	}

	// expecting testLogResponse body to be a JSON array
	private void processResults(String testModuleName, Response testLogResponse) throws JSONException {
		List<JSONObject> resultsList = new ArrayList<>();
		List<JSONObject> errorList = new ArrayList<>();
		filterResults(testLogResponse, resultsList, errorList);
		LOGGER.info(String.format("TEST RESULTS - %s:%n%s", testModuleName, resultsList.toString()));
		if (errorList.size() > 0) {
			fail(String.format("Test failed! Failed test steps:%n%s", errorList.toString()));
		} else {
			LOGGER.info(String.format("%s was successful!", testModuleName));
		}
	}

	// filter through response JSONObjects for objects that refer to test step results and add to the appropriate lists
	private void filterResults(Response testLogResponse, List<JSONObject> resultsList,
							   List<JSONObject> errorList) throws JSONException {
		JSONArray jsonResponseBody = new JSONArray(testLogResponse.getBody().asString());
		for (int i=0; i < jsonResponseBody.length(); i++) {
			JSONObject jsonResultObject = jsonResponseBody.getJSONObject(i);
			if (jsonResultObject.has("result") && jsonResultObject.has("testId") &&
					!jsonResultObject.has("config")) {
				resultsList.add(jsonResultObject);
				if (!jsonResultObject.getString("result").equals("SUCCESS")) {
					errorList.add(jsonResultObject);
				}
			}
		}
	}
}
