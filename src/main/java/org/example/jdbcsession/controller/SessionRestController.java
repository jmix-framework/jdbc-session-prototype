package org.example.jdbcsession.controller;

import io.jmix.core.session.SessionData;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = SessionRestController.API_URL)
public class SessionRestController {

    public static final String API_URL = "/myapi/session_test";

    @Autowired
    private ObjectFactory<SessionData> sessionDataFactory;

    @RequestMapping(method = RequestMethod.PUT)
    public void setAttribute(@RequestParam("name") String name, @RequestParam("value") Object value) {
        sessionDataFactory.getObject().setAttribute(name, value);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Object getAttribute(@RequestParam("name") String name) {
        return sessionDataFactory.getObject().getAttribute(name);
    }

    @RequestMapping(path = "/expire", method = RequestMethod.GET)
    public void expireSession(HttpSession httpSession) {
        httpSession.invalidate();
    }
}
