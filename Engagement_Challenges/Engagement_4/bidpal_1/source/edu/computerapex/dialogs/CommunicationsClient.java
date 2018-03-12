package edu.computerapex.dialogs;

import edu.computerapex.dialogs.internal.CommunicationsChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class CommunicationsClient {

    private final Bootstrap bootstrap;
    private EventLoopGroup group;
    private final CommunicationsChannelInitializer initializer;

    /**
     * @param communicationsHandler handler that will be called when data is available
     * @param identity the client's identity (what we present to the server)
     */
    public CommunicationsClient(CommunicationsHandler communicationsHandler, CommunicationsIdentity identity) {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup(1);
        initializer = new CommunicationsChannelInitializer(communicationsHandler, identity, false);
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(initializer);
    }

    /**
     * Connects to a server
     * @param host hostname to connect to
     * @param port the port to connect to
     * @return a CommsConnection to be used to send data
     * @throws CommunicationsDeviation
     */
    public CommunicationsConnection connect(String host, int port) throws CommunicationsDeviation {
        try {
            Channel channel = bootstrap.connect(host, port).sync().channel();
            initializer.awaitForPermission(10000);
            return channel.attr(CommunicationsConnection.CONNECTION_ATTR).get();
        } catch (Exception e) {
            // NOTE: if we don't catch the generic 'Exception' here then
            // some other sort of exception may wind up stalling us.
            // This is because Netty is doing some strange things.
            // See: https://github.com/netty/netty/issues/2597
            throw new CommunicationsDeviation(e);
        }
    }

    /**
     * Connects to a server at the specified address
     * @param addr the address of the server
     * @return the connection that can be used to talk to the server
     * @throws CommunicationsDeviation
     */
    public CommunicationsConnection connect(CommunicationsNetworkAddress addr) throws CommunicationsDeviation {
        return connect(addr.fetchHost(), addr.fetchPort());
    }

    /**
     * Closes the client gracefully
     */
    public void close() {
        group.shutdownGracefully();
    }

    public EventLoopGroup takeEventLoopGroup() {
        return group;
    }
}
