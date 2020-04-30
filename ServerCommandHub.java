import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.stream.Stream;

public class ServerCommandHub {
    ObjectOutputStream toClient;
    ObjectInputStream fromClient;
    Logger logger;
    private HashMap<String, String> CommandHelpList = new HashMap<String, String>();


    public ServerCommandHub(ObjectOutputStream toClient, ObjectInputStream fromClient, Logger logger){
        this.toClient = toClient;
        this.fromClient = fromClient;
        CommandHelpList.put("help", "Команда help выведет справку по доступным командам.");
        CommandHelpList.put("info", "Команда info выведет информацию о коллекции.");
        CommandHelpList.put("show", "Команда show выведет все элементы коллекции.");
        CommandHelpList.put("add", "Команда add добавит новый элемент, созданный по указанным параметрам, в коллекцию.");
        CommandHelpList.put("update_by_id", "Команда update id обновит значение элемента коллекции, id которого равен заданному.");
        CommandHelpList.put("remove_by_id", "Команда remove_by_id удалит из коллекции элемент с указанным id.");
        CommandHelpList.put("clear", "Команда clear очистит коллекцию(пожалусто не надо).");
        CommandHelpList.put("execute_script", "Команда execute_script cчитает и исполнит скрипт из указанного файла.");
        CommandHelpList.put("exit","Команда exit завершит работу программы, сохранив коллекцию в файл.");
        CommandHelpList.put("add_if_min", "Команда add_if_min добавит новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента коллекции.");
        CommandHelpList.put("remove_greater", "Команда remove_greater удалит из коллекции все элементы, превышающие заданный.");
        CommandHelpList.put("remove_lower","Команда remove_lower удалит из коллекции все элементы, превышающие заданный.");
        CommandHelpList.put("count_greater_than_type", "Команда count_greater_than_type выведет количество элементов, значение поля type которых больше заданного.");
        CommandHelpList.put("print_descending", "Команда print_descending выведет элементы коллекции в порядке убывания.");
        CommandHelpList.put("print_field_ascending_official_address", "Команда print_field_ascending_official_address выведет значения поля officialAddress в порядке возрастания.");
        this.logger = logger;
    }

    public void show(TreeSet<Organization> collection) throws IOException {
        Response resp = new Response("");
        collection.forEach(o -> resp.addText(o.toString()));

        this.toClient.writeObject(resp);
        logger.info("Sent response to show command");
    }

    public void info(TreeSet<Organization> collection, String collection_creation_date) throws IOException {
        this.toClient.writeObject(new Response(">Тип коллекции: " + collection.getClass() + '\n'+
                                                    ">Дата создания: " + collection_creation_date +'\n'+
                                                    ">Количество элементов: " +collection.size()+'\n'));
        logger.info("Sent response to info command");
    }

    public void add(TreeSet<Organization> collection) throws IOException, ClassNotFoundException {
        OrganizationWrap wrap = (OrganizationWrap)fromClient.readObject();
        Organization temp = new Organization(wrap.getName(),
                                             wrap.getCoordinates(),
                                             wrap.getCreationDate(),
                                             wrap.getAnnualTurnover(),
                                             wrap.getEmployeesCount(),
                                             wrap.getType(),
                                             wrap.getOfficialAddress());

        collection.add(temp);
        logger.info("Added new element to collection");
        toClient.writeObject(new Response(">Организация успешно добавлена в коллекцию"));
        logger.info("Sent response to add command");
    }
    public void update_by_id(TreeSet<Organization> collection, long id) throws IOException, ClassNotFoundException {

        Stream stream = collection.stream().filter(o -> o.getId()==id);
        OrganizationWrap wrap = (OrganizationWrap)fromClient.readObject();
        if (stream.count()==1) {
            collection.stream().filter(o -> o.getId()==id).findFirst().get().replace(wrap.getName(),
                    wrap.getCoordinates(),
                    wrap.getCreationDate(),
                    wrap.getAnnualTurnover(),
                    wrap.getEmployeesCount(),
                    wrap.getType(),
                    wrap.getOfficialAddress());

            System.out.println(collection.stream().filter(o -> o.getId()==id).findFirst().get().toString());

            toClient.writeObject(new Response(">Элемент по id "+ id + " успешно обновлен"));
            logger.info("Element with id " + id + " updated successfully");
        }
        else  {
            toClient.writeObject(new Response("vse ne ok"));
            logger.info("Attempt to change element by id failed");
        }

    }
    public void remove_by_id(long id, TreeSet<Organization> collection) throws IOException {

        Stream stream = collection.stream().filter(o -> o.getId()==id);
        if (stream.count()==1) {
            collection.remove(collection.stream().filter(o -> o.getId()==id).findFirst().get());
            toClient.writeObject(new Response(">Элемент с заданным id успешно удален"));
            logger.info("Element with id "+ + id + " removed successfully");
        }
        else {
            toClient.writeObject(new Response(">Элемента с таким id не существует"));
            logger.info("Attempt to remove element by id failed");
        }
    }

