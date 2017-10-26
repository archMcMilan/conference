package com.epam.cm.tests;

import com.epam.cm.base.*;
import com.epam.cm.jira.Jira;
import com.epam.cm.utils.JsonLoader;
import io.restassured.http.ContentType;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.nullValue;

public class OrganiserChangingTalkStatusToInProgress extends SimpleBaseTest {

    private String validContent  = JsonLoader.asString("ChangeTalkStatusToInProgress.json");
    private String nonValidContent  = JsonLoader.asString("ChangeTalkStatusToNull.json");

    @Test
    @Jira("6661")
    public void positiveOrganiserChangingTalkStatusToInProgressOfExistingTest() {

        given()
                .contentType(ContentType.JSON)
                .baseUri(config.baseHost)
                .auth().preemptive().basic(config.organiserUser, config.organiserPassword)
                .cookie(TOKEN, response.cookie(TOKEN))
                .header(X_TOKEN, response.cookie(TOKEN))
                .body(validContent)
                .
        when()
                .patch(EndpointUrl.TALK + TextConstants.EXISTING_TALK_NEW_OR_IN_PROGRESS_ID)
                .
        then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(TextConstants.ERROR, nullValue(),
                        TextConstants.RESULT, hasToString(TextConstants.SUCCESSFULLY_UPDATED));
    }

    @Test
    @Jira("6663")
    public void negativeOrganiserChangingTalkStatusToInProgressOfNonExistingTalkTest() {

        given()
                .contentType(ContentType.JSON)
                .baseUri(config.baseHost)
                .auth().preemptive().basic(config.organiserUser, config.organiserPassword)
                .cookie(TOKEN, response.cookie(TOKEN))
                .header(X_TOKEN, response.cookie(TOKEN))
                .body(validContent)
                .
        when()
                .patch(EndpointUrl.TALK + TextConstants.NON_EXISTING_TALK_ID)
                .
        then()
                .log().all()
                .statusCode(404)
                .assertThat()
                .body(TextConstants.ERROR, hasToString(TextConstants.TALK_NOT_FOUND));
    }

    @Test
    @Jira("6735")
    public void negativeOrganiserChangingTalkStatusToNullOfExistingTalkTest() {

        given()
                .contentType(ContentType.JSON)
                .baseUri(config.baseHost)
                .auth().preemptive().basic(config.organiserUser, config.organiserPassword)
                .cookie(TOKEN, response.cookie(TOKEN))
                .header(X_TOKEN, response.cookie(TOKEN))
                .body(nonValidContent)
                .
        when()
                .patch(EndpointUrl.TALK + TextConstants.EXISTING_TALK_NEW_OR_IN_PROGRESS_ID)
                .
        then()
                .log().all()
                .statusCode(400)
                .assertThat()
                .body(TextConstants.ERROR, hasToString(TextConstants.NULL_STATUS));
    }

    @Test
    @Jira("6741")
    public void negativeUnauthorizedUserChangingTalkStatusToInProgressTest() {

        given()
                .contentType(ContentType.JSON)
                .baseUri(config.baseHost)
                .auth().preemptive().basic(TextConstants.WRONG_USER, TextConstants.WRONG_PASSWORD)
                .cookie(TOKEN, response.cookie(TOKEN))
                .header(X_TOKEN, response.cookie(TOKEN))
                .body(validContent)
                .
        when()
                .patch(EndpointUrl.TALK + TextConstants.EXISTING_TALK_NEW_OR_IN_PROGRESS_ID)
                .
        then()
                .log().all()
                .statusCode(401)
                .assertThat()
                .body(TextConstants.ERROR, hasToString(TextConstants.LOGIN_ERROR));
    }
}