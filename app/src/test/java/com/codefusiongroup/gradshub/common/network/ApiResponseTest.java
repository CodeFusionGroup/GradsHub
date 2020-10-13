package com.codefusiongroup.gradshub.common.network;

import junit.framework.TestCase;

import org.junit.Test;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.codefusiongroup.gradshub.common.network.ApiResponseConstants.API_NO_SUCCESS;
import static com.codefusiongroup.gradshub.common.network.ApiResponseConstants.API_SUCCESS_CODE;
import static com.codefusiongroup.gradshub.common.network.ApiResponseConstants.RESOURCE_LOCATION_MSG;
import static com.codefusiongroup.gradshub.common.network.ApiResponseConstants.RESOURCE_UNAVAILABLE;
import static com.codefusiongroup.gradshub.common.network.ApiResponseConstants.SERVER_FAILURE_CODE;
import static com.codefusiongroup.gradshub.common.network.ApiResponseConstants.SERVER_FAILURE_MSG;
import static org.mockito.Mockito.mock;

public class ApiResponseTest extends TestCase {
    String s = "name1";
    String s2 = "name2";
    ApiResponse apiResponse = new ApiResponse(s);
    ApiResponse apiResponse2;

    @Test
    public void testGetData() {
        assertEquals(s, apiResponse.getData());
    }

    @Test
    public void testSetData() {
        apiResponse2 = new ApiResponse(s);
        apiResponse2.setData(s2);
        assertEquals(s2, apiResponse2.getData());
    }

    @Test
    public void testGetError() {
        Throwable throwable = new Throwable("Test");
        ApiResponse apiResponse1 = new ApiResponse(throwable);
        assertEquals(throwable, apiResponse1.getError());
    }

    @Test
    public void testSetError() {
        Throwable throwable = new Throwable("Test");
        apiResponse.setError(throwable);
        assertEquals(throwable, apiResponse.getError());
    }

    @Test
    public void testAPIResponseConstants(){
        assertEquals("1", API_SUCCESS_CODE);
        assertEquals("0", API_NO_SUCCESS);
        assertEquals("500", RESOURCE_UNAVAILABLE);
        assertEquals("-100", SERVER_FAILURE_CODE);
        assertEquals("Connection failed, please try again later.", SERVER_FAILURE_MSG);
        assertEquals("unable to locate resources at the specified api.", RESOURCE_LOCATION_MSG);

    }
}