package com.staybook.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "hotel")   // ✅ ADD THIS

public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String city;
    private String address;
    private Long ratings;
    private String imageUrl;

    private double pricePerNight;
    private String description;
    private String location;

    
    @Column(nullable = false)
    private boolean approved = false;


    @ManyToOne
    private User vendor;


	public Hotel() {
		super();
	}


	public Hotel(Long id, String name, String city, String address, Long ratings, String imageUrl, double pricePerNight,
			String description, String location, boolean approved, User vendor) {
		super();
		this.id = id;
		this.name = name;
		this.city = city;
		this.address = address;
		this.ratings = ratings;
		this.imageUrl = imageUrl;
		this.pricePerNight = pricePerNight;
		this.description = description;
		this.location = location;
		this.approved = approved;
		this.vendor = vendor;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public Long getRatings() {
		return ratings;
	}


	public void setRatings(Long ratings) {
		this.ratings = ratings;
	}


	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	public double getPricePerNight() {
		return pricePerNight;
	}


	public void setPricePerNight(double pricePerNight) {
		this.pricePerNight = pricePerNight;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public boolean isApproved() {
		return approved;
	}


	public void setApproved(boolean approved) {
		this.approved = approved;
	}


	public User getVendor() {
		return vendor;
	}


	public void setVendor(User vendor) {
		this.vendor = vendor;
	}


	
}