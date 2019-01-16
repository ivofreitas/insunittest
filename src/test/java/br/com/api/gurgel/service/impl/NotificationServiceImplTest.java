package br.com.api.gurgel.service.impl;

import br.com.api.gurgel.model.NotificationRequest;
import br.com.api.gurgel.service.NotificationService;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreatePlatformApplicationRequest;
import com.amazonaws.services.sns.model.CreatePlatformApplicationResult;
import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@WebFluxTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NotificationServiceImplTest {

    private Faker faker = new Faker();
    String title1 = faker.book().title();
    String title2 = faker.book().title();
    String title3 = faker.book().title();
    private Map<String, NotificationRequest> notificationRequestMap = new HashMap<>();

    @Mock
    private AmazonSNS amazonSNS;

    @InjectMocks
    private NotificationService notificationService = new NotificationServiceImpl();

    @Before
    public void setUp() {

        notificationRequestMap.put(title1, new NotificationRequest("GCM", "Teste", title1, faker.book().genre()));
        notificationRequestMap.put(title2, new NotificationRequest("GCM", "Teste", title2, faker.book().genre()));
        notificationRequestMap.put(title3, new NotificationRequest("GCM", "Teste", title3, faker.book().genre()));
    }

    @Test
    public void newTopicNotification() {

        NotificationRequest notificationRequest = notificationRequestMap.get(title1);
        ReflectionTestUtils.setField(NotificationServiceImpl.class, "senderId", "AAAAtD6uF5U:APA91bH7cYsxjY1TkzGvF6wHwXin_6-thPpgKB3NzPrhpqJMlvdcN4rU6PDx7dcPcv81gXNZO1MGgkm0QeMZyvxWZohvJW6M9QUU2nYfbrc59HPsCMpPg0rvCCG1IeBUviBqewChOvzp");

        CreatePlatformApplicationResult createPlatformApplicationResult = new CreatePlatformApplicationResult();

        Mockito.when(notificationService.)


    }

    @Test
    public void getAllNotifications() {
    }
}