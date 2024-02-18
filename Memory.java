import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Memory extends JFrame {
    private int[][] matrix;
    private List<Point> selected_points;
    private int founded_pairs;
    private int wrong_moves;
    private static final int max_wrong_moves = 3;

    public Memory() {
        //This is a constructor for the Memory Game class.
        //This part determines the title, size of the JFrame window and handles the close operation.
        setTitle("Memory Game");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameStarter();
        createGameBoard();

    }
    private JButton getButtonAtPosition(Point point) {
        //This method is used for retrieve a button to a specific position.
        // 'java.awt' has the Point class in its package. Basically it takes coordinates(x,y).
        //Here, this method takes the point object. Then calculates the linear index for 4x4 matrix.
        //the point.x*4+point.y formula converts the x and y coordinates to single linear index.
        //Component[] array contains buttons, labels etc. That line lets us accessing the components.
        int linearIndex = point.x * 4 + point.y;
        Component[] components = getContentPane().getComponents();
        //The if statement checks if the index is valid and a JButton. If true it returns components of that linearIndex.
        if (linearIndex >= 0 && linearIndex < components.length && components[linearIndex] instanceof JButton) {
            return (JButton) components[linearIndex];
        } else {
            return null;
        }
    }
    private void buttonClick(int linearIndex) {
        //calculates the row and column position according to linear index.
        Point point = new Point(linearIndex / 4, linearIndex % 4);
        JButton button = getButtonAtPosition(point);
        //getButtonAtPosition method provides the position and JButton component.
        if (!isPointSelected(point)) {
            //This if statement checks for clicked button's position has been already selected.
            addPointToSelectedList(point);
            int value = matrix[point.x][point.y];
            setButtonIcon(button, paintTheCircles(value));

            if (isTwoPointsSelected()) {
                //If two points have been selected it checks for a match.
                colorMatch();
            }
        }
    }
    private boolean isPointSelected(Point point) {
        return selected_points.contains(point);
    }
    //This method checks if selected_points set contains the provided point object. If there is an existence it returns true.
    private void addPointToSelectedList(Point point) {
        selected_points.add(point);
        //adds the point object to the selected_points set.
    }
    private void setButtonIcon(JButton button, Icon icon) {
        button.setIcon(icon);
    }
    //this sets the button's icon using JButton and Icon parameters.
    private boolean isTwoPointsSelected() {
        return selected_points.size() == 2;
    }
    //this one checks if two points are selected.

    private int[][] generateRandomValues() {
        //This method was used to generate random values for the game board.
        // Each(4) circle will take a different number.
        // Here we used array list to hold the values of circles.
        int[][] matrix= new int[4][4];
        List<Integer> pairs = new ArrayList<>();
        for (int i=0;i<8; i++){
            pairs.add(i);// for the pairing, second one adds a pair.
            pairs.add(i);
        }
        Collections.shuffle(pairs);//this is for shuffling the pairs each time(when you open the game), randomly.
        int count=0;
        for(int i=0;i<matrix.length;i++){//For assigning the shuffled pairs to each cell.
            for (int j=0;j<matrix[i].length;j++){
                matrix[i][j] =pairs.get(count)%8;
                count++;
            }
        }
        return matrix;
    }
    private void createGameBoard() {
        setLayout(new GridLayout(4, 4));
        //the buttons arranged in a 4x4 grid.
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                JButton button = createMemoryGameButton(matrix[i][j]);
                add(button);//adds the button to grid layout
            }
        }//The outer loop iterates through the rows and the inner one iterates the columns.
        //It creates a JButton for each element in the matrix using createMemoryGameButton method.

        timerButtonLabelUpdate();
    }
    private JButton createMemoryGameButton(int value) {
        //This method is for creating a button that represents an element in memory game grid.
        //This method sets the button's font, focus paint(GUI)
        JButton button = new JButton();
        button.setFont(new Font("Arial", Font.PLAIN, 20));
        button.setFocusPainted(false);
        button.setIcon(paintTheCircles(value));
        //action listener that triggers the buttonClick() method.
        button.addActionListener(e -> buttonClick(getLinearIndexForButton((JButton) e.getSource())));
        return button;
    }
    private int getLinearIndexForButton(JButton button) {
        //it gets the linear index of a given button. For loop iterates through the components for finding the given button
        Component[] components = getContentPane().getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == button) {
                return i;
            }
        }
        return -1;
    }
    private void timerButtonLabelUpdate() {
        /* It sets up a timer to execute the updateButtonLabel method. It will only happen once because 'setRepeats(false)'. */
        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateButtonLabels();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private Icon paintTheCircles(int value){
        Color color =colorEquals(value);
        int diameter=50;
        ImageIcon icon= new ImageIcon(new BufferedImage(diameter,diameter, BufferedImage.TYPE_INT_ARGB));
        // for the creation of the circle we gave the diameters.
        // BufferedImage."TYPE_INT_ARGB represents an image with 8-bit RGBA color components packed into integer pixels."
        Graphics g = icon.getImage().getGraphics();
        //we used graphics object for drawing shapes.
        g.setColor(color);
        g.fillOval(0, 0, diameter, diameter);
        g.setColor(Color.BLACK);
        g.drawOval(0, 0, diameter, diameter);
        return icon;// this returns the ImageIcon object that contains the circle with specific color.


    }
    public Color colorEquals(int value){
        //desired colors for 8 different circles.
        switch(value%8){
            case 0 :
                return Color.RED;
            case 1:
                return Color.GREEN;
            case 2:
                return Color.blue;
            case 3:
                return Color.YELLOW;
            case 4:
                return Color.ORANGE;
            case 5:
                return Color.CYAN;
            case 6:
                return Color.MAGENTA;
            case 7:
                return  Color.PINK;
            default:
                return Color.gray;
        }
    }
    private void colorMatch(){
        //colorMatch method was the most functional one.
        //First part recalls the selected points from their list.
        Point point1=selected_points.get(0);
        Point point2=selected_points.get(1);
        JButton button1= getButtonAtPosition(point1);//retrieves the JButton objects at positions.
        JButton button2= getButtonAtPosition(point2);
        //Checks if if the values associated with the selected positions match. If so it calls the checkMatchingCards method.
        if (equalValues(point1,point2)){
            checkMatchingCards();
        }
        else {//if equality does not exist it calls the unmatchedCards method.
            unmatchedCards(button1,button2);
        }
        clearSelectedPoints();//clears the list of selected points.
    }

    private boolean equalValues(Point point1, Point point2){
        //compares point1 and point2 values.
        return matrix[point1.x][point1.y] == matrix[point2.x][point2.y];
    }
    private void checkMatchingCards() {
        //this increments the founded_pairs counter. When the total of matched pairs reached to 8 it calls the displayGameCompletion method.
        founded_pairs++;
        if (founded_pairs == 8) {
            displayGameCompletion();
        }
    }
    private void unmatchedCards(JButton button1, JButton button2){
        //When unequal values are selected this method is used. It tracks the number of unsuccessful moves with wrong_moves counter.
        //If the wrong_moves reached to 3 then it calls displayGameOver method. If not it calls hideCards.
        wrong_moves++;
        if (wrong_moves==max_wrong_moves){
            displayGameOver();
        }else{
            hideCards(button1, button2);
        }
    }
    private void displayGameCompletion(){
        JOptionPane.showMessageDialog(this, "Congratulations! You found all pairs.");
        // Introduce a delay before starting a new game
        Timer delayTimer = new Timer(6000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // After the delay, start a new game and update button labels
                gameStarter();
                updateButtonLabels();
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();

    }
    private void displayGameOver(){
        JOptionPane.showMessageDialog(this, "Game Over! You've reached the maximum incorrect attempts.");
        System.exit(0);
    }
    private void hideCards(JButton button1, JButton button2){
        //This flips back the two wrong cards.
        Timer timer =new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button1.setIcon(ClosedCardIcon());
                button2.setIcon(ClosedCardIcon());

            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    private void clearSelectedPoints(){
        selected_points.clear();
    }

    public Icon ClosedCardIcon() {
        // Here the method creates an Icon for the closed cards, sets its color and returns the created icon.
        int diameter=50;
        ImageIcon icon= new ImageIcon(new BufferedImage(diameter,diameter, BufferedImage.TYPE_INT_ARGB));
        Graphics g= icon.getImage().getGraphics();
        g.setColor(Color.getHSBColor(43 ,64 ,35));
        g.fillRect(0, 0, diameter, diameter);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, diameter, diameter);
        return icon;
    }
    public void updateButtonLabels(){
        //This method iterates through the matrix, retrieves the current button and replaces the buttons with closed card icons.
        Component[] buttons = getContentPane().getComponents();
        int button_index=0;

        for (int row=0; row< matrix.length; row++){
            for (int column=0; column<matrix[row].length; column++) {
                JButton currentButton = (JButton) buttons[button_index];
                currentButton.setText("");
                currentButton.setIcon(ClosedCardIcon());
                button_index++;
            }
        }

    }

    public void gameStarter() {
        //This method is for game starting. It resets counters and arraylists.
        founded_pairs = 0;
        selected_points = new ArrayList<>();
        matrix = generateRandomValues();
        wrong_moves= 0;

    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //creating an instance
                Memory memoryGUI = new Memory();
                memoryGUI.setVisible(true);
            }
        });
    }}