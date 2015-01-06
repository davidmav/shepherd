package org.shepherd.domain;

public final class DashboardNotification {

	private long id;
	private String content;
	private boolean read;
	private String firstName;
	private String lastName;
	private String prettyTime;
	private String action;

	public long getId() {
		return this.id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public boolean isRead() {
		return this.read;
	}

	public void setRead(final boolean read) {
		this.read = read;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getPrettyTime() {
		return this.prettyTime;
	}

	public void setPrettyTime(final String prettyTime) {
		this.prettyTime = prettyTime;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(final String action) {
		this.action = action;
	}

}
