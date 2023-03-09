package pdf;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PDFRegistration implements Serializable {

	private String dateSelected;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String comment;
	private String hour;
	
	public PDFRegistration() {
		super();
		this.dateSelected = "";
		this.firstName = "";
		this.lastName = "";
		this.phoneNumber = "";
		this.comment = "";
		this.hour = "";
	}
}
