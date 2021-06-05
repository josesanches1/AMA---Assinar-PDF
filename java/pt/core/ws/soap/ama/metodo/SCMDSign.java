package pt.core.ws.soap.ama.metodo;

import pt.core.ws.conexao.SendSoap;
import pt.core.ws.soap.ama.resposta.SCMDSignResposta;
import pt.core.ws.soap.ama.resposta.entity.EntitySign;
import pt.core.ws.util.Config;
import pt.core.ws.util.Criptografia;

public class SCMDSign  {

	
	private final String SOAP =
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ama=\"http://Ama.Authentication.Service/\" xmlns:ama1=\"http://schemas.datacontract.org/2004/07/Ama.Structures.CCMovelSignature\">" +
			"<soapenv:Header/>" +
			"<soapenv:Body>" +
			"<ama:SCMDSign>" +
			"<ama:request>" +
			"<ama1:ApplicationId>%s</ama1:ApplicationId>" +	//1� application Id
			"<ama1:DocName>%s</ama1:DocName>" +				//2� nome Documento
			"<ama1:Hash>%s</ama1:Hash>" +					//3� hash documento
			"<ama1:Pin>%s</ama1:Pin>" +						//4� pin
			"<ama1:UserId>%s</ama1:UserId>" +				//5� client Id
			"</ama:request>" +
			"</ama:SCMDSign>" +
			"</soapenv:Body>" +
			"</soapenv:Envelope>";
	
	//Metodo no endpoint
	private final String SOAPAction = "http://Ama.Authentication.Service/SCMDService/SCMDSign"; 
	
	private String nomeDocumento = null;
	private String hashDocumento = null;
	private String pin = null;
	private String clientId = null;
	
	/**
	 * Contrutor
	 * 
	 * @param applicationId
	 * @param clientId
	 */
	public SCMDSign(String nomeDocumento, String hashDocumento, String pin, String clientId) {
		Criptografia criptografia= new Criptografia();
		this.clientId = criptografia.getCifraAMA(clientId);
		this.pin = criptografia.getCifraAMA(pin);
		this.nomeDocumento = nomeDocumento;
		this.hashDocumento = hashDocumento;
		criptografia = null;
	}
	
	/**
	 * Obt�m a string soap formatada para enviar ao servi�o web
	 * 
	 * @return
	 */
	private String getSOAP() {
		if(null == clientId || null == pin) return null;
		return 
				String.format(
						SOAP, 
							Config.getApplicationId(),
							nomeDocumento,
							hashDocumento,
							pin,
							clientId);
	}
	
	/**
	 * Efetua uma conex�o ao EndPoint e submete request (SOAP).
	 * Se houve uma resposta (envelope) efetua o parse da informa��o, para obten��o da resposta. 
	 */
	public EntitySign callEndPoint() {
		if(null == clientId || null == pin) return null;
		String resposta = new SendSoap().setConnection(getSOAP(), SOAPAction);
		if(null != resposta) {
			//Nota: aten��o pode existir mais do que um certificado.
			return new SCMDSignResposta().parseSOAP(resposta);
		}
		return null;
	}
}