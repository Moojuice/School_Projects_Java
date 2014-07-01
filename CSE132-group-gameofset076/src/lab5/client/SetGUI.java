package lab5.client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.Font;
import java.io.IOException;
import java.net.SocketException;


/**Set GUI
 * Visual interface for the game. Has an update function called by the client, which refreshes all the necessary
 * components. 
 * 
 * ~~~~~BASIC INSTRUCTIONS FOR GAME~~~~~
 * You need to press Call Set before clicking on your set. Take too long, or make an invalid set and you are penalized. Other people
 * must wait while you try to call a set.
 * 
 * At the end of the game, if none of the players cannot find any more sets, a majority needs to agree to press Add Cards
 * in order to end the game.  
 * 
 *  Author: Jeremy "Linsanity" Tang, Edward "Team Edward" Xie, Benjamin "Button" Epstein, Kyle "#swag_yolo4lyfe_i<3jesus" Liu
 *	Course: 132
 *	Lab: 5
 */

public class SetGUI extends JFrame {
	private  JLayeredPane contentPane; 
	/*
	 * Instance Variables, there's a lot of them so there are no "magic numbers"
	 */
	private int addCardsButtonX = 724;
	private int addCardsButtonY = 214;
	private int addCardsButtonWidth = 178;
	private int addCardsButtonHeight = 37;
	private int cardX = 0;
	private int cardY = 0;
	private int cardWidth = 82; 
	private int cardHeight = 112; 
	private int cardsNumAreaX = 813;
	private int cardsNumAreaY = 159;
	private int cardsNumAreaWidth = 89;
	private int cardsNumAreaHeight = 37;
	private int contentPaneX = 0;
	private int contentPaneY = 0;
	private int contentPaneWidth = 960; //10 * cardWidth
	private int contentPaneHeight = 548; //4 * cardHeight
	private int nameFieldX = 380;
	private int nameFieldY = 131;
	private int nameFieldWidth = 205;
	private int nameFieldHeight = 20;
	private int numCardsLblX = 752;
	private int numCardsLblY = 134;
	private int numCardsLblWidth = 150;
	private int numCardsLblHeight = 14;
	private int playerPaneX = 726;
	private int playerPaneY =266;
	private int playerPaneWidth =108;
	private int playerPaneHeight =123;
	private int pointPaneX = 833;
	private int pointPaneY = 266;
	private int pointPaneWidth = 68;
	private int pointPaneHeight = 123;
	private int popUpX = 0;
	private int popUpY = 0;
	private int popUpWidth = 300;
	private int popUpHeight = 100;
	private int rescaledWidth = 72;
	private int rescaledHeight = 102;
	private int rescaleHint = 4;
	private int setButtonX = 813;
	private int setButtonY = 86;
	private int setButtonWidth = 89;
	private int setButtonHeight = 37;
	private int startButtonX = 380;
	private int startButtonY = 322;
	private int startButtonWidth = 205;
	private int startButtonHeight = 89;
	private int startNumPlayerX = 380;
	private int startNumPlayerY = 176;
	private int startNumPlayerWidth = 205;
	private int startNumPlayerHeight = 135;
	private int titleX = 380;
	private int titleY = 41;
	private int titleWidth = 205;
	private int titleHeight = 70;
	private int border = 5;

	private Client client;
	private boolean setPressed;
	private JButton addCardsButton;
	private JButton[] cardDisplay = new JButton[21];
	private JButton start;
	private byte[] selectedArray = new byte[3];
	private JButton setButton;
	private JList listPlayers;
	private JList listPlayersPoints;
	private JList listStartPlayers;
	private DefaultListModel listModelPlayers;
	private DefaultListModel listModelScores;
	private DefaultListModel listModelStartPlayers;
	private JScrollPane playersPane;
	private JScrollPane playersPointsPane;
	private JScrollPane startGamePane;
	private JTextArea numberOfCardsInDeck;
	private JLabel lblBackground;
	private JLabel lblTitle;
	private JLabel lblNumberOfCards;
	private JTextField nameField;

	/**
	 * Launch the application.
	 */

