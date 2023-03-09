package model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "role")
@NamedQueries({ @NamedQuery(name = "Role.findById", query = "select o from Role o where o.id = :id"),
	@NamedQuery(name = "Role.findByCode", query = "select o from Role o where o.code = :code"),
	@NamedQuery(name = "Role.findByDescription", query = "select o from Role o where o.description = :description"),
	@NamedQuery(name = "Role.findAll", query = "select o from Role o") })
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;
	
	@Column(name = "code", nullable = false)
	private String code;
	
	@Column(name = "description", nullable = false)
	private String description;
	
	@Column(name = "isActive", nullable = false)
	private Boolean isActive;
	
	public Role() {
		this.id = null;
		this.code = "";
		this.description = "";
		this.isActive = true;
	}
}
