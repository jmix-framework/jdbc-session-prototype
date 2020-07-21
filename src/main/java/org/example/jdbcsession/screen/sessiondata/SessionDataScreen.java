package org.example.jdbcsession.screen.sessiondata;

import io.jmix.core.session.SessionData;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.Label;
import io.jmix.ui.component.TextField;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("session-data")
@UiDescriptor("session-data.xml")
public class SessionDataScreen extends Screen {

    @Autowired
    protected SessionData sessionData;

    @Autowired
    protected TextField sessionAttribute;

    @Autowired
    protected Label currentSessionLabel;

    private final static String SESSION_ATTRIBUTE_NAME = "testAttribute";

    @Subscribe
    protected void onInit(InitEvent event) {
        currentSessionLabel.setValue(sessionData.getSessionId());
        sessionAttribute.setValue(sessionData.getAttribute(SESSION_ATTRIBUTE_NAME));
    }

    @Subscribe("attributeBtn")
    public void onAttributeBtnClick(Button.ClickEvent event) {
        sessionData.setAttribute(SESSION_ATTRIBUTE_NAME, sessionAttribute.getValue());
    }
}
