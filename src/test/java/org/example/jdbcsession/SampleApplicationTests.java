package org.example.jdbcsession;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.jdbcsession.controller.SessionRestController;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = SampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SampleApplicationTests {

    protected static MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    @Autowired
    private MockMvc mockMvc;

    @Test
    void userSessionTest() throws Exception {
        String oAuth = obtainAccessToken("admin", "admin");
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

    @Test
    void persistentStorageTest() throws Exception {
        String resultValue = mockMvc.perform(
                auth("Zr55uo9QJQuRz5k+tFOXbeFQut0=", get(SessionRestController.API_URL)
                        .param("name", "test")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("gvxxCsRWzQ", resultValue);

    }

    private static MockHttpServletRequestBuilder auth(String oAuth, MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.accept(contentType)
                .header("Authorization", "Bearer " + oAuth)
                .header("Accept-Language", "en");
    }

    public String obtainAccessToken(String username, String password) throws Exception {
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

        return new JacksonJsonParser().parseMap(resultString).get("access_token").toString();
    }

}
