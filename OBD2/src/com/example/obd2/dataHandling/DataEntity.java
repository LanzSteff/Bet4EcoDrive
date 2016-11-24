package com.example.obd2.dataHandling;

public abstract class DataEntity extends Entity {

	protected DataEntity(int id, String name, String unit) {
		super(id, name, unit);
	}

	protected DataEntity(int id, String name, ParamDescription... pd) {
		super(id, name, pd);
	}

	abstract Object getValue();
}
