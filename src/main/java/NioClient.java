import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class NioClient {


    /**
     * 启动
     */
    public void start(String nickName) throws IOException {
        /**
         * 连接服务端
         */
        SocketChannel socketChannel = SocketChannel.open(
                new InetSocketAddress("127.0.0.1",8010)
        );

        /**
         * 接受服务器响应
         * 新开线程，专门负责接受服务端的响应数据
         */
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread(new NioClientHandler(selector)).start();


        /**
         * 向服务端发送数据
         */
        Scanner scanner =new Scanner(System.in);

        while (scanner.hasNextLine()) {

            String request = scanner.nextLine();
            if(request != null && request.length() > 0) {
                socketChannel.write(Charset.forName("UTF-8").encode(nickName+" : "+request));
            }
        }



    }

    public static void main(String[] args) throws IOException {

    }


}
