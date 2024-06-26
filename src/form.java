
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


class Query {

    static final String DB_URL = "jdbc:mysql://localhost:3306/db_employer";
    static final String USER = "root"; 
    static final String PASS = ""; 
    private String sql_Query ="";
    private String main_table ="";
    private int wherecount = 0;
    private int orwhere = 0;
    private int is_select = 0;
    private List<String> columns = new ArrayList<>();
    private List<String> query_data = new ArrayList<>();
    private List<Map<String, Object>> get_data = new ArrayList<>();





     /**
     * This Create method is used to store a record in the MySql Database.
     * When it stores the record it return the id of the record - Written By Kyle Tasmoredjo.
     * @param   table_name   the name of the table.
     * @param   column_names   an array of colums that can be defined in a class.
     * @param   input_data   an array of inputs data that is recieved and used for the records.
     * @return  it return the newly created ID
     * 
     */



    public int Create(String table_name,  String[] column_names,  String[] input_data){

        StringBuilder Column_builder = new StringBuilder();
        StringBuilder Prepared_values = new StringBuilder();
        int generatedId;
      
       for (int i = 0; i < column_names.length; i++) {
            if (i > 0) {
                Column_builder.append(", ");
                Prepared_values.append(", ");
            }
            Column_builder.append(column_names[i]);
            Prepared_values.append("?");
        }
        String sql = "INSERT INTO "+ table_name +" ("+ Column_builder +") VALUES ("+ Prepared_values+")";
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            
                PreparedStatement preparedStatement = connection.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
                for (int x = 1; x <= input_data.length; x++) {
                    preparedStatement.setString(x, input_data[x-1]);
                }
                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            generatedId = generatedKeys.getInt(1);  

                            return generatedId;
                        }
                    }
                    
                }
            } catch (SQLException e) {
                    System.out.println(e);
                  return 0;
            }
        return 100000000;
    }

    
      /**
     * This Update method is used to update a record in the MySql Database.
     * When it updated the record it return the id of the record - Written By Kyle Tasmoredjo.
     * @param   table  the name of the table.
     * @param   column_names   an array of colums that can be defined in a class where there needs to be an update.
     * @param   input_data   an array of inputs data that is recieved and used for the records to update.
     * @return  it return the newly created ID
     * 
     */

    public Query Update(String table , String[] column_names, String[] input_data){
        StringBuilder Column_builder = new StringBuilder();
        this.main_table = table;
         for (int i = 0; i < column_names.length; i++) {
            if (i > 0) {
                Column_builder.append(" , "); 
            }
            Column_builder.append(column_names[i]);
            Column_builder.append( " = ?");
           }

         this.sql_Query = "UPDATE "+ table +" SET "+ Column_builder  ;

         int x = 1;
  
        this.query_data =   new ArrayList<>(Arrays.asList(input_data));

         return this;
    }



     /**
     * This Where method is used to add conditions to the query, for complex data modification, 
     * data retrieving - Written By Kyle Tasmoredjo.
     * @param   column   the column that needs to search
     * @param   search_value   the value that is needed to search
     * @return  return partial statement
     * 
     */


    public Query Where(String column, String search_value){
        if (!this.sql_Query.contains("WHERE")) {
          this.sql_Query += " WHERE "+ column + " = ?";
        } else {
           this.sql_Query += " AND "+ column + " = ?";
        }

         this.query_data.add(search_value);
      
        wherecount++;
        return this;
    }


     /**
     * This orWhere method is used to add conditions to the query, for complex data modification, 
     * data retrieving - Written By Kyle Tasmoredjo.
     * @param   column   the column that needs to search
     * @param   search_value   the value that is needed to search
     * @return  return partial statement
     * 
     */


    public Query OrWhere(String column, String search_value ){
        
        this.sql_Query += " OR "+ column + " = ?";
        this.query_data.add(search_value);
        orwhere++;

        return this;
    }

     /**
     * This method is used to execute the query, without adding this to your code. It won't execute. - writtne by Kyle Tasmoredjo
     * 
     */

    public Query Execute(){
        
        int x = 1;
         if(!this.query_data.isEmpty() && this.is_select != 1){
             try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement preparedStatement = connection.prepareStatement(this.sql_Query)) {
                    
                
                        for (String data : this.query_data) {
                                preparedStatement.setString(x, data);
                                x++;
                        } 
                    
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
         }else{
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement preparedStatement = conn.prepareStatement(this.sql_Query)) {

                for (int i = 0; i < this.query_data.size(); i++) {
                    preparedStatement.setString(i + 1, this.query_data.get(i));
                }

                try (ResultSet rs = preparedStatement.executeQuery()) {
                      ResultSetMetaData md = rs.getMetaData();
                    int columns = md.getColumnCount();
                    List<Map<String, Object>> list = new ArrayList<>();
                    while (rs.next()) {
                        for (String column : this.columns) {
                            Object value = rs.getObject(column);
                       
                            //  System.out.print(column + ": " + value + " ");
                        }
                        System.out.println();
                        Map<String, Object> row = new HashMap<>(columns);
                        int xxx = 0;
                        for (int i = 1; i <= columns; ++i) {
                          //   System.out.println(md.getColumnName(i) + ": " + rs.getObject(i) + " ");
                            row.put(this.columns.get(xxx), rs.getObject(i));
                            xxx++;
                           
                        }
                         list.add(row);
                      
                    }

                   
                        
                //  System.out.println(Arrays.toString(list.toArray()));
                  
                     this.get_data = list;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
         }

       


        this.sql_Query = "";
   

        return this;
    }

      /**
     * The join method is used to join tables togheter, this is used to vconnect two tables- Written By Kyle Tasmoredjo.
     * @param   secondary_table   the column that needs to search
     * @param   primary_key   the value that is needed to search
     * @param   secondary_key   the value that is needed to search
     * @return  return partial statement
     * 
     */

    public Query Join(String secondary_table, String primary_key, String secondary_key){

        String joinClause = " LEFT JOIN " + secondary_table + " ON " + this.main_table + "." + primary_key + "=" + secondary_table + "." + secondary_key;

        if (this.sql_Query.toLowerCase().contains("update")) {
            int setIndex = this.sql_Query.toLowerCase().indexOf(" set ");
            if (setIndex != -1) {
                StringBuilder queryBuilder = new StringBuilder(this.sql_Query);
                queryBuilder.insert(setIndex, joinClause);
                this.sql_Query = queryBuilder.toString();
            }
        } else if (this.sql_Query.toLowerCase().contains("delete")) {
              int fromIndex = this.sql_Query.toLowerCase().indexOf(" from ");
                if (fromIndex != -1) {
                    StringBuilder queryBuilder = new StringBuilder(this.sql_Query);
                    // Insert main_table right after DELETE and before FROM
                    queryBuilder.insert("delete ".length(), this.main_table + " ");
                    queryBuilder.append(joinClause);
                    this.sql_Query = queryBuilder.toString();
                }
            
        } else {
            this.sql_Query += joinClause;
        }

        return this;
    }

     /**
     * The delete method is used to remove a record from a table - Written By Kyle Tasmoredjo.
     * @param   table_name   the table name to look which table record needs to be removed
     * @return  return partial statement
     * 
     */

    public Query Delete(String table_name){
        this.main_table = table_name;
        this.sql_Query += "DELETE FROM "  + table_name;

       return this;
    }



     /**
     * The select method is used to retrive data from the database- Written By Kyle Tasmoredjo.
     * @param   table   the primary table from where it start retrieving data
     * @param   columns   the columns that will be retrieved
     * @return  return partial statement
     * 
     */
    public Query Select(String table , String[] columns){
        StringBuilder Column_builder = new StringBuilder();
        this.is_select = 1;
        this.main_table = table;
        this.columns.clear();
         for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                Column_builder.append(" , "); 
            }
            Column_builder.append(columns[i]);
             this.columns.add(columns[i]);
     
           }
            this.sql_Query = "SELECT "+ Column_builder +" FROM "+   this.main_table ;


       
     
        return this;
    }

    public void ViewAll(){
    }

    void Display(){
        System.out.println(sql_Query);
        // System.out.println(this.main_table);
        // System.out.println(query_data);
        this.sql_Query = "";

    }


     /**
     * returns data as a object - Written By Kyle Tasmoredjo.
     * @return  return object
     * 
     */
    List<Map<String, Object>> Objectify(){
        // System.out.println(sql_Query);
        // System.out.println(this.main_table);
        // System.out.println(query_data);
        return this.get_data;

    }



}

