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
@Table(name = "parameter")
@NamedQueries({ @NamedQuery(name = "Parameter.findById", query = "select o from Parameter o where o.id = :id"),
	@NamedQuery(name = "Parameter.findByType", query = "select o from Parameter o where o.parameterType = :type"),
	@NamedQuery(name = "Parameter.findByKey", query = "select o from Parameter o where o.parameterKey = :key"),
	@NamedQuery(name = "Parameter.findAll", query = "select o from Parameter o order by o.parameterKey")})
public class Parameter implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String DAYS_TO_CANCEL = "daysToCancel";
	public static final String DAYS_TO_HIGHLIGHT = "daysToHighlight";
	public static final String MASSEUR_EMAIL = "masseurEmail" ;
	public static final String MASSAGE_LENGTH = "massageLength";
	public static final String MASSAGE_ROOM = "massageRoom";
	public static final String BODY_EMAIL = "bodyEmail";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;
	
	@Column(name = "parameterKey", nullable = false)
	private String parameterKey;
	
	@Column(name = "parameterValue", nullable = false)
	private String parameterValue;
	
	@Column(name = "parameterType", nullable = false)
	private String parameterType;
	
	@Column(name = "description", nullable = false)
	private String description;
	 
	public Parameter() {
		this.id = null;
		this.parameterKey = "";
		this.parameterType = "";
		this.parameterValue = "";
		this.description = "";
	}
}
