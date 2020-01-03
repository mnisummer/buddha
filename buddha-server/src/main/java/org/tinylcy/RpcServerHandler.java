package org.tinylcy;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by chenyangli.
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

	/**
	 * 保存目标对象
	 */
    private Map<String, Object> handlerMap;
    private final Object lock = new Object();

    public RpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, RpcRequest request)
            throws Exception {
        System.out.println("RpcServerHandler request: " + request);
        RpcResponse response = new RpcResponse();//生成响应对象
        response.setRequestId(request.getRequestId());//关联请求
        response.setError(null);
        Object result = handle(request);//处理逻辑
        response.setResult(result);//设置响应结果
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    private Object handle(RpcRequest request) throws Exception {
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);
        if (serviceBean == null) {
			//通过反射创建对象
	        synchronized (lock) {
		        serviceBean = handlerMap.get(className);
		        if (serviceBean == null) {
			        serviceBean = Class.forName(className).newInstance();
			        handlerMap.put(className, serviceBean);
		        }
	        }
        }
        //反射调用方法
        Method[] methods = serviceBean.getClass().getDeclaredMethods();
        Object result = null;
	    for (Method method : methods) {
		    if (method.getName().equals(request.getMethodName())) {
			    result = method.invoke(serviceBean, request.getParams());
			    break;
		    }
	    }
        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
