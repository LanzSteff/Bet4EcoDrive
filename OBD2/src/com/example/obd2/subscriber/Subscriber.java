/**
 * @author DOETTLINGER
 */

package com.example.obd2.subscriber;

import com.example.obd2.reply.Reply;

public interface Subscriber {

	public abstract void onReceivedReply(Reply r);

	public abstract void onShutDown();
}