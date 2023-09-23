import java.io.IOException;
import java.net.*;
import java.util.*;

public class MulticastReceiver {
    public static void main(String[] args) {
        // Групповый IP-адрес и порт для multicast-рассылки
        String multicastGroupAddress = "224.0.0.1";
        int multicastPort = 8888; // Порт ресивера
        Map<InetAddress, Long> ipTimestampMap = new HashMap<>(); // Хранение временных меток для адресов

        try {
            InetAddress group = InetAddress.getByName(multicastGroupAddress);
            MulticastSocket multicastSocket = new MulticastSocket(multicastPort);
            multicastSocket.joinGroup(group);

            byte[] buffer = new byte[1024];
            System.out.println("Waiting message...");

            // Поток для проверки адресов и их отсоединения
            Thread checkThread = new Thread(() -> {
                while (true) {
                    long currentTime = System.currentTimeMillis();

                    synchronized (ipTimestampMap) {
                        Iterator<Map.Entry<InetAddress, Long>> iterator = ipTimestampMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<InetAddress, Long> entry = iterator.next();
                            InetAddress sender = entry.getKey();
                            long timestamp = entry.getValue();

                            if (currentTime - timestamp >= 5000) {
                                System.out.println(sender + " is disconnected.\n");
                                iterator.remove();
                            }
                        }
                    }

                    try {
                        Thread.sleep(5000); // Проверка каждые 5 секунд
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            checkThread.start();

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                InetAddress senderAddress = InetAddress.getByName(message);

                synchronized (ipTimestampMap) {
                    ipTimestampMap.put(senderAddress, System.currentTimeMillis());
                }

                System.out.println(message + " is connected.\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}