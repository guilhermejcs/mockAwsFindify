import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class S3ExempleTest {

    static AmazonS3 client = generateClient();

    @Test
    void teste(){

        String dir = "src/test/tmp/s3";
        // Cria o Mock do serviço S3
        S3Mock api = S3Mock.create(8001, dir);
        // Inicializa o serviço S3
        api.start();

        // Cria um bucket
        client.createBucket("testbucket1");
        // Cria 3 objetos
        client.putObject("testbucket1", "test1", "arquivo1");
        client.putObject("testbucket1", "test2", "arquivo2");
        client.putObject("testbucket1", "test3", "arquivo3");

        // Cria outro bucket
        client.createBucket("testbucket2");
        // Cria 1 objeto
        client.putObject("testbucket2", "test1", "arquivo1");

        // Lista o conteúdo dos buckets
        listaObjetos("testbucket1");
        listaObjetos("testbucket2");

        // Recupera o conteúdo de um arquivo
        String conteudoArquivo = conteudoObjetoS3("testbucket1", "test3");
        // Imprime o conteúdo do arquivo
        System.out.println(conteudoArquivo);

        // Move um objeto de um bucket para outro
        moveObjetos("testbucket1", "testbucket2",
                "test3", "test3_movido");

        // Lista o conteúdo dos buckets
        listaObjetos("testbucket1");
        listaObjetos("testbucket2");

        // Recupera o conteúdo de um arquivo
        String conteudoArquivoMovido = conteudoObjetoS3("testbucket2", "test3_movido");
        System.out.println(conteudoArquivoMovido);

        // Verificar o conteúdo do arquivo
        Assertions.assertEquals("arquivo3", conteudoArquivoMovido);


        // Deleta arquivos do bucket1
        client.deleteObject("testbucket1", "test1");
        client.deleteObject("testbucket1", "test2");
        // Deleta bucket1
        client.deleteBucket("testbucket1");

        // Deleta arquivos do bucket2
        client.deleteObject("testbucket2", "test1");
        client.deleteObject("testbucket2", "test3_movido");

        // Deleta bucket2
        client.deleteBucket("testbucket2");

        // Desliga o Mock do serviço S3
        api.shutdown();

    }

    public static void listaObjetos(String bucketName) {
        ListObjectsV2Result result = client.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        System.out.println("Arquivos no Bucket: " + bucketName);
        for (S3ObjectSummary os : objects) {
            System.out.println("* " + os.getKey());
        }
    }

    public static void moveObjetos(String bucketOrigem, String bucketDestino,
                                   String nomeArquivoOrigem, String nomeArquivoDestino) {
        client.copyObject(bucketOrigem, nomeArquivoOrigem, bucketDestino, nomeArquivoDestino);
        client.deleteObject(bucketOrigem, nomeArquivoOrigem);
    }

    public static String conteudoObjetoS3(String bucketName, String fileName) {
        System.out.printf("O conteúdo do arquivo %s no bucket %s é: ", fileName, bucketName);
        return client.getObjectAsString(bucketName, fileName);
    }

    public static AmazonS3 generateClient() {
        return AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                        "http://localhost:8001","us-east-1"))
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();
    }
}
