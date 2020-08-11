package org.example.jdbcsession;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.example.jdbcsession.controller.SessionRestController;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = SampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SampleApplicationTests {

    protected static MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    private final static String ACCESS_TOKEN_KEY = "access_token";
    private final static String REFRESH_TOKEN_KEY = "refresh_token";

    @Autowired
    private MockMvc mockMvc;

    protected static String persistentOAuth;
    protected static String persistentRefreshToken;
    protected static String persistentAttributeValue;

    @Test
    @Order(0)
    void predefinedAttributes() throws Exception {
        Map<String, Object> tokenMap = obtainAccessToken("admin", "admin");
        persistentOAuth = tokenMap.get(ACCESS_TOKEN_KEY).toString();
        persistentRefreshToken = tokenMap.get(REFRESH_TOKEN_KEY).toString();

        persistentAttributeValue = RandomStringUtils.randomAlphabetic(10);
        assertNotNull(persistentOAuth);

        mockMvc.perform(
                auth(persistentOAuth, put(SessionRestController.API_URL)
                        .param("name", "test")
                        .param("value", persistentAttributeValue)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(1)
    void persistentStorageTest() throws Exception {
        String resultValue = mockMvc.perform(
                auth(persistentOAuth, get(SessionRestController.API_URL)
                        .param("name", "test")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(persistentAttributeValue, resultValue);

    }

    @Test
    @Order(2)
    void useRefreshToken() throws Exception {
        Map<String, Object> tokenMap = obtainAccessToken(persistentRefreshToken);
        persistentOAuth = tokenMap.get(ACCESS_TOKEN_KEY).toString();
        persistentRefreshToken = tokenMap.get(REFRESH_TOKEN_KEY).toString();

        String resultValue = mockMvc.perform(
                auth(persistentOAuth, get(SessionRestController.API_URL)
                        .param("name", "test")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(persistentAttributeValue, resultValue);

    }

    @Test
    @Order(3)
    void userSessionTest() throws Exception {
        String oAuth = obtainAccessToken("admin", "admin").get(ACCESS_TOKEN_KEY).toString();
        String attributeValue = RandomStringUtils.randomAlphabetic(10);

        mockMvc.perform(
                auth(oAuth, put(SessionRestController.API_URL)
                        .param("name", "test")
                        .param("value", attributeValue)))
                .andExpect(status().isOk());

        String resultValue = mockMvc.perform(
                auth(oAuth, get(SessionRestController.API_URL)
                        .param("name", "test")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(attributeValue, resultValue);

        mockMvc.perform(
                auth(oAuth, get(SessionRestController.API_URL + "/expire")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(
                auth(oAuth, get(SessionRestController.API_URL)
                        .param("name", "test")))
                .andExpect(status().isUnauthorized());
    }

    private static MockHttpServletRequestBuilder auth(String oAuth, MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.accept(contentType)
                .header("Authorization", "Bearer " + oAuth)
                .header("Accept-Language", "en");
    }

    public Map<String, Object> obtainAccessToken(String username, String password) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", username);
        params.add("password", password);

        ResultActions result = mockMvc.perform(post("/rest/oauth/token")
                .params(params)
                .with(httpBasic("client", "secret"))
                .accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
        String resultString = result.andReturn().getResponse().getContentAsString();

        return new JacksonJsonParser().parseMap(resultString);
    }

    public Map<String, Object> obtainAccessToken(String refreshToken) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);

        ResultActions result = mockMvc.perform(post("/rest/oauth/token")
                .params(params)
                .with(httpBasic("client", "secret"))
                .accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
        String resultString = result.andReturn().getResponse().getContentAsString();

        return new JacksonJsonParser().parseMap(resultString);
    }

}