	/*
	 * Run method connects to server & refreshes the visual 
	 */
	public void run() {
		while(true){
			if(client.isConnected()){
				while(!client.isGameStarted()) { //listens to server for the go signal
					client.listenToServer();
				}
				//
				// game components revealed
				//
				setButton.setVisible(true);
				lblNumberOfCards.setVisible(true);
				numberOfCardsInDeck.setVisible(true);
				playersPointsPane.setVisible(true);
				playersPane.setVisible(true);
				addCardsButton.setVisible(true);
				//
				// title components hidden
				//
				start.setVisible(false);
				startGamePane.setVisible(false);
				lblTitle.setVisible(false);
				nameField.setVisible(false);
				validate();
			
				updateGUI(); //visual update
				while(client.isGameStarted() && client.isConnected()){ //constantly listens to server and refreshes until game ends
					if (client.isCanCallSet()) {
						updateGUI();
						client.listenToServer();
						updateGUI();
					}
				}
				reset("" + client.getWinner()); //resets game when finished
			}else{
				try { //if not connected, will wait until checking again
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace(); //should never happen
				}
			}
		}
	}

	/*
	 *  Updates the game version of the GUI, including scores, players and cards
	 */
	public void updateGUI(){ 
		if (client.isGameStarted()) {
			if (client.isCanCallSet()) {
				if(!client.isCanSelectCards()) { 
					//resets selected cards if you can't select cards
					selectedArray[0] = 34;
					selectedArray[1] = 34;
					selectedArray[2] = 34;
					setPressed = false; //set button reset
					setButton.setEnabled(true);
					setButton.setText("Call Set");
				}
				else { //if you can select cards, not allowed to press set button again, button lets you know to pick cards
					setButton.setEnabled(false);
					setButton.setText("Pick"); 
				}
			}else{	
				if (setPressed) { //if you cant call set, but the set button is pressed, resets selected cards and the set button
					setPressed = false;
					for (int i=0; i<3; i++){
						if (selectedArray[i] != 34) {
							cardDisplay[selectedArray[i]].setBackground(Color.DARK_GRAY);
							selectedArray[i] = 34;
						}
					}
				}
				setButton.setText("Wait"); //timeout until you can select cards again
				setButton.setEnabled(false);
			}
			/*
			 * Iterates through players and scores, setting names and numbers
			 */
			for (int i = 0; i<client.getPlayers().length; i ++) {
				if(client.getPlayers()[i]!= null){
					if (listModelPlayers.size() == 0) {
						listModelPlayers.setSize(client.getPlayers().length);
						listModelScores.setSize(client.getPlayers().length);
					}
					listModelPlayers.set(i,client.getPlayers()[i].getName());
					listModelScores.set(i,client.getPlayers()[i].getScore());
				}
			}
			/*
			 * Refreshes cards in the field to display the most updated view 
			 */
			if(client.getCardsInField()!= 0){
				String[] currentCards = client.getCardArray();
				for (int i = 0; i < client.getCardsInField(); i++) {
					if(currentCards != null) {
						//This image icon is how we get a rescaled image of the cards
						//Image has a rescale method, but cannot be instantiated because it is an abstract class
						//ImageIcon does not inherit the rescale method, therefore two ImageIcons need to be called
						//What is actually created is an ImageIcon(Image(URL))
						ImageIcon cardImage = new ImageIcon(new ImageIcon(SetGUI.class.getResource("/lab5/client/mediumcards/"+currentCards[i]+".jpg")).getImage().getScaledInstance(rescaledWidth, rescaledHeight, rescaleHint));
						if (!cardDisplay[i].getIcon().equals(cardImage)) { //if the image is different than the current image (aka when a set is called or when a row is added)
							addCardsButton.setEnabled(true); //reset ability to call for adding a column
							cardDisplay[i].setIcon(cardImage); //turns to new image
							numberOfCardsInDeck.setText("" + client.getCardsLeft()); //refresh # of cards in deck
						}			
						cardDisplay[i].setVisible(true); //set visible for the beginning of the game, and for new columns
					}
				} for(int i = client.getCardsInField(); i <21 ; i++){ //visually handles buttons that are in excess
					cardDisplay[i].setVisible(false);
				}
			}
		} else {
			validate();
		}
	}


