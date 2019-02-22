package com.ycc.netty.server;

import com.ycc.netty.bean.LogEvent;
import com.ycc.netty.handler.udp.LogEventEncoder;
import com.ycc.netty.util.ExecutorPool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.util.concurrent.Executor;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * @author :MysticalYcc
 * @date :11:12 2019/2/20
 */
public class LogEventBroadcaster {

    private Logger logger = LoggerFactory.getLogger(LogEventBroadcaster.class);
    private Executor executor = ExecutorPool.getInstance();
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private  final File file;
    private WatchService watcher;

    public LogEventBroadcaster(InetSocketAddress address, File file) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new LogEventEncoder(address));
        this.file = file;
    }

    public void run() throws IOException {
        Channel ch = bootstrap.bind(0).syncUninterruptibly().channel();
        long pointer = 0;
        for (; ; ) {
            pointer = vernierTraversal(ch, pointer, file);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
        }
    }

    private long vernierTraversal(Channel ch, long pointer, File file) throws IOException {
        long len = file.length();
        if (len < pointer) {
            pointer = len;
        } else {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(pointer);
            String line;
            while ((line = raf.readLine()) != null) {
                ch.write(new LogEvent(null, -1, file.getAbsolutePath(), line));
            }
            ch.flush();
            pointer = raf.getFilePointer();
            raf.close();
        }
        return pointer;
    }

    /**
     * 读取log文件更改成监听目录文件变化并读取变化的文件
     *
     * @throws IOException
     */
    public void fileWatch() throws IOException {
        Channel ch = bootstrap.bind(0).syncUninterruptibly().channel();
        watcher = FileSystems.getDefault().newWatchService();
        String directory = System.getProperty("user.dir") + "/log";
        Path path = Paths.get(directory);
        path.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);

        executor.execute(() -> {
            long pointer = 0;
            while (true) {
                WatchKey key = null;
                try {
                    key = watcher.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    //事件可能lost or discarded
                    if (kind == OVERFLOW) {
                        continue;
                    }
                    Path fileName = (Path) event.context();
                    if (kind == ENTRY_CREATE) {
                        // 判断文件是否创建成功
                        boolean fileIsCreateSuccess = fileIsCreateSuccess(directory + fileName);
                        if (fileIsCreateSuccess) {
                            logger.info("file is created succeed!:[{}]", fileName);
                        }
                    }
                    if (!fileName.toString().contains("jb_tmp") && !fileName.toString().contains("jb_old")) {
                        logger.debug("Event [{}] has happened,which fileName is [{}] ", kind.name(), fileName);
                        try {
                            File file = new File(directory + "/" + fileName);
                            pointer = vernierTraversal(ch, pointer, file);
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            Thread.interrupted();
                            e.printStackTrace();
                            break;
                        }
                    }

                }
            }
        });
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        int port = 8083;
        String path = System.getProperty("user.dir") + "/log.txt";
        LogEventBroadcaster broadcaster = new LogEventBroadcaster(new InetSocketAddress(
                "255.255.255.255", port), new File(path));
        try {
            broadcaster.run();
        } finally {
            broadcaster.stop();
        }
    }

    /**
     * 监听的ENTRY_CREATE事件 文件是否创建完成,防止因为有ENTRY_CREATE事件却没有完成文件
     *
     * @param filePath 监听目录
     * @return ENTRY_CREATE文件是否创建完成
     */
    private boolean fileIsCreateSuccess(String filePath) {
        try {
            File file;
            file = new File(filePath);
            long len1, len2;
            len2 = file.length();
            do {
                len1 = len2;
                Thread.sleep(100);
                file = new File(filePath);
                len2 = file.length();
                System.out.println("Before the 500 ms with then:" + len1 + "," + len2);
            } while (len1 < len2);
            return true;
        } catch (Exception e) {
            logger.error("File creation failed", e);
            return false;
        }
    }

}