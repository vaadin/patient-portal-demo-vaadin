package com.vaadin.demo.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.demo.ui.security.SecurityUtils;
import com.vaadin.demo.ui.views.LoginView;
import com.vaadin.demo.ui.views.LoginView.LoginData;
import com.vaadin.demo.ui.views.MainView;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

@SpringUI
@Theme("portal")
@Viewport("width=device-width, initial-scale=1")
@Widgetset("com.vaadin.demo.ui.PatientPortalWidgetSet")
public class PatientPortalUI extends UI {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MainView mainView;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        if (SecurityUtils.isLoggedIn()) {
            showMainView();
        } else {
            showLoginView();
        }
    }

    private void showLoginView() {
        setContent(new LoginView(this::login));
    }

    private void showMainView() {
        setContent(mainView);
    }

    private boolean login(LoginData loginData) {
        try {
            Authentication token = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword()));
            // Reinitialize the session to protect against session fixation attacks. This does not work
            // with websocket communication.
            VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
            SecurityContextHolder.getContext().setAuthentication(token);
            showMainView();
            return true;
        } catch (AuthenticationException ex) {
            return false;
        }
    }
}