class Person extends Query{
    String table_name = "students";
    String[] columns = {"name","last_name","student_number", "age", "birthyear"};
    
  
}

class Team extends Query{
    String table_name = "teams";
    String[] columns = {"team_id","name"};
}

class Contact extends Query{
    String table_name = "contacts";
    String[] columns = {"email","address","phone_number"};
}

class Experience extends Query{
    String table_name = "skills";
    String[] columns = {"skill"};
}


public class form {
      public static void main(String[] args) { 
        boolean enabled = true;       


        while (enabled) {
            int menu_option = StartMenu();
            switch (menu_option) {
                case 1 -> {

                    String[] questions = {"Hoe heet de persoon?", "Wat is de persoons achternaam?", "Wat is de student nummer", "Hoe oud is de persoon?","Wat is de geboorte datum(yyyy-mm-dd)?"};
                    String[] inputs = Questionbuilder(questions);
                    Person user = new Person();
                    int user_id =  user.Create(user.table_name, user.columns, inputs);
                
                    String[] ja_or_nee = {"JA","NEE"};
                    String[] input_contact_ja_or_nee =  SelectBuilder("Heeft de persoon contact gegevens?", ja_or_nee, false);
                    String[] input_team_ja_or_nee =  SelectBuilder("Heeft de persoon een team?", ja_or_nee, false);
                    
                    if ("0".equals(input_contact_ja_or_nee[0])) {
                        Contact user_contact = new Contact();
                        String[] contact_questions = {"Wat is je contact email?","Wat is je addres?","Wat is je telefoon nummer?"};
                        String[] inputs_contact = Questionbuilder(contact_questions);
                        int user_contact_id = user_contact.Create(user_contact.table_name, user_contact.columns, inputs_contact);
                        String[] updated_value = {String.valueOf(user_contact_id)};
                        String[] updated_columns = {"contact_id"};
                       user.Update("students", updated_columns , updated_value).Where("student_id", String.valueOf(user_id)).Execute().Display();
                    }
      

                    if ("0".equals(input_team_ja_or_nee[0])) {
                        Team find_team = new Team();
                        int id;
                        String[] teams_questions = {"Wat is de team naam?"};
                        String[] inputs_teams = Questionbuilder(teams_questions);           
                        List<Map<String, Object>> team_data = new ArrayList<>();
                        team_data = find_team.Select("teams", find_team.columns).Where("name", inputs_teams[0]).Execute().Objectify();
                        // System.out.println(Arrays.toString(team_data.toArray()));
                        if (team_data != null && !team_data.isEmpty()) {
                             id = (int) team_data.get(0).get("team_id");
                          
                        }else{
                            String[] columns = {"name"};
                             id = find_team.Create(find_team.table_name,columns, inputs_teams);

                        }
                        

                        String[] updated_value = {String.valueOf(id)};
                        String[] updated_columns = {"team_id"};
                         user.Update("students", updated_columns , updated_value).Where("student_id", String.valueOf(user_id)).Execute();
                    }

                    String[] skill_questions = {"Wat is de persoon skill?"};
                    String[] skill_inputs = Questionbuilder(skill_questions);
                    Experience skill = new Experience();
                    int skill_id = skill.Create(skill.table_name, skill.columns, skill_inputs);
                    String[] updated_value = {String.valueOf(skill_id)};

                    String[] update_skill_column = {"skill_id"};
                    user.Update("students", update_skill_column, updated_value).Where("student_id", String.valueOf(user_id)).Execute();





                    System.out.println("User Inserted");
                

                 



                        
                 
                }
                case 2 -> {
                 
                    String[] tables = 
                    {
                        "Persoon", 
                        "Contact",
                        "Skill",
                        "Team",
                       
                    };

                 
                  
                   
                    String[] table_to_update = SelectBuilder("Welk onderdeel wilt u updaten?", tables , false);

                    if(table_to_update.length == 0){
                        break;
                    }

                    switch (table_to_update[0]) {
                        case "0" -> {
                            List<String>columns_to_update = new ArrayList<>();

                            String table = "students";
                            String[] columns = {
                                "Naam",
                                "Familie naam",
                                "Student nummer",
                                "Leeftijd",
                                "Geboortedatum",
                            };

                            String[] fields = {
                                "name",
                                "last_name",
                                "student_number",
                                "age",
                                "birthyear",
                            };

                            List<String>generated_questions = new ArrayList<>();
                            String[] updated_columns = SelectBuilder("Welk gegevens wilt u updaten?", columns , true);
                             if(updated_columns.length == 0){
                                    break;
                                }
                            for (String string : updated_columns) {
                                columns_to_update.add(fields[Integer.parseInt(string)]);
                                generated_questions.add("Wat zal de nieuwe "+ columns[Integer.parseInt(string)] +" zijn? ");
                                System.err.println(string);
                            }
                            String[] student_number_question = {"Wat is de studenten nummer van de persoon die u wilt updaten?"};
                            String[] student_id = Questionbuilder(student_number_question);
                        
                            Person user = new Person();
                            String[] update_columns = columns_to_update.toArray(new String[0]);
                            String[] update_questions = generated_questions.toArray(new String[0]);
                            String[] inputs = Questionbuilder(update_questions);

                            user.Update(table, update_columns, inputs).Where("student_number", student_id[0]).Execute();

                            System.out.println("Updated");
                    }
                        case "1" -> {
                            List<String>columns_to_update_x = new ArrayList<>();

                            String table_x = "contacts";
                            String[] columns_x = {
                                "Email",
                                "Address",
                                "Telefoon nummer",
                                
                            };

                            String[] fields_x = {
                                "email",
                                "address",
                                "phone_number",
                                
                            };

                            List<String>generated_questions_x = new ArrayList<>();
                            String[] updated_columns_x = SelectBuilder("Welk gegevens wilt u updaten?", columns_x , true);
                              if(updated_columns_x.length == 0){
                                    break;
                                }
                            for (String string : updated_columns_x) {
                                columns_to_update_x.add(fields_x[Integer.parseInt(string)]);
                                generated_questions_x.add("Wat zal de nieuwe "+ columns_x[Integer.parseInt(string)] +" zijn? ");
                                System.err.println(string);
                            }
                            String[] student_number_question_x = {"Wat is de studenten nummer van de persoon die u wilt updaten?"};
                            String[] student_id_x = Questionbuilder(student_number_question_x);
                        
                            Person user_x = new Person();
                            String[] update_columns_x = columns_to_update_x.toArray(new String[0]);
                            String[] update_questions_x = generated_questions_x.toArray(new String[0]);
                            String[] inputs_x = Questionbuilder(update_questions_x);

                            user_x.Update(table_x, update_columns_x, inputs_x).Join("students", "contact_id", "contact_id").Where("student_number", student_id_x[0]).Execute();

                            System.out.println("Updated");
                    }
                        case "2" -> {
                            List<String>columns_to_update_y = new ArrayList<>();

                            String table_y = "skills";
                            String[] columns_y = {
                                "Skill",
                                
                                
                            };

                            String[] fields_y = {
                                "skill",
                                
                                
                            };

                            List<String>generated_questions_y = new ArrayList<>();
                            String[] updated_columns_y = SelectBuilder("Welk gegevens wilt u updaten?", columns_y , true);
                              if(updated_columns_y.length == 0){
                                    break;
                                }
                            for (String string : updated_columns_y) {
                                columns_to_update_y.add(fields_y[Integer.parseInt(string)]);
                                generated_questions_y.add("Wat zal de nieuwe "+ columns_y[Integer.parseInt(string)] +" zijn? ");
                                System.err.println(string);
                            }
                            String[] student_number_question_y = {"Wat is de studenten nummer van de persoon die u wilt updaten?"};
                            String[] student_id_y = Questionbuilder(student_number_question_y);
                        
                            Person user_y = new Person();
                            String[] update_columns_y = columns_to_update_y.toArray(new String[0]);
                            String[] update_questions_y = generated_questions_y.toArray(new String[0]);
                            String[] inputs_y = Questionbuilder(update_questions_y);

                            user_y.Update(table_y, update_columns_y, inputs_y).Join("students", "skill_id", "contact_id").Where("student_number", student_id_y[0]).Execute();
                            
                            System.out.println("Updated");
                    }
                        case "3" -> {
                            List<String>columns_to_update_z = new ArrayList<>();

                            String table_z = "teams";
                            String[] columns_z = {
                                "Team naam",
                                
                                
                            };

                            String[] fields_z = {
                                "teams.name",
                                
                                
                            };

                            List<String>generated_questions_z = new ArrayList<>();
                            String[] updated_columns_z = SelectBuilder("Welk gegevens wilt u updaten?", columns_z , true);
                                  if(updated_columns_z.length == 0){
                                    break;
                                }
                            for (String string : updated_columns_z) {
                                columns_to_update_z.add(fields_z[Integer.parseInt(string)]);
                                generated_questions_z.add("Wat zal de nieuwe "+ columns_z[Integer.parseInt(string)] +" zijn? ");
                                System.err.println(string);
                            }
                            String[] student_number_question_z = {"Wat is de studenten nummer van de persoon die u wilt updaten?"};
                            String[] student_id_z = Questionbuilder(student_number_question_z);
                        
                            Person user_z = new Person();
                            String[] update_columns_z = columns_to_update_z.toArray(new String[0]);
                            String[] update_questions_z = generated_questions_z.toArray(new String[0]);
                            String[] inputs_z = Questionbuilder(update_questions_z);

                            user_z.Update(table_z, update_columns_z, inputs_z).Join("students", "team_id", "team_id").Where("student_number", student_id_z[0]).Execute();

                            System.out.println("Updated");
                    }
                    
                       
                    }
                   

                }
                case 3 -> {
                  
                        String[] questions = {"Teams","Contact", "Skills","Person"};
                        String[] tables = {"teams","contacts", "skills", "students"};
                        String[] student_questions = {"Wat is de studenten nummer van de persoon die u wilt verwijderen?"};
                        String[] student_id= Questionbuilder(student_questions);
                        String[] inputs = SelectBuilder("Wat wilt u verwijderen", questions, true);
                      
                      
                         for (String string : inputs) {

                                switch (Integer.parseInt(string)) {
                                    case 0 -> {
                                        Team delete_team = new Team();
                                        delete_team.Delete(tables[0]).Join("students", "team_id", "team_id").Where("student_number",student_id[0]).Execute ();
                                }
                                    case 1 -> {
                                        Contact delete_contact = new Contact();
                                        delete_contact.Delete(tables[1]).Join("students", "contact_id", "contact_id").Where("student_number",student_id[0]).Execute();
                                }
                                    case 2 -> {
                                        Experience delete_skill = new Experience();
                                        delete_skill.Delete(tables[2]).Join("students", "skill_id", "skill_id").Where("student_number",student_id[0]).Execute();
                                }
                                    case 3 -> {
                                        Person deleted_user = new Person();
                                        deleted_user.Delete(tables[3]).Where("student_number",student_id[0]).Execute();
                                }
                                
                                
                                }
        
                            }

                      

                       



                }
                case 4 -> {
                    enabled = false;

                    String[] questions = {"Van wie wilt u de informatie weten (student_number)?"};
                    String[] input = Questionbuilder(questions);
                    String[] columns = {"students.name","students.last_name","students.student_number","students.age","students.birthyear","contacts.email","contacts.address","contacts.phone_number","teams.name","skills.skill"};
                    Person get_user = new Person();
                   List<Map<String, Object>>  users_data =  get_user.Select("students", columns).Join("contacts", "contact_id", "contact_id").Join("teams", "team_id", "team_id").Join("skills", "skill_id", "skill_id").Where("students.student_number",input[0] ).Execute().Objectify();

                // System.out.println(Arrays.toString(users_data.toArray()));

                   if (!users_data.isEmpty()) {
                    Map<String, Object> firstEntry = users_data.get(0);
                    List<String> headers = new ArrayList<>(firstEntry.keySet());

          
                    System.out.println(createRow(headers));

            
                    System.out.println(createSeparator(headers.size()));

             
                    for (Map<String, Object> row : users_data) {
                        List<Object> values = new ArrayList<>(row.values());
                        System.out.println(createRow(values));
                    }
                } else {
                    System.out.println("No data available.");
                }
              

                }
                case 5 -> {
                 enabled = false;
                Person get_user = new Person();
                List<Map<String, Object>> users_data = get_user.Select("students", get_user.columns).Execute().Objectify();

               
                if (!users_data.isEmpty()) {
                    Map<String, Object> firstEntry = users_data.get(0);
                    List<String> headers = new ArrayList<>(firstEntry.keySet());

          
                    System.out.println(createRow(headers));

            
                    System.out.println(createSeparator(headers.size()));

             
                    for (Map<String, Object> row : users_data) {
                        List<Object> values = new ArrayList<>(row.values());
                        System.out.println(createRow(values));
                    }
                } else {
                    System.out.println("No data available.");
                }
                 

                }
                case 6 -> {
                    enabled = false;

                }
                
            
               
            }
        }
      

        

    }


