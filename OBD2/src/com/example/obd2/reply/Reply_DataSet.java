/**
 * @author DOETTLINGER
 */

package com.example.obd2.reply;

import com.example.obd2.dataHandling.Entity;

public class Reply_DataSet implements Reply {

	public final Entity ENTITY;
	public final Object VAL;
	public final long TIMESTAMP;
	public final int ERR_CODE;

	public Reply_DataSet(Entity entity, long timestamp, Object val, int errCode) {
		this.ENTITY = entity;
		this.VAL = val;
		this.TIMESTAMP = timestamp;
		this.ERR_CODE = errCode;
	}

	public String toString() {
		return ENTITY.NAME;
	}
}
