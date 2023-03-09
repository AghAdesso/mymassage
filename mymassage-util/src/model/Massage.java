package model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "massage")
@NamedQueries({ @NamedQuery(name = "Massage.findById", query = "select o from Massage o where o.id = :id"),
	@NamedQuery(name = "Massage.findByDate", query = "select o from Massage o where o.date = :date"),
	@NamedQuery(name = "Massage.findActiveByDate", query = "select o from Massage o where o.date = :date and o.isActive = true"),
	@NamedQuery(name = "Massage.findByHighlighted", query = "select o from Massage o where o.isHighlighted = true and o.isVacant = true and o.date >= current_date order by o.date"),
	@NamedQuery(name = "Massage.findActiveBetweenDate", query = "select o from Massage o where o.date between :date1 and :date2 and o.isActive = true order by o.date"),
	@NamedQuery(name = "Massage.findAvailableBetweenDate", query = "select o from Massage o where o.date between :date1 and :date2 and o.isActive = true and o.isVacant = true order by o.date"),
	@NamedQuery(name = "Massage.findAll", query = "select o from Massage o order by o.date")})
public class Massage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;
	
	@Column(name = "date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	@Column(name = "isVacant", nullable = false)
	private Boolean isVacant;
	
	@Column(name = "isActive", nullable = false)
	private Boolean isActive;
	
	@Column(name = "isHighlighted", nullable = false)
	private Boolean isHighlighted;
	
	@javax.persistence.Transient
	private String userRegistred;
	
	public Massage() {
		this.id = null;
		this.date = new Date();
		this.isVacant = true;
		this.isActive = true;
		this.isHighlighted = false;
	}

	public Massage(Date date) {
		this.id = null;
		this.date = date;
		this.isVacant = true;
		this.isActive = true;
		this.isHighlighted = false;
	}
	
}
