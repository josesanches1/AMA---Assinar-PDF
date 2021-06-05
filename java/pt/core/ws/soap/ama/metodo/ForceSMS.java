package pt.core.ws.soap.ama.metodo;

import pt.core.ws.conexao.SendSoap;
import pt.core.ws.soap.ama.resposta.SCMDSignResposta;
import pt.core.ws.soap.ama.resposta.entity.EntitySign;
import pt.core.ws.util.Config;
import pt.core.ws.util.Criptografia;

public class ForceSMS  {

	
	private final String SOAP =
		"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ama=\"http://Ama.Authentication.Service/\">" +
		"<soapenv:Header/>" +
		"<soapenv:Body>" +
		"<ama:ForceSMS>" +
		"<ama:processId>%s</ama:processId>" +			//1� processo Id
		"<ama:citizenId>%s</ama:citizenId>" +			//2� client Id
		"<ama:applicationId>%s</ama:applicationId>" +	//3� aplication Id
		"</ama:ForceSMS>" +
		"</soapenv:Body>" +
		"</soapenv:Envelope>";
	
	//Metodo no endpoint
	private final String SOAPAction = "http://Ama.Authentication.Service/SCMDService/ForceSMS"; 
	
	private String userId = null;
	private String processoId = null;
	
	/**
	 * Contrutor
	 * 
	 * @param applicationId
	 * @param clientId
	 */
	public ForceSMS(String userId, String processoId) {

		Criptografia criptografia= new Criptografia();
		this.userId = criptografia.getCifraAMA(userId);
		criptografia = null;
		this.processoId = processoId;
	}
	
	/**
	 * Obt�m a string soap formatada para enviar ao servi�o web
	 * 
	 * @return
	 */
	private String getSOAP() {
		if(null==userId || null==processoId) return null;

		return 
				String.format(
						SOAP,
							processoId,
							userId,
							Config.getApplicationId());
	}
	
	/**
	 * Efetua uma conex�o ao EndPoint e submete request (SOAP).
	 * Se houve uma resposta (envelope) efetua o parse da informa��o, para obten��o da resposta. 
	 */
	public EntitySign callEndPoint() {
		String resposta = new SendSoap().setConnection(getSOAP(), SOAPAction);
		if(null != resposta) {
			//Nota: aten��o pode existir mais do que um certificado.
			return new SCMDSignResposta().parseSOAP(resposta);
		}
		return null;
	}
}