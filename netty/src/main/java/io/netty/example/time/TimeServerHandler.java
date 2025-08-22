package io.netty.example.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

/**
 * 本节要实现的协议是 TIME 协议。它与前面的示例不同，它发送一条包含 32 位整数的消息，而不接收任何请求，并在发送消息后关闭连接。
 * 在此示例中，您将学习如何构造和发送消息，以及如何在完成时关闭连接。
 * 因为我们将忽略任何接收到的数据，而是在建立连接后立即发送消息，所以这次我们不能使用该方法。
 * 相反，我们应该覆盖该方法。以下是实现：channelRead()channelActive()
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        // 编写一个 32 位整数，因此我们需要一个容量至少为 4 字节的 ByteBuf
        final ByteBuf time = ctx.alloc().buffer(4);
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
        final ChannelFuture f = ctx.writeAndFlush(time);

        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                assert f == channelFuture;
                ctx.close();
            };
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
