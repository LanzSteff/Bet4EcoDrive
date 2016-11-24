/**
 * @author DOETTLINGER
 */

package com.example.obd2.dataHandling;

import java.util.Formatter;

public abstract class OBD2Entity extends Entity {

	public final String QUERY_STRING;

	public OBD2Entity(int id, String name, String unit) {
		super(id, name, unit);
		this.QUERY_STRING = new Formatter().format("01%02X\r\n", id).toString();
	}

	public abstract Object convert(int[] params);

}
