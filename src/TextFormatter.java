
import java.util.ArrayList;
import java.util.Collections;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author joseph
 */
public class TextFormatter {
    
    public static ArrayList<String> formatNames(ArrayList<String> names)
    {
        ArrayList<String> formattedNames = new ArrayList<>();
        
        for(String name : names)
        {
            String[] parts = name.trim().split("\\s+");
            StringBuilder formattedName = new StringBuilder();
            
            for(String part : parts)
            {
                 part = part.toLowerCase();
                 part = part.substring(0,1).toUpperCase() + part.substring(1);
                 formattedName.append(part).append(" ");
            }
            
            formattedNames.add(formattedName.toString().trim());
        }
        
        Collections.sort(formattedNames);
        
        return formattedNames;
    }
    
}
