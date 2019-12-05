import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 客户端线程类，专门接受服务端响应的信息
 */

public class NioClientHandler implements Runnable {

    private Selector selector;

    public NioClientHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        for (; ; ) {

            //获取可用的channel数量, select是阻塞方法，直到获取到channl信息
            int readyChannels = 0;
            try {
                readyChannels = selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (readyChannels == 0)
                continue;

            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {

                SelectionKey selectionKey = (SelectionKey) iterator.next();

                //移除set中当前的selectionKey
                iterator.remove();

                /**
                 * 7.根据信息状态处理业务逻辑
                 */

                /**
                 * 如果是可读事件
                 */
                if (selectionKey.isReadable()) {

                    try {
                        readHandler(selectionKey, selector);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        /**
         * 要从SelectionKey中获取到已经就绪的channel
         */
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();


        /**
         * 只有buffer可以控制channel的读写，创建buffer
         */
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);


        /**
         * 循环读取服务器端请求信息
         */
        String response = "";
        while (socketChannel.read(byteBuffer) > 0) {
            /**
             * 切换buffer为读模式
             */
            byteBuffer.flip();

            /**
             * 读取buffer中的内容
             */
            response += Charset.forName("UTF-8").decode(byteBuffer);

        }

        /**
         * 将channel再次注册到selector上，监听他的可读事件
         */

        socketChannel.register(selector, SelectionKey.OP_READ);


        /**
         * 信息打印到本地
         */

        if (response.length() > 0) {

            System.out.println("" + response);

        }
    }
}
