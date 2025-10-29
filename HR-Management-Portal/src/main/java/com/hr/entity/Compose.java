package com.hr.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Table(name="COMPOSE")
@Entity
public class Compose {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String empName;
	
	private String subject;
	
	@Column(name="TEXT",length = 2000)
	private String text;
	
	private Integer parentUkid;
	
	private String status;

	private String addedDate;
	
	private Integer leaveDays = 1; // Number of days requested for leave
	
	// Additional fields for compatibility with ComposeDTO
	private Integer senderId;
	private Integer recipientId;
	
	@Transient
	private String position;

	@CreationTimestamp
	private LocalDateTime createdDate;
	
	@UpdateTimestamp
	private LocalDateTime updatedDate;

	public Compose() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	 

	public Integer getParentUkid() {
		return parentUkid;
	}

	public void setParentUkid(Integer parentUkid) {
		this.parentUkid = parentUkid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(String addedDate) {
		this.addedDate = addedDate;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Integer getLeaveDays() {
		return leaveDays;
	}

	public void setLeaveDays(Integer leaveDays) {
		this.leaveDays = leaveDays;
	}
	
	public Integer getSenderId() {
		return senderId;
	}
	
	public void setSenderId(Integer senderId) {
		this.senderId = senderId;
	}
	
	public Integer getRecipientId() {
		return recipientId;
	}
	
	public void setRecipientId(Integer recipientId) {
		this.recipientId = recipientId;
	}
	
	// Alias method for compatibility with ComposeDTO
	public String getMessage() {
		return this.text;
	}
	
	public void setMessage(String message) {
		this.text = message;
	}

	@Override
	public String toString() {
		return "Compose [id=" + id + ", empName=" + empName + ", subject=" + subject + ", text=" + text
				+ ", parentUkid=" + parentUkid + ", status=" + status + ", addedDate=" + addedDate + ", createdDate="
				+ createdDate + ", updatedDate=" + updatedDate + "]";
	}
	
	
	
}
