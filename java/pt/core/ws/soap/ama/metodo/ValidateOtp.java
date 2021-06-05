package pt.core.ws.soap.ama.metodo;

import pt.core.ws.conexao.SendSoap;
import pt.core.ws.soap.ama.resposta.ValidateOtpResposta;
import pt.core.ws.soap.ama.resposta.entity.EntityValidateOtp;
import pt.core.ws.util.Config;
import pt.core.ws.util.Criptografia;

public class ValidateOtp  {

	private final String SOAP =
		"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ama=\"http://Ama.Authentication.Service/\">" +
		"   <soapenv:Header/>" +
		"   <soapenv:Body>" +
		"      <ama:ValidateOtp>" +
		"         <ama:code>%s</ama:code>" +					//1� codido recebido via SMS
		"         <ama:processId>%s</ama:processId>" +			//2� parametro do SCMDSign
		"         <ama:applicationId>%s</ama:applicationId>" +	//3� application Id
		"      </ama:ValidateOtp>" +
		"   </soapenv:Body>" +
		"</soapenv:Envelope>";
	
	//Metodo no endpoint
	private final String SOAPAction = "http://Ama.Authentication.Service/SCMDService/ValidateOtp"; 
	
	private String processoId = null;
	private String codeSMS = null;
	

	/**
	 * Contrutor
	 * 
	 * @param applicationId
	 * @param clientId
	 */
	public ValidateOtp(String processoId, String codeSMS) {
		if(null == processoId || null == codeSMS) return;
		this.processoId = processoId;
		this.codeSMS = new Criptografia().getCifraAMA(codeSMS);
	}
	
	/**
	 * Obt�m a string soap formatada para enviar ao servi�o web
	 * 
	 * @return
	 */
	private String getSOAP() {
		if(null == codeSMS || null==processoId) return null;
		return 
				String.format(
						SOAP,
							codeSMS,
							processoId,
							Config.getApplicationId());
	}
	
	/**
	 * Efetua uma conex�o ao EndPoint e submete request (SOAP).
	 * Se houve uma resposta (envelope) efetua o parse da informa��o, para obten��o da resposta. 
	 */
	public EntityValidateOtp callEndPoint() {
		String resposta = new SendSoap().setConnection(getSOAP(), SOAPAction);
		if(null != resposta) {
			//Nota: aten��o pode existir mais do que um certificado.
			return new ValidateOtpResposta().parseSOAP(resposta);
		}
		return null;
	}

}