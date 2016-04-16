import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Programmer: Sean M. DePotter
 * Filename: CarTracker.java
 * Due Date: 04/15/2016
 * Description: Individual Assignment #2 - CarTracker Class
 */
public class CarTracker extends JFrame {

    //file IO default name
    final private String INPUT_FILE = "GasMileage.txt";

    //spread the INPUT_FILE's data across a  2darray 5 wide, 6 tall
    final private int COLUMNS = 5;
    final private int ROWS = 6;
    private String[][] fileData = new String[ROWS][COLUMNS];

    //set window size to 500x300 to resolve issue with CTM * rating label in a grid layout. Name the window CTM
    final private int DEFAULT_WIDTH = 500;
    final private int DEFAULT_HEIGHT = 300;
    final private String TITLE = "CTM";

    //Declare the southern panel in the BorderLayout
    private JPanel bottomPanel;
    private JComboBox modelComboBox, yearComboBox;
    private JLabel modelYearLabel, manufacturerLabel;

    //Declare northern panel to hold the label with the gas photo
    private JPanel imagePanel;
    private JLabel imageInput;

    //Declare menu bar with File>Exit and Help>About
    private JMenuBar menuBar;
    private JMenu fileMenu, helpMenu;
    private JMenuItem exitMenuItem, aboutMenuItem;

    //Declare and group radio buttons lpg and mpg
    private ButtonGroup group;
    private JRadioButton mpgRadioButton;
    private JRadioButton lpgRadioButton;

    //Declare a label to hold the default CTM rating for the car on program load
    private JLabel ctmLabel;
    private String rating = "**";

    //declare textfield to hold the MPG/LPG mileage beside the mileage label
    private JTextField mileageTextField;
    private JLabel mileageLabel;

    //declare the default mileage type for use until changed by the radio buttons
    private String mileageString = "mpg";

    //declarations of the where the item selected by the user, in the combobox, is in the array
    private int itemComboBoxIndex;
    private int modelComboBoxIndex;

    //declare defaults for displaying when the window opens based on file's first entry
    private double mileageMpg = 22.7;
    private double mileageLpg;

    //grants access to mileage conversion methods and rating determining methods.
    // Since the class is accessed by a object created in the main method, the main method does access this class
    MileageCalculator mileageCalculator = new MileageCalculator();

    public CarTracker(){
        //create the bottom panel for storing GUI elements other than image
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(5,1,5,5));

        //assign the file's data to 2Darray/combo boxes
        getFile();

        //set windows properties
        setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
        setTitle(TITLE);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //hold Bottom panel elements in South section, hold image panel elements in Center section
        setLayout(new BorderLayout());

        //creation of menuBar and menubar elements File & Help
        buildMenuBar();

        //creation of the center and southern panels, the Image and the data fields.
        buildImagePanel();
        buildBottomPanel();

        //creation of textfields, labels, radio buttons for BottomPanel
        generateRadioButtonElements();
        generateCtmRating();
        generateMileage();

        //add model year, manufacturer, mpg/lpg, mileage, and CTM rating elements to the southern panel
        bottomPanel.add(modelYearLabel);
        bottomPanel.add(modelComboBox);
        bottomPanel.add(manufacturerLabel);
        bottomPanel.add(yearComboBox);
        bottomPanel.add(mpgRadioButton);
        bottomPanel.add(lpgRadioButton);
        bottomPanel.add(mileageLabel);
        bottomPanel.add(mileageTextField);
        bottomPanel.add(ctmLabel);

