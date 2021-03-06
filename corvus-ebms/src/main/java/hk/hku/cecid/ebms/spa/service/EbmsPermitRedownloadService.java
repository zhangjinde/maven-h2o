package hk.hku.cecid.ebms.spa.service;

import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.util.EbmsMessageStatusReverser;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.soap.SOAPFaultException;
import hk.hku.cecid.piazza.commons.soap.SOAPRequestException;
import hk.hku.cecid.piazza.commons.soap.SOAPResponse;
import hk.hku.cecid.piazza.commons.soap.WebServicesAdaptor;
import hk.hku.cecid.piazza.commons.soap.WebServicesRequest;
import hk.hku.cecid.piazza.commons.soap.WebServicesResponse;

public class EbmsPermitRedownloadService  extends WebServicesAdaptor{
	
	public static String NAMESPACE = "http://service.ebms.edi.cecid.hku.hk/";

	public void serviceRequested(WebServicesRequest request,
            WebServicesResponse response) throws SOAPException,
            DAOException, SOAPFaultException {
		 
		 String msgId = null;
		 boolean wsi = false;
		 
		 SOAPBodyElement[] bodies = (SOAPBodyElement[]) request.getBodies();
	      // WS-I <RequestElement> 
	      if (bodies != null && bodies.length == 1 && 
	    		  isElement(bodies[0], "RequestElement", NAMESPACE)) {
	    	  
	    	 EbmsProcessor.core.log.debug("WS-I Request");
			 wsi = true;
			  
			 SOAPElement[] childElement = getChildElementArray(bodies[0]);
			 msgId = getText(childElement, "messageId");
	      }else {
	    	  EbmsProcessor.core.log.debug("Non WS-I Request"); 
	    	  msgId = getText(bodies, "messageId");		
	      }
	      
	      if (msgId == null) {
	            throw new SOAPFaultException(SOAPFaultException.SOAP_FAULT_CLIENT,
	                    "Missing request information");
	        }
	      
	      EbmsProcessor.core.log.debug("Permit redownload request - Message ID: "
	                + msgId);
	      
	      EbmsMessageStatusReverser 
		  	msgReverser = new EbmsMessageStatusReverser();
	      
	      try{
			  msgReverser.updateToDownload(msgId);
			  generateReply(response, wsi,  msgId);
		  }catch(SOAPRequestException soapReqExp){
			  // This is unexpected exception may cause during
			  // generating response back to client
			  throw new SOAPException (soapReqExp);
		  }catch(DAOException daoExp){
			  // This is unexpected DAOException, which may cause by 
			  // 	1. Table Not Found
			  //	2. Database Connection Closed 
			  throw daoExp;
		  }catch(Exception exp){
			  EbmsProcessor.core.log.error(
					  "Fail to reset INBOX Message["+msgId+"] back to PS", exp);
			  genreateFault(response, msgId, exp.getMessage());
		  }
	}
	
	private void generateReply(WebServicesResponse response, boolean wsi, String messageId)
		throws SOAPRequestException{
		try {	   
        	SOAPElement messageIdElement = createElement("messageId", NAMESPACE, messageId);
        	if (wsi) {
        		EbmsProcessor.core.log.debug("WS-I Response");
        		
        		SOAPElement responseElement = createElement("ResponseElement", NAMESPACE);
                responseElement.addChildElement(messageIdElement);
                response.setBodies(new SOAPElement[] { responseElement });        		
        	} else {
        		EbmsProcessor.core.log.debug("Non WS-I Response");
        		
                response.setBodies(new SOAPElement[] { messageIdElement });        		        		
        	}
        } catch (Exception e) {
            throw new SOAPRequestException("Unable to generate reply message",e);
        }
    }
	
	private void genreateFault(WebServicesResponse response, String messageId, String errMsg)
		throws SOAPException{
		SOAPResponse soapResponse = (SOAPResponse) response.getTarget();
		soapResponse.addFault(SOAPFaultException.SOAP_FAULT_SERVER, null,
				"Failed to update message [" + messageId + "] : " + errMsg);
	}
	
}
