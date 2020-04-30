import org.apache.logging.log4j.Logger;
import java.io.*;
import java.util.TreeSet;

public class ServerCommandReader {

    ObjectOutputStream toClient;
    ObjectInputStream fromClient;
    ServerCommandHub hub;
    Logger logger;
public ServerCommandReader(ObjectOutputStream toClient, ObjectInputStream fromClient, Logger logger) {
    this.toClient = toClient;
    this.fromClient = fromClient;
    this.hub = new ServerCommandHub(toClient, fromClient, logger);
    this.logger = logger;
}

public void start_listening(TreeSet<Organization> organizations, String collection_creation_date, File file) throws IOException, ClassNotFoundException {

    String command;
    String[] commandParts;

    command = ((Command) fromClient.readObject()).description;
    logger.info("Got command: " + command);

    commandParts = command.split(" ", 3);

    switch (commandParts[0]) {
        case "help":
            hub.help(commandParts);
            break;
        case "info":
            hub.info(organizations, collection_creation_date);
            break;
        case "show":
            hub.show(organizations);
            break;
        case "add":
            hub.add(organizations);
            break;
        case "update_by_id":
            try {
                hub.update_by_id(organizations, Long.parseLong(commandParts[1]));
            } catch (Exception e) {
                toClient.writeObject(new Response(">В коллекции нет элемента с указанным id. Повторите ввод"));
            }
            break;
        case "remove_by_id":
            hub.remove_by_id(Long.parseLong(commandParts[1]), organizations);
        case "clear":
            hub.clear(organizations);
            break;
        case "add_if_min":
            hub.add_if_min(organizations);
            break;
        case "remove_greater":
            try {
                hub.remove_greater(organizations, commandParts[1]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            break;
        case "remove_lower":
            hub.remove_lower(organizations, commandParts[1]);
            break;
        case "count_greater_than_type":
            try {
                hub.count_greater_than_type(organizations, commandParts[1]);
            } catch (Exception e) {
                System.out.println("Попробуйте еще раз");
            }
            break;
        case "print_descending":
            hub.print_descending(organizations);
            break;
        case "print_field_ascending_official_address":
            hub.print_field_ascending_official_address(organizations);
            break;

    }
}
}



