import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.Timer;

import javax.swing.*;


public class Alarm extends JFrame implements ActionListener {
	private JMenuBar menuBar = new JMenuBar();
	private ArrayList<JMenu> menuList = new ArrayList<JMenu>();
	private ArrayList<JButton> buttonList = new ArrayList<JButton>();
	private boolean suspendFlag = false; 
	private boolean beginFlag = false;
	private static boolean soundOn = true;
	private static boolean webOn = true;
	private int width = 500;
	private int height = 400;
	private JPanel headPanel = new JPanel(new FlowLayout());
	private JPanel contentPanel = new JPanel(new GridBagLayout());
	private countDown count;
	private int leftSeconds = 10;
	private File f = new File("src/init.txt");
	private static String alarmSound = "src/Ship_Bell.wav";
	
	private static URI targetURL = URI.create("https://uisapp2.iu.edu/tk-prd/TimesheetDocument.do?method=open&liteMode=1&casticket=ST-1951767-1BcNbsNSxvLrD8c3t1SE-casprd04.uits.iu.edu");
	public static void main(String[] args){
		// TODO Auto-generated method stub
		Alarm alarm = new Alarm();
	}
	static public boolean getSoundFlag() {
		return soundOn;
	}
	static public boolean getWebFlag() {
		return webOn;
	}
	static public String getSoundPath() {
		return alarmSound;
	}
	static public URI getTargetWeb() {
		return targetURL;
	}
	public Alarm() {
		
		makeMenu();
		readConf();
		makeContent();
		for(JMenu menu : menuList) {
			menuBar.add(menu);
			menu.addActionListener(this);
		}
		panelInit();
		add(headPanel, "North");
		//add(buttonPanel,BorderLayout.SOUTH);
		add(contentPanel, "Center");
	}
	private void readConf() {
		boolean flag = true;
		String raw_sound = readFromFile(f,0);
		if(!raw_sound.equals("")) {
			alarmSound = raw_sound;
			//System.out.println("Read sound file: "+alarmSound);
		} else {
			flag = false;
		}
		String raw_url = readFromFile(f,1);
		if(!raw_url.equals("")) {
			targetURL = URI.create(raw_url);
			//System.out.println("Read url: "+targetURL);
		} else {
			flag = false;
		}
		String raw_soundFlag = readFromFile(f,2);
		if(!raw_soundFlag.equals("")) {
			soundOn = Boolean.valueOf(raw_soundFlag);
		} else {
			flag = false;
		}
		String raw_web = readFromFile(f,3);
		if(!raw_web.equals("")) {
			webOn = Boolean.valueOf(raw_web);
		} else {
			flag = false;
		}
		//if read file fail, create a new one
		if (flag==false) {
			//System.out.println("Read setting fail, use dafault");
			writeToFile(f,0,alarmSound);
			writeToFile(f,1,targetURL.toString());
			writeToFile(f,2,Boolean.toString(soundOn));
			writeToFile(f,3,Boolean.toString(webOn));
		}
	}
	
