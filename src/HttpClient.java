import java.net.URI;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;


public class HttpClient {
	 public void connect(String host, int port) throws Exception {
	        EventLoopGroup workerGroup = new NioEventLoopGroup();

	        try {
	            Bootstrap b = new Bootstrap();
	            b.group(workerGroup);
	            b.channel(NioSocketChannel.class);
	            b.option(ChannelOption.SO_KEEPALIVE, true);
	            b.handler(new ChannelInitializer<SocketChannel>() {
	                @Override
	                public void initChannel(SocketChannel ch) throws Exception {
	                    // �ͻ��˽��յ�����httpResponse��Ӧ������Ҫʹ��HttpResponseDecoder���н���
	                    ch.pipeline().addLast(new HttpResponseDecoder());
	                    // �ͻ��˷��͵���httprequest������Ҫʹ��HttpRequestEncoder���б���
	                    ch.pipeline().addLast(new HttpRequestEncoder());
	                    ch.pipeline().addLast(new HttpClientInboundHandler());
	                }
	            });

	            // Start the client.
	            ChannelFuture f = b.connect(host, port).sync();

	            URI uri = new URI("/");
	            String msg = "Are you ok?";
	            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
	                    uri.toASCIIString(), Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));
	            System.out.println(uri.toASCIIString());
	            // ����http����
	            request.headers().set(HttpHeaders.Names.HOST, host);
	            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
	            request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
	            // ����http����
	            f.channel().write(request);
	            f.channel().flush();
	            f.channel().closeFuture().sync();
	        } finally {
	        	System.out.println("+++++++++++++++++++++++++++++++++");
	            workerGroup.shutdownGracefully();
	        }

	    }

	    public static void main(String[] args) throws Exception {
	        HttpClient client = new HttpClient();
	        client.connect("121.40.187.96", 8080);
	    }

}
