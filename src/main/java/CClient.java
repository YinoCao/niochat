import java.io.IOException;

public class CClient {

    public static void main(String[] args) {
        try {
            new NioClient().start("Jerry");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
