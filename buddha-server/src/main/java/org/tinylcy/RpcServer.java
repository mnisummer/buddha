package org.tinylcy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.tinylcy.annotation.RpcService;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenyangli.
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(RpcServer.class);

    /**
     * host:port
     */
    private String serverAddress;
    private ServiceRegistry registry;

    /**
     * Service handler map
     * key:     service Id        (interface name)
     * value:   concrete handler  (Spring bean)
     */
    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(String serverAddress, ServiceRegistry registry) {
        this.serverAddress = serverAddress;
        this.registry = registry;
    }

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
    	//从spring容器中获取到提供rcp服务的类
        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceMap)) {
            for (Object bean : serviceMap.values()) {
                String interfaceName = bean.getClass().getAnnotation(RpcService.class).value().getName();
                handlerMap.put(interfaceName, bean);
            }
        }
    }

    public void afterPropertiesSet() throws Exception {
    	//初始化注册中心
        registry.init();
        bootstrap();
    }

	/**
	 * 启动服务
	 */
	private void bootstrap() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new RpcEncoder(RpcResponse.class));
                            channel.pipeline().addLast(new RpcDecoder(RpcRequest.class));
                            // channel.pipeline().addLast(new RpcRequestCodec());
                            channel.pipeline().addLast(new RpcServerHandler(handlerMap));
                            // channel.pipeline().addLast(new RpcResponseCodec());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] tokens = serverAddress.split(":");
            String host = tokens[0];
            int port = Integer.parseInt(tokens[1]);

            ChannelFuture future = bootstrap.bind(new InetSocketAddress(port)).sync();

            //往注册中心注册服务地址
            registry.register(host, port);

            System.out.println("buddha rpc server start successfully, listening on port: " + port);
            LOGGER.info("RpcServer start successfully, listening on port: " + port);

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("RpcServer bind failure.");
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        ApplicationContext context =
                new ClassPathXmlApplicationContext("applicationContext.xml");

        // RpcServer server = (RpcServer) context.getBean("buddha-rpc-server");
        // System.out.println(server);
    }

}
