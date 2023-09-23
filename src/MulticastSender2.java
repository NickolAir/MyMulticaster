import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MulticastSender2 {
    public static void main(String[] args) {
        // Групповый IP-адрес и порт для multicast-рассылки
        String multicastGroupAddress = "224.0.0.1";
        int multicastPort = 8888; // Порт получателя
        InetAddress ipAddress;

        try {
            InetAddress group = InetAddress.getByName(multicastGroupAddress);
            MulticastSocket multicastSocket = new MulticastSocket();

            try {
                ipAddress = InetAddress.getByName("244.0.0.10");
                //System.out.println(ipAddress.getHostAddress());
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

            while(true) {
                String Message = String.valueOf(ipAddress.getHostAddress());
                byte[] messageBytes = Message.getBytes();
                DatagramPacket packet = new DatagramPacket(
                        messageBytes, messageBytes.length, group, multicastPort);
                multicastSocket.send(packet);
                Thread.sleep(5000);
            }

            //multicastSocket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}