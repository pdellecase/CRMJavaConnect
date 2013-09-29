package com.cloudliner.integration.msdc11.connect;

import java.util.Iterator;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;

/***
 * Handler for SOAP header.
 *
 */
public final class Axis2MustUnderstandChecker extends AbstractHandler {

    public Axis2MustUnderstandChecker() {
    }
    
    
    /* (non-Javadoc)
     * Process the Security SOAP header block from the message context header. 
     * @see org.apache.axis2.engine.Handler#invoke(org.apache.axis2.context.MessageContext)
     */
    public InvocationResponse invoke(MessageContext msgContext)
            throws AxisFault {

        SOAPHeader header = msgContext.getEnvelope().getHeader();

        if (header != null) {
            Iterator<?> blocks = header.examineAllHeaderBlocks();

            while (blocks.hasNext()) {
                SOAPHeaderBlock block = (SOAPHeaderBlock) blocks.next();

                if(block != null){                	
                	if (block.getLocalName().equals("Security")) {
                		block.setProcessed();
                	}
                }
            }
        }
        return InvocationResponse.CONTINUE;
    }
}