      public static int StartMenu() {
        int option = -1;
        Scanner menu_input = new Scanner(System.in);

        while (true) {
            System.out.println("Welcome to Program X");
            System.out.println("To start, what would you like to do?");
            System.out.println("------------------------------------");
            System.out.println("1-Add a person");
            System.out.println("2-Update a person");
            System.out.println("3-Delete a person");
            System.out.println("4-View a person");
            System.out.println("5-View all");
            System.out.println("6-Quit");
            System.out.println("------------------------------------");
            System.out.println("Please Select an option");
            try {
                option = menu_input.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                menu_input.next(); // Clear the invalid input.
            }
        }

        return option;
    }

     /**
     * An method to display Questions, thats inputs into an array- Written By Kyle Tasmoredjo.
     * @param   questions an array of Questions that will displayed
     * @return  It rertun an array
     */

    public static String[] Questionbuilder( String[] questions){
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
        Scanner input_val = new Scanner(System.in);
        List<String> list_values = new ArrayList<String>();
        for (int idx = 0; idx < questions.length; idx++) {
            System.out.println(questions[idx]);
            list_values.add(input_val.next());
            
        }

        String[] input_values = list_values.toArray(String[]::new );

        return input_values;
        
    }

     /**
     * An method to display A Select menu, thats set inputs into an array- Written By Kyle Tasmoredjo.
     * @param   questions an array of Questions that will displayed
     * @return  It rertun an array
     */


