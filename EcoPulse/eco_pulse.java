// Eco-Pulse

// Author:	Oliver Janner, 1020187

// Bluecove library used for bluetooth connection
// Compile:	javac -cp bluecove.jar eco_pulse.java
// Run:		java -cp .;bluecove.jar eco_pulse


import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.microedition.io.*;


public class eco_pulse {

	// ---------------------------------------------------------------------------------------------------------------------------------
	// The following values have to be adjusted to the car (see technical datasheet and online reports)
	// Currently set for a Toyota Yaris 1.3
	// ---------------------------------------------------------------------------------------------------------------------------------
	private double[] gear_trans = {3.545,1.913,1.310,1.027,0.850};
	private double roll_circ = 1.8943848;
	private double axis_ratio = 4.055;
	private double[] fuel_comsomption_gear1 = {30,12.3,12.6,14.3,16.9,20.1};
	private double[] fuel_comsomption_gear2 = {30,8.7,7.3,7.5,8,8.8,9.7,11,12};
	private double[] fuel_comsomption_gear3 = {30,6.8,6.1,5.7,5.9,6.1,6.6,7.3,8.1,8.8,9.7,10.7,11.8};
	private double[] fuel_comsomption_gear4 = {30,5.7,5.3,5,5,5.2,5.6,6,6.6,7.4,8,9,9.9,11,12,13.5,14.7};
	private double[] fuel_comsomption_gear5 = {30,5.2,4.8,4.5,4.5,4.7,4.9,5.3,6,6.5,7.3,8,8.8,9.9,11,12.2,13.7,15,16.4,17.8};
	// ---------------------------------------------------------------------------------------------------------------------------------
	// The following value sets the bluetooth address
	private String bt_addr = "112233DDEEFF";
	// ---------------------------------------------------------------------------------------------------------------------------------
	// The following value sets the current fuel price
	private double fuel = 1.60;
	// ---------------------------------------------------------------------------------------------------------------------------------
	// The following value sets the detailed protocol function to on/off (average values are always recorded)
	private boolean prot_on = true;
	// ---------------------------------------------------------------------------------------------------------------------------------
	private boolean simulate = true, connected = false, running = false;
	private int speed, rpm, gear, ep, brake, brake_score = 0, speed_score = 0, rpm_score = 0, ep_avg = 0, speed_avg = 0, rpm_avg = 0, time = 0, curr_brake, curr_rpm, curr_speed;
	private double consumption, consumption_avg = 0;
	private String consumption_str, file_str;
	private JLabel heart, pulse, kmh, after, out, cons, cons_outer, arr_kmh, arr_rpm, rpm_label, brake_label, circle;
	private JButton back;
	private Insets insets;
	private InputStream in_stream;
	private OutputStream out_stream;
	private StreamConnection client;
	private FileWriter prot_writer;

	public static void main(String[] args) {
		eco_pulse e1 = new eco_pulse();
		e1.createGUI();
	}
	
