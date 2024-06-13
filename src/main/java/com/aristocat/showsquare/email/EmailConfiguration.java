package com.aristocat.showsquare.email;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.SendContact;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.TrackOpens;
import com.mailjet.client.transactional.TransactionalEmail;
import com.mailjet.client.transactional.response.SendEmailsResponse;
import org.springframework.stereotype.Component;

@Component
public class EmailConfiguration {


    public void sendEmail(String from,String to,String link) throws MailjetException {
        ClientOptions options = ClientOptions.builder()
                .apiKey("01db83895ab16c97fbf53bc14384d794")
                .apiSecretKey("d5c2e168b41ab1ded242be9e988f6809")
                .build();

        MailjetClient client = new MailjetClient(options);

        TransactionalEmail message1 = TransactionalEmail
                .builder()
                .to(new SendContact(to, to))
                .from(new SendContact(from, "Mailjet integration test"))
                .htmlPart("<h1>This is the HTML content of the mail</h1> "+link)
                .subject("This is the subject")
                .trackOpens(TrackOpens.ENABLED)
                .build();

        SendEmailsRequest request = SendEmailsRequest
                .builder()
                .message(message1) // you can add up to 50 messages per request
                .build();

        // act
        SendEmailsResponse response = request.sendWith(client);
    }

}
