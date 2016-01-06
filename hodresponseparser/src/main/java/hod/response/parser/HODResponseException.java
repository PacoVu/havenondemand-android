package hod.response.parser;

public class HODResponseException extends RuntimeException {

	public int error = 0;
	public String reason = "";
	public String detail = "";
	public HODResponseException(int e, String r, String d) {
		super(r);
		error = e;
		reason = r;
		detail = d;
	}
}
