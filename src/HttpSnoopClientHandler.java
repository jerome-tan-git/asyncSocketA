 import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
 
 public class HttpSnoopClientHandler extends SimpleChannelInboundHandler<HttpObject> {
 
     @Override
     public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
         if (msg instanceof HttpResponse) {
             HttpResponse response = (HttpResponse) msg;
 
             System.err.println("STATUS: " + response.getStatus());
             System.err.println("VERSION: " + response.getProtocolVersion());
             System.err.println();
 
             if (!response.headers().isEmpty()) {
                 for (String name: response.headers().names()) {
                     for (String value: response.headers().getAll(name)) {
                         System.err.println("HEADER: " + name + " = " + value);
                     }
                 }
                 System.err.println();
             }
 
             if (HttpHeaders.isTransferEncodingChunked(response)) {
                 System.err.println("CHUNKED CONTENT {");
             } else {
                 System.err.println("CONTENT {");
             }
         }
         if (msg instanceof HttpContent) {
             HttpContent content = (HttpContent) msg;
 
             System.out.print(content.content().toString(CharsetUtil.UTF_8));
             System.out.flush();
 
             if (content instanceof LastHttpContent) {
                 System.out.println("} END OF CONTENT");
                 ctx.close();
                 System.out.println("55555555555555");
             }
         }
     }
 
     @Override
     public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
         cause.printStackTrace();
         ctx.close();
     }
 }