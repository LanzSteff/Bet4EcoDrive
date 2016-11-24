package com.example.obd2.subscriber;

/**
 * @author DOETTLINGER
 */

import com.example.obd2.Gui;
import com.example.obd2.R;
import com.example.obd2.reply.Reply;
import com.example.obd2.reply.Reply_DataSet;
import com.example.obd2.reply.Reply_DataSetComplete;
import com.example.obd2.utils.Utilities;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class GuiSubscriber implements Subscriber {

	// private final String TAG = "GUI_SUBSCRIBER";
	private TextView textField;

	private String msg = "";
	private long t = System.currentTimeMillis();

	public GuiSubscriber(Gui gui) {
		this.textField = (TextView) gui.findViewById(R.id.logView);
	}

	@Override
	public void onReceivedReply(Reply r) {
		if (r instanceof Reply_DataSet) {
			Reply_DataSet elem = (Reply_DataSet) r;

			if (msg.length() == 0) {
				long t_act = System.currentTimeMillis();
				msg += Utilities.timestampToDate(elem.TIMESTAMP) + " ("
						+ (t_act - t) + ")" + "\n";
				t = t_act;
			}

			msg += "> " + elem.ENTITY.NAME + ": " + "  " + elem.VAL + " "
					+ /* elem.ENTITY.unit + */"\n";

		} else if (r instanceof Reply_DataSetComplete) {
			textFieldHandler.sendEmptyMessage(0);
			Utilities.sleep(20);
			msg = "";
		} else {
			msg += "ERROR: Invalid comReply.\n";
		}
	}

	@Override
	public void onShutDown() {
		// TODO Auto-generated method stub
	}

	private Handler textFieldHandler = new Handler() {
		@SuppressWarnings("synthetic-access")
		@Override
		public void handleMessage(Message msg_) {
			textField.setText(msg + "\n");
		}
	};

}
