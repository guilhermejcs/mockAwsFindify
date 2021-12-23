import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import io.findify.sqsmock.SQSService;
import org.junit.jupiter.api.Test;

public class SQSExempleTest {

    @Test
    void teste() {

            // create and start SQS API mock
            SQSService api = new SQSService(8001, 1);
            api.start();

            // AWS SQS client setup
            AWSCredentials credentials = new AnonymousAWSCredentials();
            AmazonSQSClient client = new AmazonSQSClient(credentials);

            client.setEndpoint("http://localhost:8001");

            // use it as usual
            String queue = client.createQueue("hello").getQueueUrl();
            client.sendMessage(queue,"world");

        System.out.println(client.receiveMessage(queue).getMessages().get(0).getBody());

        api.shutdown();

    }
}
