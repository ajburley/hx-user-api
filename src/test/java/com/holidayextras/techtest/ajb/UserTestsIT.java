package com.holidayextras.techtest.ajb;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class UserTestsIT {
	private static final String SERVER = "http://localhost:8080";
	
	private static boolean deleteAllSucceeded = false;
	private static boolean getZeroUsersSucceeded = false;
	private static boolean getUsersSucceeded = false;
	private static boolean createUserSucceeded = false;
	private static boolean providedDatasetActivationSucceeded = false;
	
    private HttpResponse<String> sendRequest(HttpRequest request) {
        var client = HttpClient.newHttpClient();
        try {
             return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Error when calling the endpoint", e);
        }
    }
    
    private HttpResponse<String> sendGetRequest(String url) {
        var request = HttpRequest.newBuilder()
        		.GET()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();
        return sendRequest(request);
    }
    
    private HttpResponse<String> sendPostRequest(String url, String body) {
    	var bodyPublisher = HttpRequest.BodyPublishers.ofString(body);
        var request = HttpRequest.newBuilder()
        		.POST(bodyPublisher)
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();
        return sendRequest(request);
    }
    
    private HttpResponse<String> sendPutRequest(String url, String body) {
    	var bodyPublisher = HttpRequest.BodyPublishers.ofString(body);
        var request = HttpRequest.newBuilder()
        		.PUT(bodyPublisher)
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();
        return sendRequest(request);
    }
    
    private HttpResponse<String> sendDeleteRequest(String url) {
        var request = HttpRequest.newBuilder()
        		.DELETE()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();
        return sendRequest(request);
    }
    
    private void validateErrorObject(JSONObject errorObject, String expectedErrorCode, String expectedErroneousValue) {
    	var errorMap = (Map<String,?>)errorObject;
    	assertThat(errorMap, hasKey("errorCode"));
    	var errorCode = errorObject.getString("errorCode");
    	assertThat(errorCode, is(expectedErrorCode));
    	assertThat(errorMap, hasKey("message"));
    	var message = errorObject.getString("message");
    	assertThat(message, is(not(emptyString())));
    	if (expectedErroneousValue != null) {
        	assertThat(errorMap, hasKey("erroneousValue"));
        	var erroneousValue = errorObject.getString("erroneousValue");
        	assertThat(erroneousValue, is(expectedErroneousValue));
    	}
    }
    
    @Test
    @Order(1)
    public void setNumUsersToAlphabetic() {
    	var url = SERVER + "/db-mgmt/user-stats";
    	var body = "{\"numUsers\": \"abc\"}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "GEN-002", "abc");
    }

    @Test
    @Order(1)
    public void setNumUsersToNegativeValue() {
    	var url = SERVER + "/db-mgmt/user-stats";
    	var body = "{\"numUsers\": -5}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "DMC-001", "-5");
    }

    @Test
    @Order(1)
    public void setNumUsersToPositiveValue() {
    	var url = SERVER + "/db-mgmt/user-stats";
    	var body = "{\"numUsers\": 5}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "DMC-001", "5");
    }
    
    @Test
    @Order(1)
    public void createNonRandomTestDataset() {
    	var url = SERVER + "/db-mgmt/test-datasets";
    	var body = "{\"activated\": false, \"random\": false, \"numUsers\": 1}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "DMC-002", "false");
    }
    
    @Test
    @Order(1)
    public void createTestDatasetWithNegativeUsers() {
    	var url = SERVER + "/db-mgmt/test-datasets";
    	var body = "{\"activated\": false, \"random\": true, \"numUsers\": -1}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "DMC-003", "-1");
    }
    
    @Test
    @Order(1)
    public void createTestDatasetWithZeroUsers() {
    	var url = SERVER + "/db-mgmt/test-datasets";
    	var body = "{\"activated\": false, \"random\": true, \"numUsers\": 0}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "DMC-003", "0");
    }
    
    @Test
    @Order(1)
    public void activateTestDatasetWithAlphabeticID() {
    	var url = SERVER + "/db-mgmt/test-datasets/abc/activated";
    	var body = "{\"activated\": true}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "GEN-002", "abc");
    }
    
    @Test
    @Order(1)
    public void activateNonExistentTestDataset() {
    	var url = SERVER + "/db-mgmt/test-datasets/88888/activated";
    	var body = "{\"activated\": true}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "DMC-004", "88888");
    }
    
    @Test
    @Order(1)
    public void getUsersWithAlphabeticOffset() {
    	var url = SERVER + "/user-mgmt/users?offset=abc";
    	var response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "GEN-002", "abc");
    }
    
    @Test
    @Order(1)
    public void getUsersWithNegativeOffset() {
    	var url = SERVER + "/user-mgmt/users?offset=-1";
    	var response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "UC-001", "-1");
    }
    
    @Test
    @Order(1)
    public void getUsersWithNegativeLimit() {
    	var url = SERVER + "/user-mgmt/users?limit=-1";
    	var response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "UC-002", "-1");
    }

    @Test
    @Order(1)
    public void getUsersWithZeroLimit() {
    	var url = SERVER + "/user-mgmt/users?offset=5&limit=0";
    	var response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "UC-002", "0");
    }

    @Test
    @Order(1)
    public void createUserWithEmailMissingAttherate() {
    	var url = SERVER + "/user-mgmt/users";
    	var body = "{\"email\": \"joebloggs.com\", \"givenName\": \"Joe\", \"familyName\": \"Bloggs\"}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "UC-003", "joebloggs.com");
    }
    
    @Test
    @Order(1)
    public void createUserWithEmailStartingWithAttherate() {
    	var url = SERVER + "/user-mgmt/users";
    	var body = "{\"email\": \"@bloggs.com\", \"givenName\": \"Joe\", \"familyName\": \"Bloggs\"}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "UC-004", "@bloggs.com");
    }
    
    @Test
    @Order(1)
    public void createUserWithEmailEndingWithAttherate() {
    	var url = SERVER + "/user-mgmt/users";
    	var body = "{\"email\": \"joe@\", \"givenName\": \"Joe\", \"familyName\": \"Bloggs\"}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "UC-004", "joe@");
    }
    
    @Test
    @Order(1)
    public void createUserWithEmailHavingMultipleAttherates() {
    	var url = SERVER + "/user-mgmt/users";
    	var body = "{\"email\": \"phil@joe@bloggs.com\", \"givenName\": \"Joe\", \"familyName\": \"Bloggs\"}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "UC-005", "phil@joe@bloggs.com");
    }
    
    @Test
    @Order(1)
    public void createUserWithEmailDomainMissingDot() {
    	var url = SERVER + "/user-mgmt/users";
    	var body = "{\"email\": \"joe@bloggscom\", \"givenName\": \"Joe\", \"familyName\": \"Bloggs\"}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "UC-006", "joe@bloggscom");
    }
    
    @Test
    @Order(1)
    public void createUserWithEmailDomainStartingWithDot() {
    	var url = SERVER + "/user-mgmt/users";
    	var body = "{\"email\": \"joe@.com\", \"givenName\": \"Joe\", \"familyName\": \"Bloggs\"}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "UC-007", "joe@.com");
    }
    
    @Test
    @Order(1)
    public void createUserWithEmailDomainEndingWithDot() {
    	var url = SERVER + "/user-mgmt/users";
    	var body = "{\"email\": \"joe@bloggs.\", \"givenName\": \"Joe\", \"familyName\": \"Bloggs\"}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "UC-007", "joe@bloggs.");
    }
    
    @Test
    @Order(1)
    public void createUserWithNonAlphabeticGivenName() {
    	var url = SERVER + "/user-mgmt/users";
    	var body = "{\"email\": \"joe@bloggs.com\", \"givenName\": \"123\", \"familyName\": \"Bloggs\"}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "UC-008", "123");
    }
    
    @Test
    @Order(1)
    public void createUserWithNonAlphabeticFamilyName() {
    	var url = SERVER + "/user-mgmt/users";
    	var body = "{\"email\": \"joe@bloggs.com\", \"givenName\": \"Joe\", \"familyName\": \"456\"}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "UC-009", "456");
    }
    
    @Test
    @Order(1)
    public void updateUserWithNonAlphabeticFamilyName() {
    	var url = SERVER + "/user-mgmt/users/1";
    	var body = "{\"email\": \"joe@bloggs.com\", \"givenName\": \"Joe\", \"familyName\": \"456\"}";
    	var response = sendPutRequest(url, body);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "UC-009", "456");
    }
    
    private HttpResponse<String> deleteAllUsers() {
    	var url = SERVER + "/db-mgmt/user-stats";
    	var body = "{\"numUsers\": 0}";
    	return sendPostRequest(url, body);
    }

    @Test
    @Order(1)
    public void deleteAllUsersAndGetNone() {
    	var response = deleteAllUsers();
    	assertThat(response.statusCode(), is(200));
    	var url = SERVER + "/user-mgmt/users";
    	response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(200));
    	var responseBody = response.body();
    	var responseJSON = JSONArray.fromObject(responseBody);
    	assertThat(responseJSON.size(), is(0));
    	deleteAllSucceeded = true;
    	getZeroUsersSucceeded = true;
    }
    
    private void validateUser(JSONObject user) {
    	var userId = user.getString("id");
    	assertThat(userId, is(not(nullValue())));
    	assertThat(userId, is(not(emptyString())));
    	assertThat(userId.chars().allMatch(Character::isDigit), is(true));
    	var email = user.getString("email");
    	assertThat(email, is(not(nullValue())));
    	assertThat(email, is(not(emptyString())));
    	assertThat(email, containsString("@"));
    	assertThat(email, not(startsWith("@")));
    	assertThat(email, not(endsWith("@")));
    	var emailParts = email.split("@");
    	assertThat(emailParts, is(arrayWithSize(2)));
    	assertThat(emailParts[1], containsString("."));
    	assertThat(emailParts[1], not(startsWith(".")));
    	assertThat(emailParts[1], not(endsWith(".")));
    	var givenName = user.getString("givenName");
    	assertThat(givenName, is(not(nullValue())));
    	assertThat(givenName, is(not(emptyString())));
    	assertThat(givenName.chars().allMatch(Character::isLetter), is(true));
    	var familyName = user.getString("familyName");
    	assertThat(familyName, is(not(nullValue())));
    	assertThat(familyName, is(not(emptyString())));
    	if (familyName.contains("'")) {
    		assertThat(familyName, startsWith("O'"));
    		assertThat(familyName.substring(2), not(containsString("'")));
    	}
    	assertThat(familyName.replace("'", "").chars().allMatch(Character::isLetter), is(true));
    	var created = user.getString("created");
    	assertThat(created, is(not(nullValue())));
    	assertThat(created, is(not(emptyString())));
    }
    
    @Test
    @Order(2)
    public void deactivateAlreadyDeactivatedDataset() {
    	assumeTrue(deleteAllSucceeded);
    	assumeTrue(getZeroUsersSucceeded);
    	deleteAllUsers();
    	var url = SERVER + "/db-mgmt/test-datasets/1/activated";
    	var body = "{\"activated\": false}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(200));
    	url = SERVER + "/user-mgmt/users";
    	response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(200));
    	var responseBody = response.body();
    	var responseJSON = JSONArray.fromObject(responseBody);
    	assertThat(responseJSON.size(), is(0));
    }
    
    @Test
    @Order(2)
    public void createActivatedDatasetAndGetMultiple() {
    	assumeTrue(deleteAllSucceeded);
    	deleteAllUsers();
    	var url = SERVER + "/db-mgmt/test-datasets";
    	var body = "{\"activated\": true, \"random\": true, \"numUsers\": 5}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(200));
    	var responseBody = response.body();
    	var responseJSONObj = JSONObject.fromObject(responseBody);
    	assertThat((Map<String,?>)responseJSONObj, hasKey("newId"));
    	String newId = responseJSONObj.getString("newId");
    	assertThat(newId, is(not(nullValue())));
    	assertThat(newId, is(not(emptyString())));
    	url = SERVER + "/user-mgmt/users";
    	response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(200));
    	responseBody = response.body();
    	var responseJSONArr = JSONArray.fromObject(responseBody);
    	assertThat(responseJSONArr.size(), is(5));
    	var firstUser = responseJSONArr.getJSONObject(0);
    	validateUser(firstUser);
    	getUsersSucceeded = true;
    }
    
    @Test
    @Order(3)
    public void createDatasetThenActivate() {
    	assumeTrue(deleteAllSucceeded);
    	assumeTrue(getZeroUsersSucceeded);
    	assumeTrue(getUsersSucceeded);
    	deleteAllUsers();
    	var url = SERVER + "/db-mgmt/test-datasets";
    	var body = "{\"activated\": false, \"random\": true, \"numUsers\": 5}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(200));
    	var responseBody = response.body();
    	var responseJSONObj = JSONObject.fromObject(responseBody);
    	var newId = responseJSONObj.getString("newId");
    	assertThat(newId, is(not(nullValue())));
    	assertThat(newId, is(not(emptyString())));
    	url = SERVER + "/user-mgmt/users";
    	response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(200));
    	responseBody = response.body();
    	var responseJSONArr = JSONArray.fromObject(responseBody);
    	assertThat(responseJSONArr.size(), is(0));
    	url = SERVER + "/db-mgmt/test-datasets/" + newId + "/activated";
    	body = "{\"activated\": true}";
    	response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(200));
    	url = SERVER + "/user-mgmt/users";
    	response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(200));
    	responseBody = response.body();
    	responseJSONArr = JSONArray.fromObject(responseBody);
    	assertThat(responseJSONArr.size(), is(5));
    	var firstUser = responseJSONArr.getJSONObject(0);
    	validateUser(firstUser);
    }
    
    @Test
    @Order(3)
    public void createSingleUser() {
    	assumeTrue(deleteAllSucceeded);
    	assumeTrue(getUsersSucceeded);
    	deleteAllUsers();
    	var url = SERVER + "/user-mgmt/users";
    	var body = "{\"email\": \"joe@bloggs.com\", \"givenName\": \"Joe\", \"familyName\": \"Bloggs\"}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(200));
    	var responseBody = response.body();
    	var responseJSONObj = JSONObject.fromObject(responseBody);
    	var newId = responseJSONObj.getString("newId");
    	assertThat(newId, is(not(nullValue())));
    	assertThat(newId, is(not(emptyString())));
    	url = SERVER + "/user-mgmt/users";
    	response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(200));
    	responseBody = response.body();
    	var responseJSONArr = JSONArray.fromObject(responseBody);
    	assertThat(responseJSONArr.size(), is(1));
    	var user = responseJSONArr.getJSONObject(0);
    	assertThat(user.getString("email"), is("joe@bloggs.com"));
    	assertThat(user.getString("givenName"), is("Joe"));
    	assertThat(user.getString("familyName"), is("Bloggs"));
    	validateUser(user);
    	createUserSucceeded = true;
    }
    
    @Test
    @Order(3)
    public void activateProvidedDataset() {
    	assumeTrue(deleteAllSucceeded);
    	assumeTrue(getUsersSucceeded);
    	deleteAllUsers();
    	var url = SERVER + "/db-mgmt/test-datasets/0/activated";
    	var body = "{\"activated\": true}";
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(200));
    	url = SERVER + "/user-mgmt/users";
    	response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(200));
    	var responseBody = response.body();
    	var responseJSON = JSONArray.fromObject(responseBody);
    	assertThat(responseJSON.size(), is(1));
    	var user = responseJSON.getJSONObject(0);
    	validateUser(user);
    	providedDatasetActivationSucceeded = true;
    }
    
    private int createSingleUser(char basis, boolean returnNewId) {
    	var url = SERVER + "/user-mgmt/users";
		var email = basis + "@" + basis + ".com";
    	var body = "{\"email\": \"" + email + "\", \"givenName\": \"" + basis + "\", \"familyName\": \"" + basis + "\"}";
    	var response = sendPostRequest(url, body);
    	if (returnNewId) {
        	var responseJSON = JSONObject.fromObject(response.body());
        	return responseJSON.getInt("newId");
    	} else {
    		return -1;
    	}
    }
    
    private void createMultipleUsersAlphabetically(int numUsers) {
    	var currentName = 'a';
    	for (int i = 1; i <= numUsers; i++) {
    		createSingleUser(currentName, false);
        	currentName++;
    	}
    }
    
    @Test
    @Order(4)
    public void getUsersWithOffset() {
    	assumeTrue(deleteAllSucceeded);
    	assumeTrue(createUserSucceeded);
    	deleteAllUsers();
    	createMultipleUsersAlphabetically(7);
    	var url = SERVER + "/user-mgmt/users?offset=3";
    	var response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(200));
    	var responseBody = response.body();
    	var responseJSON = JSONArray.fromObject(responseBody);
    	assertThat(responseJSON.size(), is(4));
    	var firstUser = responseJSON.getJSONObject(0);
    	assertThat(firstUser.getString("givenName"), is("d"));
    }
    
    @Test
    @Order(4)
    public void getUsersWithLimit() {
    	assumeTrue(deleteAllSucceeded);
    	assumeTrue(createUserSucceeded);
    	deleteAllUsers();
    	createMultipleUsersAlphabetically(7);
    	var url = SERVER + "/user-mgmt/users?limit=3";
    	var response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(200));
    	var responseBody = response.body();
    	var responseJSON = JSONArray.fromObject(responseBody);
    	assertThat(responseJSON.size(), is(3));
    	var firstUser = responseJSON.getJSONObject(0);
    	assertThat(firstUser.getString("givenName"), is("a"));
    }
    
    @Test
    @Order(4)
    public void getUsersWithOffsetAndLimit() {
    	assumeTrue(deleteAllSucceeded);
    	assumeTrue(createUserSucceeded);
    	deleteAllUsers();
    	createMultipleUsersAlphabetically(7);
    	var url = SERVER + "/user-mgmt/users?offset=2&limit=3";
    	var response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(200));
    	var responseBody = response.body();
    	var responseJSON = JSONArray.fromObject(responseBody);
    	assertThat(responseJSON.size(), is(3));
    	var firstUser = responseJSON.getJSONObject(0);
    	assertThat(firstUser.getString("givenName"), is("c"));
    }
    
    @Test
    @Order(4)
    public void getNonExistentUser() {
    	assumeTrue(deleteAllSucceeded);
    	assumeTrue(createUserSucceeded);
    	deleteAllUsers();
    	var id = createSingleUser('a', true);
    	int nonExistentId;
    	if (id == 1) {
    		nonExistentId = 2;
    	} else {
    		nonExistentId = id - 1;
    	}
    	var url = SERVER + "/user-mgmt/users/" + nonExistentId;
    	var response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(200));
    	var responseBody = response.body();
    	assertThat(responseBody.length(), is(0));
    }
    
    @Test
    @Order(4)
    public void getExistentUser() {
    	assumeTrue(deleteAllSucceeded);
    	assumeTrue(createUserSucceeded);
    	deleteAllUsers();
    	int id = createSingleUser('a', true);
    	var url = SERVER + "/user-mgmt/users/" + id;
    	var response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(200));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	assertThat(responseJSON.getString("givenName"), is("a"));
    	validateUser(responseJSON);
    }
    
    @Test
    @Order(4)
    public void updateUser() {
    	assumeTrue(deleteAllSucceeded);
    	assumeTrue(createUserSucceeded);
    	assumeTrue(getUsersSucceeded);
    	deleteAllUsers();
    	int id = createSingleUser('a', true);
    	var url = SERVER + "/user-mgmt/users/" + id;
    	var body = "{\"email\": \"joe@bloggs.com\", \"givenName\": \"Joe\", \"familyName\": \"Bloggs\"}";
    	var response = sendPutRequest(url, body);
    	assertThat(response.statusCode(), is(200));
    	url = SERVER + "/user-mgmt/users";
    	response = sendGetRequest(url);
    	var responseBody = response.body();
    	var responseJSON = JSONArray.fromObject(responseBody);
    	assertThat(responseJSON.size(), is(1));
    	var user = responseJSON.getJSONObject(0);
    	assertThat(user.getString("givenName"), is("Joe"));
    }
    
    @Test
    @Order(4)
    public void deleteUser() {
    	assumeTrue(deleteAllSucceeded);
    	assumeTrue(createUserSucceeded);
    	assumeTrue(getZeroUsersSucceeded);
    	deleteAllUsers();
    	int id = createSingleUser('a', true);
    	var url = SERVER + "/user-mgmt/users/" + id;
    	var response = sendDeleteRequest(url);
    	assertThat(response.statusCode(), is(200));
    	url = SERVER + "/user-mgmt/users";
    	response = sendGetRequest(url);
    	var responseBody = response.body();
    	var responseJSON = JSONArray.fromObject(responseBody);
    	assertThat(responseJSON.size(), is(0));
    }
    
    @Test
    @Order(4)
    public void activateAlreadyActivatedDataset() {
    	assumeTrue(deleteAllSucceeded);
    	assumeTrue(getUsersSucceeded);
    	assumeTrue(providedDatasetActivationSucceeded);
    	deleteAllUsers();
    	var url = SERVER + "/db-mgmt/test-datasets/0/activated";
    	var body = "{\"activated\": true}";
    	sendPostRequest(url, body);
    	var response = sendPostRequest(url, body);
    	assertThat(response.statusCode(), is(200));
    	url = SERVER + "/user-mgmt/users";
    	response = sendGetRequest(url);
    	assertThat(response.statusCode(), is(200));
    	var responseBody = response.body();
    	var responseJSON = JSONArray.fromObject(responseBody);
    	assertThat(responseJSON.size(), is(1));
    	var user = responseJSON.getJSONObject(0);
    	validateUser(user);
    }
    
    @Test
    @Order(4)
    public void deactivateActivatedDataset() {
    	assumeTrue(deleteAllSucceeded);
    	assumeTrue(getUsersSucceeded);
    	assumeTrue(providedDatasetActivationSucceeded);
    	deleteAllUsers();
    	var url = SERVER + "/db-mgmt/test-datasets/0/activated";
    	var activateBody = "{\"activated\": true}";
    	sendPostRequest(url, activateBody);
    	var deactivateBody = "{\"activated\": false}";
    	var response = sendPostRequest(url, deactivateBody);
    	assertThat(response.statusCode(), is(400));
    	var responseBody = response.body();
    	var responseJSON = JSONObject.fromObject(responseBody);
    	validateErrorObject(responseJSON, "DMC-005", "false");
    }
}
