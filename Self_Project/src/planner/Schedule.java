// Made with Jesse Inkinen & Ella Harmaala

package planner;
 
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
 
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
 
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.util.ArrayList;
 
/*
 * In this version we are creating a table that does not allow cell edition.
 * See method createCourseTableModel() below
 */
public class Schedule extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final static int MAX_QTY = 20; // Value that defines the max amount of Courses in the application
 
    private ArrayList<Course> allCourses;
    private Course currentCourse;
   
    static JTable tableCourse; 
    static JButton btnAddCourse;
    static JButton btnEditCourse;
    static JButton btnDeleteCourse;
    static DefaultTableModel myCourseTableModel;
   
    //Creating the GUI for application including the tables and buttons
    public Schedule(){
        super("My Course Collection");
 
        new CourseQueries();
       
        setDefaultCloseOperation(EXIT_ON_CLOSE); //For closing the application
        getContentPane().setLayout(null);
        setBounds(0,0,618,540);
        setLocationRelativeTo(null);
 
        JLabel lblTheseAreMy = new JLabel("These are my Courses:");
        lblTheseAreMy.setBounds(222, 11, 168, 14);
        getContentPane().add(lblTheseAreMy);
       
        tableCourse = new JTable();
        tableCourse.setShowVerticalLines(false);
        tableCourse.setShowHorizontalLines(false);
        tableCourse.setShowGrid(false);
        tableCourse.setModel(createCourseTableModel()); // Creating a custom TableModel with Course data from the database       
        tableCourse.setBounds(6, 36, 586, 380);
        getContentPane().add(tableCourse);
       
        btnAddCourse = new JButton("Add Course");//Button to add courses
        btnAddCourse.setBounds(398, 446, 117, 23);
        getContentPane().add(btnAddCourse);
       
        btnDeleteCourse = new JButton("Delete Course");// Button to delete course
        btnDeleteCourse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = tableCourse.getSelectedRow();
                DefaultTableModel model= (DefaultTableModel)tableCourse.getModel();
 
                String selected = model.getValueAt(row, 0).toString();
                               
                            //Option dialog to confirm the delete action
                            if (row >= 0) { int dialogResult = JOptionPane.showConfirmDialog (null, "Would you like to delete this course?","Warning",JOptionPane.OK_CANCEL_OPTION);
                            if(dialogResult == JOptionPane.YES_OPTION){
 
                                model.removeRow(row);
                           
                                try {
                                    // Getting the connection straight from here
                                	//Sending prepared sql statement to database
                                    Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://eu-cdbr-azure-west-b.cloudapp.net:3306/hermas_august", "b88ca230425d11", "6c52fb01");
                                    PreparedStatement ps = (PreparedStatement) conn.prepareStatement("delete from courses where courseName='"+selected+"' ");
                                    ps.executeUpdate();
                                }
                                //Error message
                                catch (Exception w) {
                                    JOptionPane.showMessageDialog(null, "Connection Error!");
                                }  
                     }                                    
                }  
            }     
        } );
        
        btnDeleteCourse.setBounds(45, 443, 117, 29); 
        getContentPane().add(btnDeleteCourse);
       
       
        btnEditCourse = new JButton("Edit Course");
        btnEditCourse.setBounds(222, 443, 117, 29);
        getContentPane().add(btnEditCourse);
 
        
        //Creating event handler for addCourse button
        MyEventHandler commandHandler = new MyEventHandler();
        btnAddCourse.addActionListener(commandHandler);
   
        //Creating event handler for editCourse button
        MyEventHandler2 commandHandler1 = new MyEventHandler2();
        btnEditCourse.addActionListener(commandHandler1);
    }
    /*
     * This method creates a customized TableModel that:
     * 1) contains Course data retrieved from the database (via the allCourses ArrayList)
     */
    private DefaultTableModel createCourseTableModel()
    {
        allCourses = planner.CourseQueries.getAllCourses();
 
        Object[][] data = new Object[allCourses.size()][5];
        String[] columns = new String[] {"Course Name", "Course ID", "Semester", "Status", "Year"};
       
        /*
         * This for loop will populate the fixed array "data" with the rows found from the allCourses ArrayList
         * We need to do this because "data" is passed as an argument to the DefaultTableModel constructor method.
         * We can nor pass an ArrayList as an argument to the DefaultTableModel constructor method
         */
        for (int row=0; row<allCourses.size(); row++){
           
            currentCourse = allCourses.get(row); // get a Course from the ArrayList allCourses
           
            data[row][0] = currentCourse.getCourseName();
            data[row][1] = currentCourse.getCourseId();
            data[row][2] = currentCourse.getSemester();
            data[row][3] = currentCourse.getCourseStatus();
            data[row][4] = currentCourse.getYear();
        }
 
        myCourseTableModel = new DefaultTableModel(data, columns) // "data" contains the Course data from the database
        
    	{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column)  // Disabling cell edition
			{
				return false;
			}
};
       
        return myCourseTableModel;
    }
 
    //Creating the event handler for addCourse button
    private class MyEventHandler implements ActionListener
    {
        public void actionPerformed (ActionEvent myEvent)
        {
            if (myEvent.getSource() == btnAddCourse){
                if (allCourses.size() < MAX_QTY){ // If the current amount of Courses in the database is smaller than MAX_QTY ...
                    getNewCourseFromUser();
                    tableCourse.setModel(createCourseTableModel()); // New TableModel to our table containing up-to-date Course data
                }
                else{
                    JOptionPane.showMessageDialog(null, "You can not add more Courses in your collection", "Info", JOptionPane.INFORMATION_MESSAGE);
                }          
            }
        }
    }
   //Defining the parameters for table that can be selected in new JOptionPane
    private void getNewCourseFromUser(){
        JTextField courseNameField = new JTextField(10);
        JTextField courseIdField = new JTextField(10);
        JComboBox<String> semesterField = new JComboBox<String>();
        JComboBox<String> courseStatusField = new JComboBox<String>();
        JTextField yearField = new JTextField(10);
 
        JPanel myPanel = new JPanel();
       
        myPanel.add(new JLabel("Course Name:"));
        myPanel.add(courseNameField);
       
        myPanel.add(new JLabel("Course ID:"));
        myPanel.add(courseIdField);
 
        myPanel.add(new JLabel("Semester:"));
        myPanel.add(semesterField);
        semesterField.addItem("Spring");
        semesterField.addItem("Autumn");
       
        myPanel.add(new JLabel("Status:"));
        myPanel.add(courseStatusField);
        courseStatusField.addItem("Scheduled");
        courseStatusField.addItem("Ongoing");
        courseStatusField.addItem("Completed");
        courseStatusField.addItem("Failed");
       
        myPanel.add(new JLabel("Year:"));
        myPanel.add(yearField);
       
        int result = JOptionPane.showConfirmDialog(null, myPanel, "Enter details of your new Course", JOptionPane.OK_CANCEL_OPTION); 
       
        if (result == JOptionPane.OK_OPTION) {         //Calling CourseQueries to insert data to database
            // modelYear should be passed as an integer to the addCourse method. Integer.parseInt() is used to convert a String to integer
            planner.CourseQueries.addCourse(courseNameField.getText(), courseIdField.getText(), semesterField.getSelectedItem(), courseStatusField.getSelectedItem(), Integer.parseInt(yearField.getText()));
        }
    }
  
    //Creating the event handler for editCourse button
    private class MyEventHandler2 implements ActionListener {
        public void actionPerformed(ActionEvent thisEvent) {
            {
                if (thisEvent.getSource() == btnEditCourse){
                    if (allCourses.size() <= MAX_QTY){ // Works with any amount of courses
                        getCourseFromUser();
                        tableCourse.setModel(createCourseTableModel()); // Assigning a new TableModel to our table containing up-to-date Course data
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Oops, something went wrong with editing. Please try again!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }          
                }
            }
        }
   
        
        //Creating JPanel to update data in JTable and database
        private void getCourseFromUser(){
           
        DefaultTableModel model = (DefaultTableModel)tableCourse.getModel();
   
        // Get selected row index
        int selectedRowIndex = tableCourse.getSelectedRow();
        String thisRow = model.getValueAt(selectedRowIndex, 0).toString();
           
        	//Defining field types and input quantities
            JTextField courseName = new JTextField(10);
            JTextField courseId = new JTextField(10);
            JComboBox<String> semester = new JComboBox<String>();
            JComboBox<String> courseStatus = new JComboBox<String>();
            JTextField year = new JTextField(10);
           
         JPanel thisPanel = new JPanel();
           
            thisPanel.add(new JLabel("Course Name:")); //Text field (Name)
            thisPanel.add(courseName);
           
            thisPanel.add(new JLabel("Course ID:")); //Text field (Id)
            thisPanel.add(courseId);
 
            thisPanel.add(new JLabel("Semester:")); //Dropdown selection (Semester)
            thisPanel.add(semester);
            semester.addItem("Spring");	// Parameters for dropdown
            semester.addItem("Autumn");	// Parameters for dropdown
           
            thisPanel.add(new JLabel("Status:")); //Dropdown selection (Status)
            thisPanel.add(courseStatus);
            courseStatus.addItem("Scheduled");	// Parameters for dropdown
            courseStatus.addItem("Ongoing"); 	// Parameters for dropdown
            courseStatus.addItem("Completed");	// Parameters for dropdown
            courseStatus.addItem("Failed");		// Parameters for dropdown
           
            thisPanel.add(new JLabel("Year:")); // Text field (Year)
            thisPanel.add(year);
                   
            int result = JOptionPane.showConfirmDialog(null, thisPanel, "Enter the changes", JOptionPane.OK_CANCEL_OPTION);
           
            if (result == JOptionPane.OK_OPTION) {         
                // modelYear should be passed as an integer to the addCourse method. Integer.parseInt() is used to convert a String to integer
                planner.CourseQueries.editCourse(courseName.getText(), courseId.getText(), semester.getSelectedItem(), courseStatus.getSelectedItem(), Integer.parseInt(year.getText()), thisRow);
            }                   
        }      
};
   // Setting frame visible
    public static void main(String[] args) {
        Schedule frame = new Schedule();
        frame.setVisible(true);
    }
}