     public static String[] SelectBuilder(String header, String[] select_fields, boolean return_array) {
        Scanner input_val = new Scanner(System.in);
        List<String> list_values = new ArrayList<>();
        Set<Integer> selectedIndices = new HashSet<>();
        
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
        boolean select_loop = true;
        while (select_loop) {
     
            System.out.println(header);
            System.out.println("------------------------------------");
            for (int idx = 0; idx < select_fields.length; idx++) {
                if (!selectedIndices.contains(idx)) {
                    System.out.println(idx + " - " + select_fields[idx]);
                }
            }
            System.out.println("------------------------------------");
            System.out.println("Please select an option, type 'stop' if you are done selecting:");
            
            String input = input_val.next();
           
            if ("stop".equals(input)) {
                break;
            } else {
                try {
                    int selectedIndex = Integer.parseInt(input);
                    if (selectedIndex >= 0 && list_values.size() < select_fields.length ) {
                        if (selectedIndices.contains(selectedIndex)) {
                            System.out.println("Item already selected, please choose a different one.");
                        } else {
                            selectedIndices.add(selectedIndex);
                            list_values.add(String.valueOf(selectedIndex));
                            if (!return_array) {
                                select_loop = false;
                            }
                        }
                    } else {
                        System.out.println("Invalid selection, please try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input, please enter a number.");
                }
            }
        }

        String[] input_values = list_values.toArray(new String[0]);
        return input_values;
    }


      private static String createRow(List<?> columns) {
        StringBuilder row = new StringBuilder();
        for (Object column : columns) {
            row.append(String.format("%-20s", column)).append(" | ");
        }
        return row.toString();
    }

    private static String createSeparator(int columnCount) {
        StringBuilder separator = new StringBuilder();
        for (int i = 0; i < columnCount; i++) {
            separator.append("------------------------");
        }
        return separator.toString();
    }

  

}