	/**
	 * Create the frame, adding title screen and ingame components, but changing visibility at
	 * end
	 */
	public SetGUI(Client c) {
		client = c;
		setResizable(false); //cannot expand game to keep background image looking spiffy 
		
		/*
		 * Standard setup for contentPane, notice that it is a layered pane
		 * so we are able to have a good-looking background
		 */
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(contentPaneX, contentPaneY, contentPaneWidth, contentPaneHeight);		
		contentPane = new JLayeredPane();
		contentPane.setBorder(new EmptyBorder(border, border, border, border));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		/*
		 * Background inspired from LoZ Majora's Mask, covers entire content pane, but displayed on a layer
		 * underneath everything else
		 */
		lblBackground = new JLabel();
		lblBackground.setBounds(contentPaneX, contentPaneY, contentPaneWidth, contentPaneHeight);
		contentPane.add(lblBackground);
		contentPane.setLayer(lblBackground, -1); //-1 means it is the most "background" layer
		lblBackground.setIcon(new ImageIcon(SetGUI.class.getResource("/lab5/client/mediumcards/backgroundrescaled.png")));

		/*
		 * Fancy title text
		 */
		lblTitle = new JLabel("Set");
		lblTitle.setForeground(Color.LIGHT_GRAY);
		lblTitle.setFont(new Font("Mistral", Font.PLAIN, 60));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(titleX, titleY, titleWidth, titleHeight);
		contentPane.add(lblTitle);
		contentPane.setLayer(lblTitle, 0); //For all future cases, and including this one,
		//layer 0 is the standard above the background image

		/*
		 * Pane which allows selection of number of players
		 */
		startGamePane = new JScrollPane();
		startGamePane.setBounds(startNumPlayerX, startNumPlayerY, startNumPlayerWidth,
				startNumPlayerHeight);
		contentPane.add(startGamePane);
		contentPane.setLayer(startGamePane, 0);
		//model allows for modification of list information
		listModelStartPlayers = new DefaultListModel();
		listStartPlayers = new JList(listModelStartPlayers);
		listStartPlayers.setBorder(new TitledBorder(null, "Number of Players",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		startGamePane.setViewportView(listStartPlayers);
		//Fills list with 1-4 players
		for (byte i = 1; i <= 4; i++) {
			listModelStartPlayers.addElement(i);
		}

		/*
		 * Input text field for choosing player's name
		 */
		nameField = new JTextField();
		nameField.setToolTipText("Type your name here");
		nameField.setText("Type your name in this box");
		nameField.setBounds(nameFieldX, nameFieldY, nameFieldWidth, nameFieldHeight);
		contentPane.add(nameField);
		contentPane.setLayer(nameField, 0);
		nameField.setColumns(border*2);

		/*
		 * Standard button, lets player start after number of players
		 * is chosen
		 */
		start = new JButton();
		start.setToolTipText("Select number of players and enter your name and press to start.");
		start.setText("Start Game");
		start.setBounds(startButtonX, startButtonY, startButtonWidth,
				startButtonHeight);
		contentPane.add(start);
		contentPane.setLayer(start, 0);

		/*
		 * Upon press, checks to make sure a number of players are chosen, and that
		 * the user has typed in a valid name. Will attempt to connect to server, using client's hello method 
		 */
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (listStartPlayers.getSelectedValue() != null) {
					if(nameField.getText()!= "" && !nameField.getText().equals("Type your name in this box") && !nameField.getText().equals("Need to type your name first")){
						if(client.connect()){
							start.setText("Waiting For Players");
							start.setEnabled(false);
							String nameToSend = nameField.getText().replaceAll("%", "");//makes sure the name doesn't have a % in it
							if(nameToSend.length()>36)nameToSend=nameToSend.substring(0, 36);//makes sure the name isn't too long
							client.sayHello(nameToSend, ((Byte) (listStartPlayers.getSelectedValue())).byteValue());
						}
					}else{
						nameField.setText("Need to type your name first");
					}
				} else {
					nameField.setText("Need to select the number of players");
				}

			}

		});

		/*
		 * Set Button that allows player to pick cards 
		 * after pressing in order to attempt a set
		 */
		setButton = new JButton("Call Set");
		setButton.setToolTipText("Press this button before selecting cards to send them as a set. If another player is selecting cards, you have to wait until they finish");
		setButton.setBounds(setButtonX, setButtonY, setButtonWidth, setButtonHeight);
		contentPane.add(setButton);
		contentPane.setLayer(setButton, 0);
		
