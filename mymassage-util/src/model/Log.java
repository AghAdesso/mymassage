package model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "log")
@NamedQuery(name = "Log.findAllByDate", query = "select o from Log o order by o.date desc")
public class Log implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;
	
	@Column(name = "date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	@Column(name = "type", nullable = false)
	private String type;
	
	@Column(name = "severity", nullable = false)
	private String severity;
	
	@Column(name = "content", nullable = false)
	private String content;
	
	@Column(name = "userLogin", nullable = false)
	private String userLogin;
	
	public Log() {
		this.id = null;
		this.date = new Date();
		this.type = "";
		this.severity = "";
		this.content = "";
		this.userLogin = "";
	}
}