    public void clear(TreeSet<Organization> collection) throws IOException {
        collection.clear();
        toClient.writeObject(new Response(">Коллекция успешно очищена"));
        logger.info("Collection was cleared");

    }

    public void add_if_min(TreeSet<Organization> collection) throws IOException, ClassNotFoundException {
        OrganizationWrap wrap = (OrganizationWrap)fromClient.readObject();
        Organization temp = new Organization(wrap.getName(),
                wrap.getCoordinates(),
                wrap.getCreationDate(),
                wrap.getAnnualTurnover(),
                wrap.getEmployeesCount(),
                wrap.getType(),
                wrap.getOfficialAddress());

        if (collection.stream().filter(o -> o.compareTo(temp)<0).count()==0)
        {
            collection.add(temp);
            toClient.writeObject(new Response(">Элемент минимален \n" + temp.getName() +" - успешно добавлен в коллекцию"));
            logger.info("New element was successfully added");
        } else {
            toClient.writeObject(new Response(">Элемент " + temp.getName() + " не является минимальным - данный метод не может добавить его в коллекцию"));
            logger.info("Element wasn't added as it isn't minimal");
        }

    }

    public void remove_greater(TreeSet<Organization> collection, String name) throws IOException {

        if (collection.stream().filter(o-> o.getName().toLowerCase().equals(name)).count()>0){
            for (Object o: collection.stream().filter(o -> o.getName().toLowerCase().compareTo(name)>0).toArray())
                collection.remove((Organization) o);
            toClient.writeObject(new Response(">Объекты, бОльшие чем " + name + " ,удалены"));
            logger.info("Organizations were deleted");
        }
        else{
            toClient.writeObject(new Response(">Не найдено организации с названием " + name));
            logger.info("No organizations were deleted as there isn't any element with name " + name);
        }

    }
    public void remove_lower(TreeSet<Organization> collection, String name) throws IOException {

        if (collection.stream().filter(o-> o.getName().toLowerCase().equals(name)).count()>0){
            for (Object o: collection.stream().filter(o -> o.getName().toLowerCase().compareTo(name)<0).toArray())
                collection.remove((Organization) o);
            toClient.writeObject(new Response(">Объекты, меньшие чем " + name + " ,удалены"));
            logger.info("Organizations were deleted");
        }
        else{
            toClient.writeObject(new Response(">Не найдено организации с названием " + name));
            logger.info("No organizations were deleted as there isn't any element with name " + name);
        }

    }

    public void count_greater_than_type(TreeSet<Organization> collection, String type) throws IOException {
        System.out.println(OrganizationType.COMMERCIAL.toString().toLowerCase().compareTo(type));
        toClient.writeObject(new Response(">Найдено " + collection.stream().filter(o -> OrganizationType.valueOf(o.getType()).ordinal()>OrganizationType.valueOf(type.toUpperCase()).ordinal()).count() + " элементов"));
        logger.info("Found " + collection.stream().filter(o -> OrganizationType.valueOf(o.getType()).ordinal()>OrganizationType.valueOf(type.toUpperCase()).ordinal()).count() + "elements");

    }

    public void print_descending(TreeSet<Organization> collection) throws IOException {
        Response resp = new Response("");
        collection.stream().forEachOrdered(o -> resp.addTextForward(o.toString()));
        toClient.writeObject(resp);
        logger.info("Organizations were responded in descending order");
    }

    public void print_field_ascending_official_address(TreeSet<Organization> collection) throws IOException {
        ArrayList<String> addresses = new ArrayList<String>();
        collection.forEach(o -> addresses.add(o.getOfficialAddress()));
        Response resp = new Response("");
        addresses.stream().sorted().forEachOrdered(o->resp.addText(o));
        toClient.writeObject(resp);
        logger.info("Addresses were printed in ascending order");
    }

    void help(String [] commandParts) throws IOException {
        if (commandParts.length==1)
        {
            toClient.writeObject(new Response(">Список доступных команд:\n"  + CommandHelpList.keySet()));
        }
        else
        {
            if (CommandHelpList.containsKey(commandParts[1])) {
                toClient.writeObject(new Response(CommandHelpList.get(commandParts[1])));
            }
            else
                toClient.writeObject(new Response(">Такая команда не найдена"));
        }
        logger.info("A hand of help were given");
    }
}