		//Button changes state of setPressed to true, allowing players to attempt
		//a set
		setButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!setPressed) {
					client.playerCallSet(); //sends message to attempt to call set
					setPressed = true;
				}
			}
		});

		/*
		 * Counter section for number of cards left in deck.
		 */
		numberOfCardsInDeck = new JTextArea();
		numberOfCardsInDeck.setToolTipText("Number of cards left in deck.");
		numberOfCardsInDeck.setEditable(false);
		numberOfCardsInDeck.setBounds(cardsNumAreaX, cardsNumAreaY, cardsNumAreaWidth, cardsNumAreaHeight);
		contentPane.add(numberOfCardsInDeck);
		contentPane.setLayer(numberOfCardsInDeck, 0);

		/*
		 * Label to indicate what the counter is keeping track of
		 */
		lblNumberOfCards = new JLabel("Cards left in deck");
		lblNumberOfCards.setForeground(Color.LIGHT_GRAY);
		lblNumberOfCards.setBounds(numCardsLblX, numCardsLblY, numCardsLblWidth, numCardsLblHeight);
		lblNumberOfCards.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPane.add(lblNumberOfCards);
		contentPane.setLayer(numberOfCardsInDeck, 0);

		/*
		 * Pane which keeps track of players in the game
		 */
		playersPane = new JScrollPane();
		playersPane.setBounds(playerPaneX, playerPaneY, playerPaneWidth, playerPaneHeight);
		contentPane.add(playersPane);
		contentPane.setLayer(playersPane, 0);
		//list model allows for easy modification
		listModelPlayers = new DefaultListModel();
		listPlayers = new JList(listModelPlayers);
		listPlayers.setBorder(new TitledBorder(null, "Players", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		playersPane.setViewportView(listPlayers);
		/*
		 * Pane which keeps track of score
		 */
		playersPointsPane = new JScrollPane();
		playersPointsPane.setBounds(pointPaneX, pointPaneY, pointPaneWidth, pointPaneHeight);
		contentPane.add(playersPointsPane);
		contentPane.setLayer(playersPointsPane, 0);
		//list model allows for easy change
		listModelScores = new DefaultListModel();
		listPlayersPoints = new JList(listModelScores);
		playersPointsPane.setViewportView(listPlayersPoints);
		listPlayersPoints.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Scores", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		/*
		 * Button which can be pressed to ask for a column addition, or call to end game
		 */
		addCardsButton = new JButton("Add Cards");
		addCardsButton.setToolTipText("Ask for a new column of cards(Needs majority vote). Call if you can't find any sets");
		addCardsButton.setBounds(addCardsButtonX, addCardsButtonY, addCardsButtonWidth, addCardsButtonHeight);
		contentPane.add(addCardsButton);
		contentPane.setLayer(addCardsButton, 0);	
		//Upon press, sends message through client to request for a row addition, or if not enough cards, end the game
		addCardsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.askForCards();
				addCardsButton.setEnabled(false);
			}
		});

		//
		//All of the game components are set invisible until the set button is pressed
		//
		setButton.setVisible(false);
		lblNumberOfCards.setVisible(false);
		numberOfCardsInDeck.setVisible(false);
		playersPointsPane.setVisible(false);
		playersPane.setVisible(false);
		addCardsButton.setVisible(false);
		//cards are generated (invisible as well)
		generateCards();
	}
	/*
	 * Generates cards and their button functionality
	 */
	public void generateCards(){
		try {	
			int index = 0;
			for (int j = 0; j < 7; j++) { // iterates through 3 rows and 7 columns, creating 21 buttons
				for (int i = 0; i < 3; i++) {
					final JButton card = new JButton();
					card.setBounds(cardX, cardY, cardWidth, cardHeight);
					cardY += cardHeight + 5*border; //shift down 1 row
					card.setBackground(Color.DARK_GRAY);
					//
					// Gives each card at the beginning a blank icon for replacement in the update 
					// If you are curious about the implementation of this icon, check the update method
					//
					card.setIcon(new ImageIcon(new ImageIcon(SetGUI.class.getResource("/lab5/client/mediumcards/1BlankCard.jpg")).getImage().getScaledInstance(rescaledWidth, rescaledHeight,  rescaleHint)));
					contentPane.add(card);
					contentPane.setLayer(card, 0); 
					cardDisplay[index] = card; //adds button to the array of buttons, for easy access
					final byte temp = (byte) index; //temp is the "index" of the card (from 0-20, up-down left-right priority) 
					/*
					 * ActionListener takes into account if the card is already part of the "selectedset"
					 * ands deals with it through booleans. Requires set button to be pressed
					 * and player needs ability to select cards. Once player completes 3 cards, the set
					 * is immediately sent and everything is reset. Uses the index of the card to identify which card is which,
					 * which is added to an array of selected cards.
					 */
					card.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							boolean deleting = false; 
							if (client.isCanSelectCards() && setPressed) {
								for(byte i = 0; i < 3; i++){
									if(selectedArray[i] == temp){ //if card has been selected before
										selectedArray[i] = 34; //theres no index in the selectedArray
										deleting = true; //card skips next step
										card.setBackground(Color.DARK_GRAY);
									}
								}
								if(!deleting){ //if card has not been selected yet
									boolean added = false;
									for(byte i = 0; i < 3; i++){ //iterates through selected cards,
										//if space is empty (set to 34), then
										//cards' index is added
										if(!added){
											if(selectedArray[i] == 34){ //if index is empty, card's index is added to selected
																		//set and is highlighted 
												selectedArray[i] = temp;
												added = true;
												card.setBackground(Color.YELLOW);
											}
										}
									}
								}
							}					
							/*
							 * Submits full set by index
							 */
							if (selectedArray[0]!= 34 &&selectedArray[1]!= 34 &&selectedArray[2]!= 34) { 
								//resets callSetButton
								setPressed = false;
								setButton.setText("Call Set");
								//resets highlighting of selected cards
								cardDisplay[selectedArray[0]].setBackground(Color.DARK_GRAY);
								cardDisplay[selectedArray[1]].setBackground(Color.DARK_GRAY);
								cardDisplay[selectedArray[2]].setBackground(Color.DARK_GRAY);
								try {
									//sends set under the player name 
									client.getSmos().setCall(client.getPlayerNumber(), selectedArray);
								}catch (SocketException e) {
									//should never happen 
									System.exit(-1);
									System.out.println("socket exception");
								} catch (IOException e) {
									//should never happen
									e.printStackTrace();
								}
								//Now we reset the selected array
								selectedArray[0] = 34;
								selectedArray[1] = 34;
								selectedArray[2] = 34;
							}
						}

					});
					index++; //advances the index
					card.setVisible(false); //card is set invisible
				}
				cardX += cardWidth + 2*border; //iterates through columns  
				cardY = 0; //resets Y value
			}

		} catch (Exception e) {
			//general exception which should never happen 
			e.printStackTrace();
		}
	}

	/*
	 * Method which is called at the end of the game or when player disconnected, visually resets the GUI and makes a gameover popup
	 */
	public void reset(String name) {
		//
		// game components hidden
		//
		setButton.setVisible(false);
		lblNumberOfCards.setVisible(false);
		numberOfCardsInDeck.setVisible(false);
		playersPointsPane.setVisible(false);
		playersPane.setVisible(false);
		addCardsButton.setVisible(false);
		for (int i = 0; i < 21; i ++) {
			cardDisplay[i].setVisible(false);
		}

		//
		// title components revealed
		//
		start.setText("Start");
		start.setVisible(true);
		startGamePane.setVisible(true);
		lblTitle.setVisible(true);
		nameField.setVisible(true);
		nameField.setText("Type your name in this box");
		start.setEnabled(true);
		validate();
		//
		// winning popup
		//
		JFrame winner = new JFrame();
		winner.setVisible(true);
		winner.setBounds(popUpX, popUpY, popUpWidth, popUpHeight);
		JTextField winnerText = new JTextField();
		if (name.equals("0")) { //if player is disconnected, gui will still reset
			winnerText.setText("Disconnected from server");
		}
		else {
			winnerText.setText("Player " + name + " has won!");
		}
		winnerText.setHorizontalAlignment(SwingConstants.CENTER);
		winnerText.setBounds(popUpX,popUpY, popUpWidth, popUpHeight);
		winner.getContentPane().add(winnerText);
	}
}
