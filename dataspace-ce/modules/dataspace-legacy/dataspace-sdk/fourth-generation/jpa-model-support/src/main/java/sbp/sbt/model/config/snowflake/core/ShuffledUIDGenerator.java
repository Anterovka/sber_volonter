package sbp.sbt.model.config.snowflake.core;

import com.google.common.primitives.Longs;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Random;

import static sbp.sbt.model.config.snowflake.core.NetworkUtil.getIP;

public class ShuffledUIDGenerator implements StringIdGenerator {

    public static final int DEFAULT_OCTET = 4;

    private static final Random random = new SecureRandom();

    private static final ShuffledUIDGenerator uidGenerator = createInstance();

    private final byte id;
    private final byte shuffleId;
    private final Object lock = new Object();
    private short seq;
    private long lastTimestamp;

    ShuffledUIDGenerator(byte id,
                         byte shuffleId) {
        this.id = id;
        this.shuffleId = shuffleId;
    }

    public byte getId() {
        return this.id;
    }

    public byte getShuffleId() {
        return this.shuffleId;
    }

    private static ShuffledUIDGenerator createInstance() {

        try {
            byte id = generateIdByIpAddress();
            byte shuffleId = generateShuffleId();
            return new ShuffledUIDGenerator(
                id,
                shuffleId
            );
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    private static byte generateIdByIpAddress() throws SocketException, UnknownHostException {

        byte[] address = getIP().getAddress();

        return address[DEFAULT_OCTET - 1];

    }

    public static ShuffledUIDGenerator getInstance() {
        return uidGenerator;
    }

    private static byte generateShuffleId() {
        return (byte) random.nextInt(255);
    }

    long countdown() {
        long time = System.currentTimeMillis();
        return 1000 * (time / 1000 + 1) - time;
    }

    byte[] toByteArray(long value) {
        byte[] result = new byte[8];
        for (int i = 3; i >= 0; i--) {
            result[i] = (byte) (value & 0xffL);
            value >>= 8;
        }
        return result;
    }

    long nextId() {
        synchronized (lock) {
            long timeValue = System.currentTimeMillis() / 1000;
            if (lastTimestamp != timeValue) {
                seq = 0;
                lastTimestamp = timeValue;
                lock.notifyAll();
            }
            byte[] time = toByteArray(timeValue);
            while (seq < 0) { // overflow
                try {
                    lock.wait(countdown());
                    timeValue = System.currentTimeMillis() / 1000;
                    if (lastTimestamp != timeValue) {
                        seq = 0;
                        lastTimestamp = timeValue;
                        lock.notifyAll();
                    }
                    time = toByteArray(timeValue);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    return nextId();
                }
            }
            seq++; //inc
            time[4] = shuffleId;
            time[5] = id; //ip
            time[7] = (byte) (seq & 0xffL); //low
            time[6] = (byte) (seq >> 8); //hi

            return Longs.fromByteArray(time);
        }

    }

    @Override
    public String getNextValue() {
        return String.valueOf(nextId());
    }
}
