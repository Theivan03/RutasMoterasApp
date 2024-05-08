package com.RutasMoteras.rutasmoterasapp;

import static org.mockito.Mockito.*;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

@RunWith(AndroidJUnit4.class)
public class ContactWithUsTest {
    @Rule
    public ActivityScenarioRule<ContactWithUs> activityRule = new ActivityScenarioRule<>(ContactWithUs.class);

    @Mock
    Session mockSession;

    @Mock
    Transport mockTransport;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendEmail() throws Exception {
        // Configurar mocks
        doNothing().when(mockTransport).send(any(MimeMessage.class));
        when(mockSession.getTransport("smtp")).thenReturn(mockTransport);

        // Ejecutar el método
        activityRule.getScenario().onActivity(activity -> {
            activity.sendEmail("Prueba", "Mensaje de prueba");
        });

        // Verificaciones y aserciones
        verify(mockTransport).send(any(MimeMessage.class));
    }
}
