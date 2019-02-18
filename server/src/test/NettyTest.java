import com.alibaba.fastjson.JSON;
import com.ycc.netty.bean.SendMsg;
import com.ycc.netty.handler.tcp.ChatServerHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.junit.Assert;
import org.junit.Test;

public class NettyTest {
    private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    @Test
    public void testHandler() {
        EmbeddedChannel channel = new EmbeddedChannel(new ChatServerHandler(group));
        SendMsg sendMsg = new SendMsg();
        Assert.assertFalse(channel.writeInbound(JSON.toJSONString(sendMsg)));
        Assert.assertTrue(channel.finish());
        Object o = channel.readInbound();
        System.out.println(o);
    }
}
