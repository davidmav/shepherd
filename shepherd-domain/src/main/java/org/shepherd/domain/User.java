package org.shepherd.domain;

public final class User {

	private String role;
	private String firstName;
	private String lastName;
	private String title;
	private boolean male;
	private String email;
	private String location;
	private String phone;
	private Integer newsletterSubscription;
	private String website;
	private String bio;

	public String getEmail() {
		return this.email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}

	public Integer getNewsletterSubscription() {
		return this.newsletterSubscription;
	}

	public void setNewsletterSubscription(final Integer newsletterSubscription) {
		this.newsletterSubscription = newsletterSubscription;
	}

	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(final String website) {
		this.website = website;
	}

	public String getBio() {
		return this.bio;
	}

	public void setBio(final String bio) {
		this.bio = bio;
	}

	public boolean isMale() {
		return this.male;
	}

	public void setMale(final boolean male) {
		this.male = male;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(final String role) {
		this.role = role;
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

}