	private String readFromFile(File f, int index) {
		BufferedReader reader = null;
		String line = "";
		try {
			reader = new BufferedReader(new FileReader(f));
			int i = 0;
			String inputLine = "";
			while ((inputLine = reader.readLine()) != null) {
				if(i==index) {
					line = inputLine;
					break;
				}
				i++;
			}
		} catch (Exception e) {
			//System.out.println("Cannot open conf file");
		} finally {
			if(reader !=null) {
				try { reader.close(); }
				catch (IOException e) {e.printStackTrace();}
			}
		}
		return line;
	}
	
	
	private void writeToFile(File f, int index, String line) {
		BufferedReader br = null;
		BufferedWriter bw = null;
		FileReader fr = null;
		try {
			if(!f.exists()) {
				f.createNewFile();
				//System.out.println("Create a file");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			String infoMessage = "Cannot create a file src/init.txt";
    		String title = "Cannot create a file";
    		JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
		}

		try {
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			ArrayList<String> inputLines = new ArrayList<String>();
			String inputLine = "";
			while ((inputLine = br.readLine()) != null) {
				inputLines.add(inputLine);
			}
			bw = new BufferedWriter(new FileWriter(f));
			int length = Math.max(inputLines.size(), index+1);
			for(int i=0;i<length;i++) {				
				if(i==index) {
					bw.write(line);
					bw.newLine();
					//System.out.println("Write setting into file "+line);
				} else {
					String tmpLine = inputLines.get(i);
					bw.write(tmpLine);
					bw.newLine();
				}
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			String infoMessage = "Cannot write into the file src/init.txt";
    		String title = "Cannot write conf file";
    		JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
		} finally {
			if(fr !=null) {
				try { fr.close(); }
				catch (IOException e) {e.printStackTrace();}
			}
			if(br !=null) {
				try { br.close(); }
				catch (IOException e) {e.printStackTrace();}
			}
			if(bw !=null) {
				try { bw.close(); }
				catch (IOException e) {e.printStackTrace();}
			}
		}
	}
	
	private void panelInit() {
		setTitle("Clockin assistant");
		setSize(width, height);
		setJMenuBar(menuBar);
		setLayout(new BorderLayout());
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void makeContent() {
		//contentPanel = content;
		JLabel leftTime = new JLabel("Please set up the time");
		leftTime.setFont(new Font("Calibri", Font.PLAIN, 20));
		leftTime.setHorizontalAlignment(JLabel.CENTER);
		leftTime.setVerticalAlignment(JLabel.CENTER);
		

		contentPanel.setPreferredSize(new Dimension(400,300));
		count = new countDown(leftTime, leftSeconds);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		//c.ipady = 40;      //make this component tall
	//	c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		contentPanel.add(leftTime, c);
		//For buttons
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.insets = new Insets(10,5,10,5);	
		c.gridx = 0;
		c.gridy = 1;
		String buttonString = "";
		if(webOn) buttonString = "Pop page: on";
		else buttonString = "Pop page: off";
		JButton buttonA = new JButton(buttonString);	
		contentPanel.add(buttonA,c);
		c.gridx = 1;
		c.gridy = 1;
		JButton buttonB = new JButton("Set up");
		contentPanel.add(buttonB,c);
		c.gridx = 2;
		c.gridy = 1;
		if(soundOn) buttonString = "Audio: on";
		else buttonString = "Audio: off";
		JButton buttonC = new JButton(buttonString);	
		contentPanel.add(buttonC,c);
		
		JButton button1 = new JButton("Start");	
		c.gridx = 0;
		c.gridy = 2;
		contentPanel.add(button1, c);
		JButton button2 = new JButton("Pause");
		c.gridx = 1;
		c.gridy = 2;
		contentPanel.add(button2, c);
		JButton button3 = new JButton("Stop");
		c.gridx = 2;
		c.gridy = 2;
		contentPanel.add(button3, c);
		buttonList.clear();
		buttonList.add(button1);
		buttonList.add(button2);
		buttonList.add(button3);
		buttonList.add(buttonA);
		buttonList.add(buttonB);
		buttonList.add(buttonC);
		for(JButton button : buttonList) {
			button.addActionListener(this);
		}
	}
	
	private void makeMenu() {
		JMenu setting = new JMenu("Setting");
		setting.setMnemonic('S');
		JMenuItem item1 = new JMenuItem("Set up a time");
		JMenuItem item2 = new JMenuItem("Reset");
		JMenuItem item3 = new JMenuItem("Pop page on/off");
		JMenuItem item4 = new JMenuItem("Turn audio on/off");
		JMenuItem item5 = new JMenuItem("Select audio");
		JMenuItem item6 = new JMenuItem("Timesheet URL");
		item1.addActionListener(this);
		item2.addActionListener(this);
		item3.addActionListener(this);
		item4.addActionListener(this);
		item5.addActionListener(this);
		item6.addActionListener(this);
		setting.add(item1);
		setting.add(item2);
		setting.add(item3);
		setting.add(item4);
		setting.add(item5);
		setting.add(item6);
		menuList.add(setting);
		JMenu about = new JMenu("About");
		setting.setMnemonic('A');
		JMenuItem item7 = new JMenuItem("Demo");
		JMenuItem item8 = new JMenuItem("About");
		item7.addActionListener(this);
		item8.addActionListener(this);
		about.add(item7);
		about.add(item8);
		menuList.add(about);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource()==buttonList.get(0)) {	
			if(beginFlag==false) {		
				//System.out.println("Start");
				beginFlag = true;
				suspendFlag = false;
				
				count.start();
			}
		} else if (arg0.getSource()==buttonList.get(1)) {
			if(beginFlag && !suspendFlag) {
				//is going to pause
				suspendFlag = true;
				buttonList.get(1).setText("Resume");
				count.pause();
				
			} else if(beginFlag && suspendFlag) {
				//has suspended, going resume
				suspendFlag = false;
				buttonList.get(1).setText("Pause");
				count.start();
			}

		} else if (beginFlag && (arg0.getSource()==buttonList.get(2) || arg0.getSource()==menuList.get(0).getMenuComponent(1))) {
			beginFlag = false;
			suspendFlag = false;
			count.pause();
			contentPanel.removeAll();
			contentPanel.revalidate();
			makeContent();		
		} else if(arg0.getSource()==menuList.get(0).getMenuComponent(0) || arg0.getSource()==buttonList.get(4)) {
			//set up a time
			JTextField timeField = new JTextField(5);
			JTextField exceedField = new JTextField(2);
			JPanel myPanel = new JPanel(new GridLayout(0, 1));
			myPanel.add(new JLabel("How many hours you need: like 4.2"));
			myPanel.add(timeField);
			myPanel.add(new JLabel("How many mins to exceed: "));
			myPanel.add(exceedField);
			myPanel.add(Box.createHorizontalStrut(15)); // a spacer
			float result = JOptionPane.showConfirmDialog(null, myPanel,
					"Time for hours", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				if(beginFlag) {
					count.pause();				
				}
				beginFlag = false;
				suspendFlag = false;
				// hours

					try {
						String raw_hours = timeField.getText();
						int seconds = raw_hours.equals("") ? 0 : (int) Double.parseDouble(raw_hours) * 3600;
						String raw_mins = exceedField.getText();
						int mins = raw_mins.equals("") ? 0 : Integer.parseInt(raw_mins)*60;
						leftSeconds = seconds + mins;
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						String infoMessage = "Please enter valid number";
		        		String title = "Wrong input";
		        		JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
	
					}
				//System.out.println(leftSeconds);
				contentPanel.removeAll();
				contentPanel.revalidate();
				makeContent();		
			}
		} else if(arg0.getSource()==menuList.get(0).getMenuComponent(2)) {
			Object[] options = {"Turn on","Turn off"};
			int result = JOptionPane.showOptionDialog(null, "Do you want to open the webpage or not?",
					"Pop-up: on/off", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options, options[1]);
			if(result==0) {
				buttonList.get(3).setText("Pop page: on");
				webOn = true;
			} else {
				buttonList.get(3).setText("Pop page: off");
				webOn = false;
			}
			writeToFile(f,3,Boolean.toString(webOn));
		} else if(arg0.getSource()==menuList.get(0).getMenuComponent(3)) {
			Object[] options = {"Turn on","Turn off"};
			int result = JOptionPane.showOptionDialog(null, "Do you want to turn the audio on/off?",
					"Turn audio on/off", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options, options[1]);
			if(result==0) {
				buttonList.get(5).setText("Audio: on");
				soundOn = true;
			} else {
				buttonList.get(5).setText("Audio: off");
				soundOn = false;
			}
			writeToFile(f,2,Boolean.toString(soundOn));
		} else if(arg0.getSource()==buttonList.get(3)) {
			if(webOn) {
				buttonList.get(3).setText("Pop page: off");
				webOn = false;
			} else {
				buttonList.get(3).setText("Pop page: on");
				webOn = true;
			}
			writeToFile(f,3,Boolean.toString(webOn));
		} else if(arg0.getSource()==buttonList.get(5)) {
			if(soundOn) {
				buttonList.get(5).setText("Audio: off");
				soundOn = false;
			} else {
				buttonList.get(5).setText("Audio: on");
				soundOn = true;
			}
			writeToFile(f,2,Boolean.toString(soundOn));
		} else if (arg0.getSource()==menuList.get(0).getMenuComponent(4)) {
			//select sound file
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(this);
			 if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            Path path = file.toPath();
		            alarmSound = path.toString();
					writeToFile(f,0,alarmSound);
		     } 
		} else if (arg0.getSource()==menuList.get(0).getMenuComponent(5)) {
			// select URL
			JTextField urlField = new JTextField(20);
			JPanel myPanel = new JPanel(new GridLayout(0, 1));
			myPanel.add(new JLabel("Enter timesheet URL:"));
			myPanel.add(urlField);	
			int result = JOptionPane.showConfirmDialog(null, myPanel,
					"Target URL", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				String raw_url = urlField.getText();
				HttpURLConnection connection = null;
				try {
				    URL myurl = new URL(raw_url);        
				    connection = (HttpURLConnection) myurl.openConnection(); 
				    //Set request to header to reduce load as Subirkumarsao said.       
				    connection.setRequestMethod("HEAD");         
				    int code = connection.getResponseCode(); 
				    //System.out.println((""+code).equals("200"));
				    if(!(""+code).equals("200")) throw new IOException();
				    else {
				    	targetURL = URI.create(raw_url);
				    	writeToFile(f,1,targetURL.toString());
				    }
					/*java.awt.Desktop.getDesktop().browse(tmpURL);
					targetURL = tmpURL;*/
				} catch (IOException e) {
					// TODO Auto-generated catch block
					String infoMessage = "Please input a validate URL";
	        		String title = "Validate URL";
	        		JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
				}	
			}
		} else if(arg0.getSource()==menuList.get(1).getMenuComponent(0)) {
			//Demo
			URI demo = URI.create("http://www.cs.indiana.edu/cgi-pub/jiguan/alarm/");
			try {
				java.awt.Desktop.getDesktop().browse(demo);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				String infoMessage = "Cannot open Demo website, please connect with the author.";
        		String title = "Wrong URL";
        		JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
			}
		} else if(arg0.getSource()==menuList.get(1).getMenuComponent(1)) {
			//about
			String infoMessage = "Version: 1.01\nAuthor: Jianqing\nEmail: jiguan@indiana.edu\nAny suggestion is welcome. ";
    		String title = "Checkin Assistant";
    		JOptionPane.showMessageDialog(null, infoMessage, title, JOptionPane.INFORMATION_MESSAGE);
		}
	}
}


class countDown {
	private Timer timer;
	int seconds;
	private JLabel label;
	public countDown( JLabel label, int leftSeconds) {
		seconds = leftSeconds;	
		this.label = label;
		label.setText(getTime(seconds));
		label.setFont(new Font("Calibri", Font.PLAIN, 100));
	}
	public void start() {
	    this.timer = new Timer();
	    this.timer.schedule(new TimerTask() {   
            public void run() {
            	String time = getTime(seconds--);
            	//System.out.println(seconds);
				label.setText(time);
                if (seconds< 0) {
                	timer.cancel();
                	try {
                		if(Alarm.getWebFlag()) {
                			URI targetURL = Alarm.getTargetWeb();
                			java.awt.Desktop.getDesktop().browse(targetURL);
                		}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						String infoMessage = "Cannot open web";
		        		String title = "Wrong URL";
		        		JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + title, JOptionPane.INFORMATION_MESSAGE);
					}
                	if(Alarm.getSoundFlag()) {
                		String alarmSound = Alarm.getSoundPath();
	            		SoundPlay sound_play = new SoundPlay(alarmSound);
	            		Thread alarm = new Thread(sound_play);
	            		alarm.start();
                	}
                }
            }
	    }, 0, 1000 );
	}
	public void pause() {
		timer.cancel();
	}
	private String getTime(int seconds) {
    	int hour = seconds / 3600;
		int min = (seconds % 3600 ) / 60;
		int second = seconds % 60;
		String time = String.format("%02d", hour)+":"+String.format("%02d", min)+":"+String.format("%02d", second);
		return time;
	}
}

