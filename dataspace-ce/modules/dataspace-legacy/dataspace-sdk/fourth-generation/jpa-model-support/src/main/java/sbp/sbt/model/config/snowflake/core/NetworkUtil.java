package sbp.sbt.model.config.snowflake.core;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public final class NetworkUtil {

    private NetworkUtil() {
        /* no ops */
    }

    private static int compareInetAddress(InetAddress left, InetAddress right) {

        final byte[] a = left.getAddress();
        final byte[] b = right.getAddress();

        if (a.length != b.length) {
            return Integer.compare(a.length, b.length);
        }

        for (int i = 0; i < a.length; i++) {

            final int compare = Integer.compare(
                Byte.toUnsignedInt(a[i]),
                Byte.toUnsignedInt(b[i])
            );

            if (compare != 0) {
                return compare;
            }

        }

        return 0;
    }

    private static void collectIPv4(Enumeration<InetAddress> addresses, List<InetAddress> inetAddresses) {

        Collections
            .list(addresses)
            .stream()
            .filter(Inet4Address.class::isInstance)
            .filter(InetAddress::isSiteLocalAddress)
            .filter(inetAddress -> {
                final byte third = inetAddress.getAddress()[3];

                return !(third == 0 || third == -1);
            }).forEach(inetAddresses::add);

    }

    private static InetAddress getIPv4() throws SocketException {

        final List<InetAddress> inetAddresses = new ArrayList<>();

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

            collectIPv4(addresses, inetAddresses);
        }

        return inetAddresses
            .stream()
            .sorted(NetworkUtil::compareInetAddress)
            .collect(Collectors.toList())
            .stream()
            .findFirst()
            .orElse(null);
    }

    public static InetAddress getIP() throws SocketException, UnknownHostException {

        InetAddress candidate = getIPv4();

        if (candidate != null) {
            return candidate;
        }

        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();

        if (jdkSuppliedAddress == null) {
            throw new UnknownHostException("The method InetAddress.getLocalHost() returned null.");
        }

        return jdkSuppliedAddress;
    }

}
