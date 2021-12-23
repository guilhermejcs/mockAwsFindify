import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import io.findify.sqsmock.SQSService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SQSExempleAtualizadoTest {

    static AmazonSQS client = generateClient2();

    @Test
    void teste() {

        // Cria o Mock do serviço SQS
        SQSService api = new SQSService(8001, 1);

        // Inicializa o serviço SQS
        api.start();

        // Cria a fila
        String queue = client.createQueue("fila1").getQueueUrl();
        // Envia a mensagem
        client.sendMessage(queue, "teste1");

        //Recebe a mensagem
        String mensagemRecebida = client.receiveMessage(queue).getMessages().get(0).getBody();
        System.out.println(mensagemRecebida);

        Assertions.assertEquals("teste1", mensagemRecebida);

        // Desliga o serviço SQS
        api.shutdown();
    }

    private static AmazonSQS generateClient2() {
        return AmazonSQSClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                "http://localhost:8001", "us-east-1"))
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();
    }
}