	private void createGUI() {
		JFrame window = new JFrame("EcoPulse");
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setUndecorated(true);
		window.setLayout(new BorderLayout());
		JPanel panelBottom = new JPanel(new GridLayout(1,3));
		JPanel panelMid = new JPanel();
		panelMid.setLayout(null);
		insets = panelMid.getInsets();
		JPanel panelTop = new JPanel(new BorderLayout());
		JButton connect = new JButton("Verbinden / Trennen");
		connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
					@Override
					protected Void doInBackground(){
						connect();
						return null;
					}
					@Override
					protected void done(){
					}
				};
				worker.execute();
			}
		});
		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
					@Override
					protected Void doInBackground(){
						start();
						return null;
					}
					@Override
					protected void done(){
					}
				};
				worker.execute();
			}
		});
		JButton stop = new JButton("Stop");
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
					@Override
					protected Void doInBackground(){
						stop();
						return null;
					}
					@Override
					protected void done(){
					}
				};
				worker.execute();
			}
		});
		JButton close = new JButton("x");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (connected) {
					try {
						client.close();
						in_stream.close();
						out_stream.close();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.exit(1);
			}
		});
		back = new JButton("Zurück");
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				out.setVisible(true);
				cons.setVisible(true);
				cons_outer.setVisible(true);
				heart.setVisible(true);
				pulse.setVisible(true);
				kmh.setVisible(true);
				rpm_label.setVisible(true);
				brake_label.setVisible(true);
				arr_kmh.setVisible(true);
				arr_rpm.setVisible(true);
				circle.setVisible(true);
				after.setVisible(false);
				back.setVisible(false);
			}
		});
		after = new JLabel("");
		after.setHorizontalAlignment(JLabel.CENTER);
		after.setFont(new Font("Toyota MR2", Font.PLAIN, 30));
		out = new JLabel("");
		out.setHorizontalAlignment(JLabel.CENTER);
		out.setFont(new Font("Toyota MR2", Font.PLAIN, 30));
		out.setForeground(new Color(0x5f5f5f));
		cons = new JLabel("<html><center>0,0</center></html>");
		cons.setHorizontalAlignment(JLabel.CENTER);
		cons.setFont(new Font("Toyota MR2", Font.PLAIN, 70));
		cons.setForeground(new Color(0x5f5f5f));
		cons_outer = new JLabel(new ImageIcon("img/consumption.png"));
		heart = new JLabel(new ImageIcon("img/heart_ok.png"));
		pulse = new JLabel(new ImageIcon("img/pulse_ok.png"));
		kmh = new JLabel(new ImageIcon("img/kmh.png"));
		rpm_label = new JLabel(new ImageIcon("img/rpm_1-4.png"));
		brake_label = new JLabel(new ImageIcon("img/brake_ok.png"));
		arr_kmh = new JLabel(new ImageIcon("img/arrow.png"));
		arr_rpm = new JLabel(new ImageIcon("img/arrow.png"));
		circle = new JLabel(new ImageIcon("img/circle.png"));
		panelBottom.add(connect);
		panelBottom.add(start);
		panelBottom.add(stop);
		panelMid.add(after);
		panelMid.add(out);
		panelMid.add(cons);
		panelMid.add(cons_outer);
		panelMid.add(heart);
		panelMid.add(pulse);
		panelMid.add(kmh);
		panelMid.add(rpm_label);
		panelMid.add(brake_label);
		panelMid.add(arr_kmh);
		panelMid.add(arr_rpm);
		panelMid.add(circle);
		after.setSize(1346,716);
		after.setLocation(10+insets.left, 0+insets.top);
		after.setVisible(false);
		out.setSize(1366,40);
		out.setLocation(0+insets.left, 675+insets.top);
		cons.setSize(211,211);
		cons.setLocation(60+insets.left, 208+insets.top);
		Dimension size = cons_outer.getPreferredSize();
		cons_outer.setSize(size.width, size.height);
		cons_outer.setLocation(60+insets.left, 208+insets.top);
		size = heart.getPreferredSize();
		heart.setSize(size.width, size.height);
		heart.setLocation(523+insets.left, 205+insets.top);
		size = pulse.getPreferredSize();
		pulse.setSize(size.width, size.height);
		pulse.setLocation(482+insets.left, 205+insets.top);
		size = kmh.getPreferredSize();
		kmh.setSize(size.width, size.height);
		kmh.setLocation(330+insets.left, 55+insets.top);
		size = rpm_label.getPreferredSize();
		rpm_label.setSize(size.width, size.height);
		rpm_label.setLocation(330+insets.left, 515+insets.top);
		size = brake_label.getPreferredSize();
		brake_label.setSize(size.width, size.height);
		brake_label.setLocation(1095+insets.left, 208+insets.top);
		size = arr_kmh.getPreferredSize();
		arr_kmh.setSize(size.width, size.height);
		arr_kmh.setLocation(324+insets.left, 20+insets.top);
		size = arr_rpm.getPreferredSize();
		arr_rpm.setSize(size.width, size.height);
		arr_rpm.setLocation(324+insets.left, 480+insets.top);
		size = circle.getPreferredSize();
		circle.setSize(size.width, size.height);
		circle.setLocation(0+insets.left, 0+insets.top);
		panelTop.add(BorderLayout.EAST, close);
		panelTop.add(BorderLayout.WEST, back);
		back.setVisible(false);
		window.add(BorderLayout.NORTH, panelTop);
		window.add(BorderLayout.CENTER, panelMid);
		window.add(BorderLayout.SOUTH, panelBottom);
		window.setVisible(true);
	}
	
	private void connect() {
		if (connected) {
			try {
				client.close();
				in_stream.close();
				out_stream.close();
				out.setText("Verbindung getrennt.");
				connected = false;
				simulate = true;
			}
			catch (Exception e) {
				out.setText("Trennen fehlgeschlagen.");
				connected = true;
				simulate = false;
			}
		}
		else {
			out.setText("Verbindung wird hergestellt...");
			bt_connection();
			if (connected) {
				simulate = false;
				out.setText("Verbindung hergestellt.");
			}
			else {
				simulate = true;
				out.setText("Verbindung fehlgeschlagen.");
			}
		}
	}
	
	private void reconnect() {
		try {
			out.setText("Versuche Verbindung wiederherzustellen...");
			client.close();
			in_stream.close();
			out_stream.close();
			bt_connection();
			int count = 1;
			while (!connected && count < 10) {
				bt_connection();
				count++;
			}
			if (connected)
				out.setText("Ausführung.");
			else
				stop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void start() {
		try {
			Calendar cal = Calendar.getInstance();
			String y = ""+cal.get(Calendar.YEAR);
			String m = ""+(cal.get(Calendar.MONTH)+1);
			if (m.length() != 2)
				m = "0"+m;
			String d = ""+cal.get(Calendar.DAY_OF_MONTH);
			if (d.length() != 2)
				d = "0"+d;
			String h = ""+cal.get(Calendar.HOUR_OF_DAY);
			if (h.length() != 2)
				h = "0"+h;
			String min = ""+cal.get(Calendar.MINUTE);
			if (min.length() != 2)
				min = "0"+min;
			String s = ""+cal.get(Calendar.SECOND);
			if (s.length() != 2)
				s = "0"+s;
			file_str = "protocols\\"+y+"-"+m+"-"+d+"_"+h+"-"+min+"-"+s+".txt";
			File protocol = new File(file_str);
			prot_writer = new FileWriter(protocol, true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		running = true;
		speed = 0;
		rpm = 0;
		ep = 0;
		brake = 0;
		gear = 1;
		ep_avg = 0;
		consumption_avg = 0;
		speed_avg = 0;
		rpm_avg = 0;
		time = 0;
		if (simulate == true) {
			out.setText("Ausführung mit simulierten Daten.");
			simulate_data();
		}
		else {
			out.setText("Ausführung.");
			get_data();
		}
	}
	
	private void stop() {
		running = false;
		speed = 0;
		rpm = 0;
		ep = 0;
		brake = 0;
		gear = 1;
		set_gfx();
		out.setText("Beendet.");
		cons.setText("<html><center>0,0</center></html>");
		if (prot_on) {
			try {
				prot_writer.write("\n\n");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		calc_after();
		try {
			prot_writer.flush();
			prot_writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		out.setVisible(false);
		cons.setVisible(false);
		cons_outer.setVisible(false);
		heart.setVisible(false);
		pulse.setVisible(false);
		kmh.setVisible(false);
		rpm_label.setVisible(false);
		brake_label.setVisible(false);
		arr_kmh.setVisible(false);
		arr_rpm.setVisible(false);
		circle.setVisible(false);
		after.setVisible(true);
		back.setVisible(true);
	}
	
	private void bt_connection() {
		connected = false;
		try {
        	client = (StreamConnection)Connector.open("btspp://"+bt_addr+":1;authenticate=false;encrypt=false;master=false");
        	in_stream = client.openInputStream();
        	out_stream = client.openOutputStream();
			connected = true;
			init_dongle();
		}
		catch (Exception e) {
			e.printStackTrace();
			connected = false;
		}
	}
	
	private void init_dongle() {
		out.setText("Dongle wird initialisiert...");
		byte[] ATZ = {'A', 'T', 'Z', '\r', '\n'};		// Reset
    	byte[] ATE0 = {'A', 'T', 'E', '0','\r', '\n'};	// Echo off
    	byte[] ATH0 = {'A', 'T', 'H', '0','\r', '\n'};	// Headers off
    	byte[] ECU = {'0', '1', '0', '0','\r', '\n'};	// Check ECU connection
    	try {
			out_stream.write(ATZ);
			out_stream.flush();
			Thread.sleep(200);
			read_data();
			out_stream.write(ATE0);
			out_stream.flush();
			read_data();
			out_stream.write(ATH0);
			out_stream.flush();
			read_data();
			out_stream.write(ECU);
			out_stream.flush();
			if (new String(read_data(), "us-ascii").contains("ERROR"))
				connected = false;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private byte[] read_data() {
		byte[] bytesRead = null;
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int b;
			while ((b = in_stream.read()) != -1) {
				if (b == '>')
					break;
				buf.write(b);
			}
			bytesRead = buf.toByteArray();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return bytesRead;
    }
	
	private int evaluate_data(byte[] b) {
		String b_str = "";
		int res = 0;
		try {
			b_str = new String(b, "us-ascii");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		b_str = b_str.replaceAll(" ", "");
		b_str = b_str.replaceAll("\r", "");
		b_str = b_str.replaceAll("\n", "");
		if (b_str.length() == 6) {
			b_str = b_str.substring(4,6);
			res = Integer.parseInt(b_str, 16);
		}
		else if (b_str.length() == 8) {
			b_str = b_str.substring(4,8);
			res = (Integer.parseInt(b_str, 16))/4;
		}
		else
			res = -1;
		return res;
	}
	
	private void simulate_data() {
		while (running) {
			int speed_diff = (int) (Math.random() * 10);
			int add_or_sub = (int) (Math.random() * 2);
			if ((add_or_sub == 0 && speed+speed_diff <= 190) || (add_or_sub > 0 && speed-speed_diff < 0)) {
				speed += speed_diff;
				brake = 0;
			}
			else if ((add_or_sub == 0 && speed+speed_diff > 190) || (add_or_sub > 0 && speed-speed_diff >= 0)) {
				speed -= speed_diff;
				brake = speed_diff;
			}
			int gear_diff = (int) (Math.random() * 2);
			add_or_sub = (int) (Math.random() * 2);
			if ((add_or_sub == 0 && gear+gear_diff <= 5) || (add_or_sub > 0 && gear-gear_diff < 1)) {
				gear += gear_diff;
			}
			else if ((add_or_sub == 0 && gear+gear_diff > 5) || (add_or_sub > 0 && gear-gear_diff >= 1)) {
				gear -= gear_diff;
				brake = 0;
			}
			rpm = (int) (speed * 1000 / 60 / roll_circ * axis_ratio * gear_trans[gear-1]);
			while (rpm < 1300 && gear > 1) {
				gear--;
				rpm = (int) (speed * 1000 / 60 / roll_circ * axis_ratio * gear_trans[gear-1]);
			}
			while (rpm > 6000 && gear < 5) {
				gear++;
				rpm = (int) (speed * 1000 / 60 / roll_circ * axis_ratio * gear_trans[gear-1]);
			}
			calc_ep();
			calc_consumption();
			speed_avg += speed;
			rpm_avg += rpm;
			ep_avg += ep;
			consumption_avg += consumption;
			time ++;
			if (prot_on) {
				DecimalFormat df = new DecimalFormat("0.0");
				try {
					prot_writer.write("Eco-Pulse: "+ep+",\t Verbrauch: "+df.format(consumption)+" l / 100km,\t Bremswert: "+curr_brake+",\t Geschwindigkeit: "+speed+" km / h ("+curr_speed+"),\t Umdrehungszahl: "+rpm+" U / min ("+curr_rpm+")\n");
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			set_gfx();
			try {
				Thread.sleep(1000);
			}
			catch (Exception e) {
			}
		}
	}
	
	private void get_data() {
		while (running) {
			int speed_diff = speed;
			int gear_diff = gear;
			byte[] RPM = {'0', '1', '0', 'C', '\r', '\n'};	// RPM (2 byte hex / 4)
			byte[] KMH = {'0', '1', '0', 'D', '\r', '\n'};	// Speed (1 byte hex)
			try {
				out_stream.write(RPM);
				out_stream.flush();
				int rpm_temp = evaluate_data(read_data());
				if (rpm_temp != -1)
					rpm = rpm_temp;
				out_stream.write(KMH);
				out_stream.flush();
				int speed_temp = evaluate_data(read_data());
				if (speed_temp != -1)
					speed = speed_temp;
			}
			catch (Exception e) {
				e.printStackTrace();
				reconnect();
			}
			calc_gear();
			speed_diff = speed_diff - speed;
			gear_diff = gear_diff - gear;
			if (speed_diff > 0 && gear_diff <=0) {
				brake = speed_diff;
				if (brake > 10)
					brake = 10;
			}
			calc_ep();
			calc_consumption();
			speed_avg += speed;
			rpm_avg += rpm;
			ep_avg += ep;
			consumption_avg += consumption;
			time ++;
			if (prot_on) {
				DecimalFormat df = new DecimalFormat("0.0");
				try {
					prot_writer.write("Eco-Pulse: "+ep+",\t Verbrauch: "+df.format(consumption)+" l / 100km,\t Bremswert: "+curr_brake+",\t Geschwindigkeit: "+speed+" km / h ("+curr_speed+"),\t Umdrehungungszahl: "+rpm+" U / min ("+curr_rpm+")\n");
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			set_gfx();
			try {
				Thread.sleep(1000);
			}
			catch (Exception e) {
			}
		}
	}
	
	private void calc_gear() {
		double trans = 0;
		if (speed != 0)
			trans = rpm / speed / axis_ratio * roll_circ * 60 / 1000;
		double[] gear_breakpoints = {(gear_trans[0]+gear_trans[1])/2.0,(gear_trans[1]+gear_trans[2])/2.0,(gear_trans[2]+gear_trans[3])/2.0,(gear_trans[3]+gear_trans[4])/2.0};
		if (trans > gear_breakpoints[0])
			gear = 1;
		else if (trans <= gear_breakpoints[0] && trans > gear_breakpoints[1])
			gear = 2;
		else if (trans <= gear_breakpoints[1] && trans > gear_breakpoints[2])
			gear = 3;
		else if (trans <= gear_breakpoints[2] && trans > gear_breakpoints[3])
			gear = 4;
		else
			gear = 5;
		if (trans == 0)
			gear = 1;
		while ((gear == 1 && speed > 48) || (gear == 2 && speed > 88) || (gear == 3 && speed > 129) || (gear == 4 && speed > 164))
			gear++;
		while ((gear == 2 && speed < 20) || (gear == 3 && speed < 28) || (gear == 4 && speed < 36) || (gear == 5 && speed < 43))
			gear--;
	}
	
	private void calc_ep() {
		int score = brake;
		brake_score += score;
		curr_brake = score;
		ep = 2*score;
		if (gear < 5 && rpm <= 2000)
			score = (int) (rpm/1000);
		else if (gear < 5 && rpm > 2000 && rpm <= 3000)
			score = (int) (rpm/333) - 3;
		else if (gear < 5 && rpm > 3000)
			score = (int) (rpm/1166) + 5;
		else if (gear == 5 && rpm <= 3200)
			score = (int) (rpm/1600);
		else if (gear == 5 && rpm > 3200 && rpm <= 4200)
			score = (int) (rpm/333) - 6;
		else
			score = (int) (rpm/766) + 2;
		rpm_score += score;
		curr_rpm = score;
		ep += (int)3*score;
		if (speed <= 30) {
			if (speed == 0)
				score = 10;
			else if (speed < 10)
				score = 9;
			else if (speed < 20)
				score = 8;
			else
				score = 7;
		}	
		else if (speed > 30 && speed <= 75) {
			if (speed < 41)
				score = 6;
			else if (speed < 52)
				score = 5;
			else if (speed < 63)
				score = 4;
			else
				score = 3;
		}
		else if (speed > 75 && speed <= 115) {
			if (speed < 83)
				score = 2;
			else if (speed < 91)
				score = 1;
			else if (speed < 99)
				score = 0;
			else if (speed < 107)
				score = 1;
			else
				score = 2;
		}
		else if (speed > 115 && speed <= 135) {
			if (speed < 120)
				score = 3;
			else if (speed < 125)
				score = 4;
			else if (speed < 130)
				score = 5;
			else
				score = 6;
		}
		else {
			if (speed < 140)
				score = 7;
			else if (speed < 145)
				score = 8;
			else if (speed < 150)
				score = 9;
			else
				score = 10;
		}
		speed_score += score;
		curr_speed = score;
		ep += (int)2*score;
	}
	
	private void calc_consumption() {
		if (simulate) {
			while ((gear == 1 && speed > 48) || (gear == 2 && speed > 88) || (gear == 3 && speed > 129) || (gear == 4 && speed > 164))
				gear++;
			while ((gear == 2 && speed < 20) || (gear == 3 && speed < 28) || (gear == 4 && speed < 36) || (gear == 5 && speed < 43))
				gear--;
		}
		if (gear == 5 && speed > 190)
			consumption = 30;
		else if (gear == 1) {
			if (speed%10 == 0) {
				consumption = fuel_comsomption_gear1[speed/10];
			}
			else {
				consumption = (fuel_comsomption_gear1[speed/10]*(speed%10) + fuel_comsomption_gear1[speed/10+1]*(10-speed%10)) / 10;
			}
		}
		else if (gear == 2) {
			if (speed%10 == 0) {
				consumption = fuel_comsomption_gear2[speed/10];
			}
			else {
				consumption = (fuel_comsomption_gear2[speed/10]*(speed%10) + fuel_comsomption_gear2[speed/10+1]*(10-speed%10)) / 10;
			}
		}
		else if (gear == 3) {
			if (speed%10 == 0) {
				consumption = fuel_comsomption_gear3[speed/10];
			}
			else {
				consumption = (fuel_comsomption_gear3[speed/10]*(speed%10) + fuel_comsomption_gear3[speed/10+1]*(10-speed%10)) / 10;
			}
		}
		else if (gear == 4) {
			if (speed%10 == 0) {
				consumption = fuel_comsomption_gear4[speed/10];
			}
			else {
				consumption = (fuel_comsomption_gear4[speed/10]*(speed%10) + fuel_comsomption_gear4[speed/10+1]*(10-speed%10)) / 10;
			}
		}
		else {
			if (speed%10 == 0) {
				consumption = fuel_comsomption_gear5[speed/10];
			}
			else {
				consumption = (fuel_comsomption_gear5[speed/10]*(speed%10) + fuel_comsomption_gear5[speed/10+1]*(10-speed%10)) / 10;
			}
		}
		DecimalFormat df = new DecimalFormat("0.0");
		consumption_str = df.format(consumption);
		cons.setText("<html><center>"+consumption_str+"</center></html>");
	}
	
	private void calc_after() {
		File[] protocols = new File("protocols").listFiles();
		String line;
		String line2;
		int curr_ep = -1;
		int min_ep = -1;
		double min_cons = -1;
		for (int i=0; i<protocols.length; i++) {
			if (!(("protocols\\"+protocols[i].getName()).equals(file_str))) {
				try {
					BufferedReader b_fr = new BufferedReader(new FileReader("protocols\\"+protocols[i].getName()));
					while ((line = b_fr.readLine()) != null) {
						if (line.equals("Durchschnittswerte:")) {
							while ((line2 = b_fr.readLine()) != null) {
								if (line2.contains("Eco-Pulse")) {
									line2 = line2.replace("Eco-Pulse: ", "");
									curr_ep = Integer.parseInt(line2);
								}
								else if (line2.contains("Verbrauch")) {
									line2 = line2.replace("Verbrauch: ", "");
									line2 = line2.replace(" l / 100km", "");
									line2 = line2.replace(",", ".");
									double curr_cons = Double.parseDouble(line2);
									if (curr_cons < min_cons || min_cons == -1) {
										min_cons = curr_cons;
										min_ep = curr_ep;
									}
								}
							}
						}
					}
					b_fr.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		String time_str = "Fahrdauer: ";
		String aggr_str = "EcoPulse-Wert: <span style='font-size:30px; font-weight:bold;'>";
		int time2;
		if (time > 3600) {
			time_str += (time/3600)+" Stunden, ";
			time2 = time%3600;
			time_str += (time2/60)+" Minuten, ";
			time2 = time2%60;
			time_str += time2+" Sekunden.";
		}
		else if (time > 60) {
			time_str += (time/60)+" Minuten, ";
			time2 = time%60;
			time_str += time2+" Sekunden.";
		}
		else {
			time_str += time+" Sekunden.";
		}
		ep_avg = ep_avg/time;
		aggr_str += ep_avg+"</span> <font color=";
		String red = "#ff0000";
		String orange = "#ff9c00";
		String yellow = "#e1e900";
		String yellowgr = "#96ff00";
		String green = "#12ff00";
		if (ep_avg > 34)
			aggr_str += red+"'>(sehr unökonomisch)";
		else if (ep_avg > 24)
			aggr_str += orange+"'>(recht unökonomisch)";
		else if (ep_avg > 18)
			aggr_str += yellow+"'>(mittelmäßig)";
		else if (ep_avg > 10)
			aggr_str += yellowgr+">(recht ökonomisch)";
		else
			aggr_str += green+"'>(sehr ökonomisch)";
		aggr_str += ".</font>";
		consumption_avg = consumption_avg/time;
		DecimalFormat df = new DecimalFormat("0.0");
		String cons_str = df.format(consumption_avg);
		cons_str = "Verbrauch: <span style='font-size:30px; font-weight:bold;'>"+cons_str+"</span> l / 100km.";
		String comp2best;
		String comp2datasheet;
		DecimalFormat eur = new DecimalFormat("0.00");
		if (min_cons > consumption_avg || min_cons == -1) {
			comp2best = "<span style='font-size:30px; font-weight:bold;'>Neues Bestergebnis.</span>";
		}
		else {
			comp2best = "<span style='font-size:30px; font-weight:bold;'>"+eur.format((consumption_avg-min_cons)*fuel)+" Euro</span>";
		}
		if (consumption_avg > 6.0) {
			comp2datasheet = "<span style='font-size:30px; font-weight:bold;'>"+eur.format((consumption_avg-6.0)*fuel)+" Euro</span>";
		}
		else {
			comp2datasheet = "<span style='font-size:30px; font-weight:bold;'>Unterhalb durchschnittlichem Verbrauch lt. Hersteller.</span>";
		}
		String comp;
		if (min_cons > consumption_avg || min_cons == -1) {
			comp = "<u>Mögliche Ersparnis (pro 100km bei "+eur.format(fuel)+" Euro / l):</u><br>Verglichen mit früheren Fahrten: "+comp2best+"<br>Verglichen mit Verbrauch lt. Hersteller (6,0 lt): "+comp2datasheet;
		}
		else {
			comp = "<u>Mögliche Ersparnis (pro 100km bei "+eur.format(fuel)+" Euro / l):</u><br>Verglichen mit früheren Fahrten ("+df.format(min_cons)+" lt / EP "+min_ep+"): "+comp2best+"<br>Verglichen mit Verbrauch lt. Hersteller (6,0 lt): "+comp2datasheet;
		}
		speed_avg = speed_avg/time;
		String speed_str = "Geschwindigkeit: "+speed_avg+" km / h";
		rpm_avg = rpm_avg/time;
		String rpm_str = "Umdrehungszahl: "+rpm_avg+" U / min";
		String improve_str = "<u>Verbesserungsvorschläge:</u><br><span style='font-size:20px'>";
		brake_score = brake_score/time;
		if (brake_score > 6)
			improve_str += "Ihre Bremswerte liegen in einem kritischen Bereich - versuchen Sie durch vorausschauendes Fahren und Nutzung der Motorbremse entgegenzuwirken.<br>";
		else if (brake_score > 2)
			improve_str += "Ihre Bremswerte sind leicht erhöht - versuchen Sie durch vorausschauendes Fahren und Nutzung der Motorbremse entgegenzuwirken.<br>";
		speed_score = speed_score/time;
		if (speed_score > 6)
			improve_str += "Ihre Geschwindigkeitswerte liegen in einem kritischen Bereich - versuchen Sie Stadtverkehr zu meiden und auch auf der Autobahn Geschwindigkeiten von 120 km / h nicht zu überschreiten.<br>";
		else if (speed_score > 2)
			improve_str += "Ihre Geschwindigkeitswerte sind leicht erhöht - versuchen Sie Stadtverkehr zu meiden und auch auf der Autobahn Geschwindigkeiten von 120 km / h nicht zu überschreiten.<br>";
		rpm_score = rpm_score/time;
		if (rpm_score > 6)
			improve_str += "Ihre Umdrehungswerte liegen in einem kritischen Bereich - versuchen Sie früher zu schalten. Ein moderner Benzinmotor läuft selbst unter 2000 U / min noch flüssig.<br>";
		else if (rpm_score > 2)
			improve_str += "Ihre Umdrehungswerte sind leicht erhöht - versuchen Sie früher zu schalten. Ein moderner Benzinmotor läuft selbst unter 2000 U / min noch flüssig.<br>";
		if (brake_score <= 2 && speed_score <= 2 && rpm_score <= 2)
			improve_str += "All Ihre Werte liegen im grünen Bereich - weiter so!<br>";
		improve_str += "</span>";
		after.setText("<html><center>"+time_str+"<br>"+aggr_str+"<br>"+cons_str+"<br>"+speed_str+"<br>"+rpm_str+"<br><br>"+comp+"<br><br>"+improve_str+"</center></html>");
		try {
			prot_writer.write("Durchschnittswerte:\n\n"+time_str+"\nEco-Pulse: "+ep_avg+"\nVerbrauch: "+df.format(consumption_avg)+" l / 100km\nBremswert: "+brake_score+"\n"+speed_str+" ("+speed_score+")\n"+rpm_str+" ("+rpm_score+")");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void set_gfx() {
		int left = (int) (324+insets.left+(3.5*speed));
		arr_kmh.setLocation(left, 20+insets.top);
		left = (int) (324+insets.left+(0.1*rpm));
		arr_rpm.setLocation(left, 480+insets.top);
		if (gear == 5)
			rpm_label.setIcon(new ImageIcon("img/rpm_5.png"));
		else
			rpm_label.setIcon(new ImageIcon("img/rpm_1-4.png"));
		if (brake < 3)
			brake_label.setIcon(new ImageIcon("img/brake_ok.png"));
		else if (brake < 7)
			brake_label.setIcon(new ImageIcon("img/brake_med.png"));
		else
			brake_label.setIcon(new ImageIcon("img/brake_bad.png"));
		if (ep == 0) {
			heart.setIcon(new ImageIcon("img/heart_ok.png"));
			pulse.setIcon(new ImageIcon("img/pulse_ok.png"));
		}
		else if (ep < 18) {
			heart.setIcon(new ImageIcon("img/heart_ok.gif"));
			pulse.setIcon(new ImageIcon("img/pulse_ok.png"));
		}
		else if (ep < 29) {
			heart.setIcon(new ImageIcon("img/heart_med.gif"));
			pulse.setIcon(new ImageIcon("img/pulse_med.png"));
		}
		else {
			heart.setIcon(new ImageIcon("img/heart_bad.gif"));
			pulse.setIcon(new ImageIcon("img/pulse_bad.png"));
		}
	}
	
}