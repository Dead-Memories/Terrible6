import java.io.*;

public class ClientCommandReader {

    public static void start_reading(ObjectInputStream fromServer, ObjectOutputStream toServer, String command) throws IOException, ClassNotFoundException {

        ClientCommandHub hub = new ClientCommandHub(fromServer, toServer);

        command = command.toLowerCase();
        String[] commandParts;
        commandParts = command.split(" ", 2);

                switch (commandParts[0]) {
                    case "":
                        break;
                    case "help":
                        hub.help(command);
                        break;
                    case "info":
                        hub.info();
                        break;
                    case "show":
                        hub.show();
                        break;
                    case "add":
                        hub.add();
                        break;
                    case "update_by_id":
                        hub.update_by_id(Long.parseLong(commandParts[1]));
                        break;
                    case "remove_by_id":
                            hub.remove_by_id(Long.parseLong(commandParts[1]));
                        break;
                    case "clear":
                        hub.clear();
                        break;
                    case "add_if_min":
                            hub.add_if_min();
                        break;
                    case "remove_greater":
                        try {
                            hub.remove_greater(commandParts[1]);}
                        catch (Exception e)
                        {
                            System.out.println("Попробуйте еще раз"); }
                        break;
                    case "remove_lower":
                        try {
                        hub.remove_lower(commandParts[1]);}
                        catch (Exception e)
                            {
                                System.out.println("Попробуйте еще раз"); }
                        break;
                    case "count_greater_than_type":
                        try {
                        hub.count_greater_than_type(commandParts[1]);}
                        catch (Exception e)
                        {
                            System.out.println("Попробуйте еще раз"); }
                        break;
                    case "print_descending":
                        hub.print_descending();
                        break;
                    case "print_field_ascending_official_address":
                        hub.print_field_ascending_official_address();
                        break;
                    default:
                        System.out.println('"' + command + "\" не является командой. Используйте help, чтобы узнать список доступных команд.");
                        break;
                }
                System.out.print('>');
    }
}
