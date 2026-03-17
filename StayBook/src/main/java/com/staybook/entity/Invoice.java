package com.staybook.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String invoiceNumber;

    private Double amount;

    private String status; // GENERATED / PAID

    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "booking_id", unique = true)
    private Booking booking;

	public Invoice() {
		super();
	}

	public Invoice(Long id, String invoiceNumber, Double amount, String status, LocalDateTime createdAt,
			Booking booking) {
		super();
		this.id = id;
		this.invoiceNumber = invoiceNumber;
		this.amount = amount;
		this.status = status;
		this.createdAt = createdAt;
		this.booking = booking;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

  
}