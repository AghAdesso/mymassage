package model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "registration")
@NamedQueries({ @NamedQuery(name = "Registration.findById", query = "select o from Registration o where o.id = :id"),
	@NamedQuery(name = "Registration.findByMassage", query = "select o from Registration o where o.massage.id = :id and o.isActive = true order by o.id desc"),
	@NamedQuery(name = "Registration.findByReminder", query = "select o from Registration o where o.isActive = true and o.reminder = true and o.massage.date between :date1 and :date2 order by o.id desc"),
	@NamedQuery(name = "Registration.findByUser", query = "select o from Registration o where o.isCanceled = false and o.isActive = true and o.firstName = :firstName and o.lastName = :lastName and o.massage.date >= current_date"),
	@NamedQuery(name = "Registration.findAll", query = "select o from Registration o order by o.id")})
public class Registration implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;
	
	@Column(name = "firstName", nullable = false)
	private String firstName;
	
	@Column(name = "lastName", nullable = false)
	private String lastName;
	
	@Column(name = "comment")
	private String comment;
	
	@Column(name = "phoneNumber", nullable = false)
	private int phoneNumber;
	
	@Column(name = "isActive", nullable = false)
	private Boolean	isActive;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "massageId")
	private Massage massage;
	
	@Column(name = "isCanceled", nullable = false)
	private Boolean	isCanceled;
	
	@Column(name = "appointmentId", nullable = false)
	private String appointmentId; 
	
	@Column(name = "reminder", nullable = false)
	private Boolean	reminder;
	
	public Registration() {
		this.id = null;
		this.firstName = "";
		this.lastName = "";
		this.comment = "";
		this.isActive = true;
		this.phoneNumber = 0;
		this.massage = null;
		this.isCanceled = false;
		this.appointmentId = "";
		this.reminder = false;
	}
	
	public Registration(Massage massage) {
		this.id = null;
		this.firstName = "";
		this.lastName = "";
		this.comment = "";
		this.isActive = true;
		this.phoneNumber = 0;
		this.massage = massage;
		this.isCanceled = false;
		this.appointmentId = "";
		this.reminder = false;
	}

}
