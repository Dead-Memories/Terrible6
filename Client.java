import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        System.out.println("              Добро пожаловать в приложение! \n" +
                           "Соединение будет установлено буквально с секунды на секунду.\n" +
                "*Для просмотра доступных команд введите help\n" +
                "*Для выхода из приложения с сохранением коллекции в файл введите ctrl+c");
        boolean first_time = true;
        boolean sended = true;
        try {

            Scanner in = new Scanner(System.in);
            String command="";
            while (true) {
                if (!first_time & sended)
                    command = in.nextLine();
                sended = false;
                SocketChannel outcoming = SocketChannel.open();

                InetSocketAddress  isa = new InetSocketAddress(args[0] , 2605);
                outcoming.connect(isa);
                if (first_time) {
                    System.out.println("Server reached");
                    System.out.println(">Start command reading");
                    first_time = false;
                    command = in.nextLine();
                }
                outcoming.socket().setSoTimeout(10000);
                try (ObjectOutputStream SendtoServer = new ObjectOutputStream(outcoming.socket().getOutputStream());
                     ObjectInputStream GetfromServer = new ObjectInputStream(outcoming.socket().getInputStream())
                ) {
                    try {
                        try {
                            if (!command.equals("exit")) {

                                ClientCommandReader.start_reading(GetfromServer, SendtoServer, command);

                                outcoming.finishConnect();
                            } else {
                                System.out.println(">Closing socket and terminating programm. Boooom");
                                outcoming.finishConnect();
                                break;
                            }
                        } catch (IOException e)
                        {
                            System.out.println("Сервер занят, пожалуйста подождите");
                            continue;
                        }
                        sended = true;

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }catch (Exception e)
                {
                    continue;
                }
            }
        }  catch (IOException e) {
            System.out.println(">Could not connect. Server caught a corona.");
            System.out.println(e.getMessage());
        }
    }
}