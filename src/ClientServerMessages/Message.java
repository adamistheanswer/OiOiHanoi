package ClientServerMessages;

import java.io.Serializable;

/**
 * @author lxf736
 * @version 2018-03-01
 */

public abstract class Message implements Serializable {

	private String requestMessage;
	private String responseMessage;
	private Boolean responseSuccess;

	public Message(String requestMessage) {
		this.requestMessage = requestMessage;
	}

	public String getRequestMessage() {
		return requestMessage;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public Boolean getResponseSuccess() {
		return this.responseSuccess;
	}

	public void setResponse(String responseMessage, boolean responseSuccess) {
		this.responseMessage = responseMessage;
		this.responseSuccess = responseSuccess;
	}

}
