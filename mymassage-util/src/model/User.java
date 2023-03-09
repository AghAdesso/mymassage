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

/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@Getter
@Setter
@Table(name = "user")
@NamedQueries({ @NamedQuery(name = "User.findById", query = "select o from User o where o.id = :id"),
	@NamedQuery(name = "User.findByLogin", query = "select o from User o where o.login = :login"),
	@NamedQuery(name = "User.findAll", query = "select o from User o order by o.login")})
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;
	
	@Column(name = "login", unique = true, nullable = false)
	private String login;
	
	@Column(name = "isActive", nullable = false)
	private Boolean isActive;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "roleId")
	private Role role;
	
	public User() {
		this.id = 0;
		this.login = "";
		this.isActive = true;
		this.role = null;
	}
	
	public User(Role role) {
		this.id = 0;
		this.login = "";
		this.isActive = true;
		this.role = role;
	}
}
