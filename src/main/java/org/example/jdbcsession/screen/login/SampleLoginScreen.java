package org.example.jdbcsession.screen.login;

import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletResponse;
import io.jmix.core.CoreProperties;
import io.jmix.core.Messages;
import io.jmix.core.security.ClientDetails;
import io.jmix.ui.Notifications;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.UiProperties;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.CheckBox;
import io.jmix.ui.component.ComboBox;
import io.jmix.ui.component.PasswordField;
import io.jmix.ui.component.TextField;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;

import java.util.Locale;

import static org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices.DEFAULT_PARAMETER;

@UiController("sample_LoginScreen")
@UiDescriptor("sample-login-screen.xml")
@Route(path = "login", root = true)
public class SampleLoginScreen extends Screen {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private TextField<String> usernameField;

    @Autowired
    private PasswordField passwordField;

    @Autowired
    protected CheckBox rememberMeCheckBox;

    @Autowired
    private ComboBox<Locale> localesField;

    @Autowired
    private Notifications notifications;

    @Autowired
    private Messages messages;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CoreProperties coreProperties;

    @Autowired
    private UiProperties uiProperties;

    @Autowired
    private ScreenBuilders screenBuilders;

    @Autowired
    protected CompositeSessionAuthenticationStrategy authenticationStrategy;

    @Subscribe
    private void onInit(InitEvent event) {
        usernameField.focus();
        initLocalesField();
        initDefaultCredentials();
    }

    private void initLocalesField() {
        localesField.setOptionsMap(coreProperties.getAvailableLocales());
        localesField.setValue(coreProperties.getAvailableLocales().values().iterator().next());
    }

    private void initDefaultCredentials() {
        usernameField.setValue("admin");
        passwordField.setValue("admin");
    }

    @Subscribe("submit")
    private void onSubmitActionPerformed(Action.ActionPerformedEvent event) {
        login();
    }

    protected void login() {
        String username = usernameField.getValue();
        String password = passwordField.getValue() != null ? passwordField.getValue() : "";

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption(messages.getMessage("loginWindow.emptyLoginOrPassword"))
                    .show();
            return;
        }

        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            ClientDetails clientDetails = ClientDetails.builder()
                    .locale(localesField.getValue())
                    .build();
            authenticationToken.setDetails(clientDetails);
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            onSuccessfulAuthentication(authentication);

            String mainScreenId = uiProperties.getMainScreenId();
            screenBuilders.screen(this)
                    .withScreenId(mainScreenId)
                    .withOpenMode(OpenMode.ROOT)
                    .build()
                    .show();
        } catch (BadCredentialsException e) {
            showLoginException(e.getMessage());
        }
    }

    protected void onSuccessfulAuthentication(Authentication authentication) {
        VaadinServletRequest request = VaadinServletRequest.getCurrent();
        VaadinServletResponse response = VaadinServletResponse.getCurrent();
        request.setAttribute(DEFAULT_PARAMETER, rememberMeCheckBox.isChecked());

        authenticationStrategy.onAuthentication(authentication, request, response);
    }

    protected void showLoginException(String message) {
        String title = messages.getMessage("loginWindow.loginFailed");
        notifications.create(Notifications.NotificationType.ERROR)
                .withCaption(title)
                .withDescription(message)
                .show();
    }

}