        add(bottomPanel, BorderLayout.SOUTH);
        add(imagePanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public void generateMileage(){
        //hold the default mpg in the label when the program begins
        mileageLabel = new JLabel("Mileage (" + mileageString + "):");

        //Combo box inputs were converted to indexes and then used to get the value from
        // the array for display in non user editable textfield
        mileageTextField = new JTextField(fileData[itemComboBoxIndex +1][modelComboBoxIndex +1]);
        mileageTextField.setEditable(false);
    }

    public void generateCtmRating(){
        //Assign the label the default rating for the 2012 BMW (first item in array), center the inside Gridlayout
        ctmLabel= new JLabel("CTM Mileage Rating (0-4 Stars): " + rating);
        ctmLabel.setHorizontalAlignment(JLabel.CENTER);
    }

    public void generateRadioButtonElements(){
        group = new ButtonGroup();

        //text for the default selected radio button, given a tooltip and a M is the mnemonic key.
        // when selected, mpg value of the selected model/year will show alongside appropriate CTM rating.
        mpgRadioButton = new JRadioButton("Miles Per Gallon");
        mpgRadioButton.setMnemonic(KeyEvent.VK_M);
        mpgRadioButton.setToolTipText("Select for miles per gallon conversion");
        mpgRadioButton.setSelected(true);
        mpgRadioButton.addActionListener(new MpgRadioListener());

        //text for nondefault selected radio button. L mnemonic key, converts MPG
        // for selected model/year,& sets CTM
        lpgRadioButton = new JRadioButton("Liters per 100KM");
        lpgRadioButton.setMnemonic(KeyEvent.VK_L);
        lpgRadioButton.setToolTipText("Select for L/100km conversion");
        lpgRadioButton.addActionListener(new LpgRadioListener());

        group.add(mpgRadioButton);
        group.add(lpgRadioButton);
    }

    public void buildBottomPanel() {
        //initialize file input to assign values to combo boxes
        getFile();

        //assignment of each model to a row of the array
        modelComboBox = new JComboBox();
        for (int index =1; index< ROWS; index++){
            modelComboBox.addItem(fileData[index][0]);
        }

        //fill desired mileage textfield based on selected model/year
        modelComboBox.addActionListener(new ComboListener());

        //assignment of each year to a column of the array
        yearComboBox = new JComboBox();
        for (int index = 1; index<COLUMNS; index++){
            yearComboBox.addItem(fileData[0][index]);
        }

        //fill desired mileage textfield based on selected year/model
        yearComboBox.addActionListener(new ComboListener());

        modelYearLabel = new JLabel("Model Year:");
        manufacturerLabel = new JLabel("Manufacturer");
    }

    public void buildImagePanel(){
        //default filename for gas pump image
        final ImageIcon IMAGE = new ImageIcon("GasPump.png");

        imagePanel = new JPanel();

        //initialize a label to hold the gaspump image
        imageInput = new JLabel();
        imageInput.setIcon(IMAGE);

        //add to the centered panel
        imagePanel.add(imageInput);
    }

    public void buildMenuBar(){
        menuBar = new JMenuBar();

        //build File menu with the item(s) Exit
        buildFileMenu();
        //build Help menu with the item(s) About
        buildHelpMenu();

        //both menus are added to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    public void buildFileMenu(){
        //create the exit item in the File menu, shortcut Alt+X
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        exitMenuItem.addActionListener(new ExitListener()); // exits the program

        //create File Menu, shortcut Alt+F
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu.add(exitMenuItem);
    }

    public void buildHelpMenu(){
        //create the About item in the Help menu, shortcut Alt+A
        aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        aboutMenuItem.addActionListener(new HelpListener());

        //create Help Menu, shortcut Alt+H
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        helpMenu.add(aboutMenuItem);
    }

    private class ComboListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            //take the index selected via the combo box and assign value to variables
            itemComboBoxIndex = modelComboBox.getSelectedIndex() +1;
            modelComboBoxIndex = yearComboBox.getSelectedIndex() +1;

            //derive mileage from retrieved combo box indexes
            mileageMpg = Double.parseDouble(fileData[itemComboBoxIndex][modelComboBoxIndex]);

            //check if LPG is selected and perform conversion, if not then use MPG
            if(lpgRadioButton.isSelected()){
                mileageLpg = mileageCalculator.convertMpgToLiters(mileageMpg);
                mileageTextField.setText(String.format("%.1f",mileageLpg));
            }
            else {
                mileageTextField.setText(Double.toString(mileageMpg));
            }

            //determine CTM mileage rating based on strictly mpg mileage
            rating = mileageCalculator.determineRating(mileageMpg);

            //set the text to the label based on a combo box item change and the rating given
            ctmLabel.setText("CTM Mileage Rating (0-4 Stars): " + rating);
        }
    }

    private class ExitListener implements ActionListener{
        //close widow when Exit is selected from File menu
        public void actionPerformed(ActionEvent e)
        {
            System.exit(0);
        }
    }

    private class HelpListener implements ActionListener{
        //about the creator dialog box from the Help Menu
        public void actionPerformed(ActionEvent e){
            JOptionPane.showMessageDialog(null,"Mileage Lookup Application\nCopywright 2016 - S. DePotter");
        }
    }

    private class MpgRadioListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            //set default mileage string and value for when program initializes inside label and textfield
            mileageString = "mpg";
            mileageLabel.setText("Mileage (" + mileageString + "):");
            mileageTextField.setText(Double.toString(mileageMpg));
        }
    }

    private class LpgRadioListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            //set default mileage string, for lpg, and value for when program initializes inside label and textfield
            mileageString = "L/100km";
            mileageLabel.setText("Mileage (" + mileageString + "):");
            mileageLpg = mileageCalculator.convertMpgToLiters(mileageMpg);
            mileageTextField.setText(String.format("%.1f",mileageLpg));
        }
    }

    public void getFile(){
        Scanner input;
        String fileRow;

        //hold each row item from the text document
        String[] rowItems;

        File file;

        //attempt to locate and assign file contents to array via tokenization, if not then display error mesagge
        try{
            file = new File(INPUT_FILE);
            input = new Scanner(file);
            input.nextLine();

            //tokenize values from data file & assign each rows a value and then each column
            // in that row a value relative to the text document, similar to excel sheet
            for (int row = 0; row < ROWS; row++) {
                fileRow = input.nextLine();
                rowItems = fileRow.split(",");

                for (int col = 0; col < COLUMNS; col++) {
                    fileData[row][col] = rowItems[col];
                }
            }
        }
        catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null,"Error:" + e.getMessage() + "File not found. Please place file in Assignment 3 folder");
            System.exit(0);
        }
    }

    //embedded main method
    public static void main(String[] args) {
        new CarTracker();
    }
}
