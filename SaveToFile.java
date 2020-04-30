import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.TreeSet;

public class SaveToFile {
    public static void saveToFile(TreeSet<Organization> collection, File file) throws FileNotFoundException {

        JSONArray organizations = new JSONArray();

        for (Organization a: collection) {
            JSONObject one = new JSONObject();

            one.put("id", a.getId());
            one.put("name", a.getName());
            JSONObject coords = new JSONObject();
            coords.put("x",a.getCoordinates().getX());
            coords.put("y",a.getCoordinates().getY());
            one.put("coordinates", coords);
            one.put("creationDate", a.getCreationDate());
            one.put("annualTurnover", a.getAnnualTurnover());
            one.put("employeesCount", a.getEmployeesCount());
            if (a.getType()==null)
                one.put("type", JSONObject.NULL);
            else
                one.put("type", a.getType());

            try{
                one.put("officialAddress", a.getOfficialAddress().toString());
            }
            catch (Exception e)
            {
                one.put("officialAddress", JSONObject.NULL);
            }

            organizations.put(one);
        }

        PrintWriter writer = new PrintWriter(file);
        writer.write(organizations.toString());
        writer.close();
        System.out.println("Коллекция успешно сохранена в файл " + file);
    }
}
