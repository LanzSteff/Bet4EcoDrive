package com.example.obd2.dataHandling;

public abstract class Entity {
	public final int ID;
	public final String NAME;
	public final ParamDescription[] PARAM_DESCRIPTION;
	private boolean enabled;

	protected Entity(int id, String name, String unit) {
		this(id, name, new ParamDescription(name, unit));
	}

	protected Entity(int id, String name, ParamDescription... pd) {
		this.ID = id;
		this.NAME = name;
		this.PARAM_DESCRIPTION = new ParamDescription[pd.length];
		this.enabled = false;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public static class ParamDescription {
		public final String name;
		public final String unit;

		public ParamDescription(String name, String unit) {
			this.name = name;
			this.unit = unit;
		}
	}
}
