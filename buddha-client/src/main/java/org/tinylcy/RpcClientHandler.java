package org.tinylcy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by chenyangli.
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private RpcResponse response;

    public RpcClientHandler(RpcResponse response) {
        this.response = response;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, RpcResponse resp)
            throws Exception {
    	//经过之前解码器的解码后，这里能拿到响应对象resp
	    //将返回的响应内容拷贝至response
        System.out.println("RpcClientHandler - response: " + resp);
        response.setRequestId(resp.getRequestId());
        response.setError(resp.getError());
        response.setResult(resp.getResult());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public RpcResponse getResponse() {
        return response;
    }
}
