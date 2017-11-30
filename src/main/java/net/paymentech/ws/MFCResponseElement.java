/**
 * MFCResponseElement.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.paymentech.ws;

public class MFCResponseElement  implements java.io.Serializable {
    private java.lang.String version;

    private java.lang.String bin;

    private java.lang.String merchantID;

    private java.lang.String terminalID;

    private java.lang.String orderID;

    private java.lang.String txRefNum;

    private java.lang.String txRefIdx;

    private java.lang.String splitTxRefIdx;

    private java.lang.String amount;

    private java.lang.String respDateTime;

    private java.lang.String procStatus;

    private java.lang.String procStatusMessage;

    private java.lang.String approvalStatus;

    private java.lang.String respCode;

    private java.lang.String avsRespCode;

    private java.lang.String authorizationCode;

    private java.lang.String respCodeMessage;

    private java.lang.String hostRespCode;

    private java.lang.String hostAVSRespCode;

    private java.lang.String retryTrace;

    private java.lang.String retryAttempCount;

    private java.lang.String lastRetryDate;

    private java.lang.String txnSurchargeAmt;

    private java.lang.String tokenAssuranceLevel;

    private java.lang.String dpanAccountStatus;

    public MFCResponseElement() {
    }

    public MFCResponseElement(
           java.lang.String version,
           java.lang.String bin,
           java.lang.String merchantID,
           java.lang.String terminalID,
           java.lang.String orderID,
           java.lang.String txRefNum,
           java.lang.String txRefIdx,
           java.lang.String splitTxRefIdx,
           java.lang.String amount,
           java.lang.String respDateTime,
           java.lang.String procStatus,
           java.lang.String procStatusMessage,
           java.lang.String approvalStatus,
           java.lang.String respCode,
           java.lang.String avsRespCode,
           java.lang.String authorizationCode,
           java.lang.String respCodeMessage,
           java.lang.String hostRespCode,
           java.lang.String hostAVSRespCode,
           java.lang.String retryTrace,
           java.lang.String retryAttempCount,
           java.lang.String lastRetryDate,
           java.lang.String txnSurchargeAmt,
           java.lang.String tokenAssuranceLevel,
           java.lang.String dpanAccountStatus) {
           this.version = version;
           this.bin = bin;
           this.merchantID = merchantID;
           this.terminalID = terminalID;
           this.orderID = orderID;
           this.txRefNum = txRefNum;
           this.txRefIdx = txRefIdx;
           this.splitTxRefIdx = splitTxRefIdx;
           this.amount = amount;
           this.respDateTime = respDateTime;
           this.procStatus = procStatus;
           this.procStatusMessage = procStatusMessage;
           this.approvalStatus = approvalStatus;
           this.respCode = respCode;
           this.avsRespCode = avsRespCode;
           this.authorizationCode = authorizationCode;
           this.respCodeMessage = respCodeMessage;
           this.hostRespCode = hostRespCode;
           this.hostAVSRespCode = hostAVSRespCode;
           this.retryTrace = retryTrace;
           this.retryAttempCount = retryAttempCount;
           this.lastRetryDate = lastRetryDate;
           this.txnSurchargeAmt = txnSurchargeAmt;
           this.tokenAssuranceLevel = tokenAssuranceLevel;
           this.dpanAccountStatus = dpanAccountStatus;
    }


    /**
     * Gets the version value for this MFCResponseElement.
     * 
     * @return version
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this MFCResponseElement.
     * 
     * @param version
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }


    /**
     * Gets the bin value for this MFCResponseElement.
     * 
     * @return bin
     */
    public java.lang.String getBin() {
        return bin;
    }


    /**
     * Sets the bin value for this MFCResponseElement.
     * 
     * @param bin
     */
    public void setBin(java.lang.String bin) {
        this.bin = bin;
    }


    /**
     * Gets the merchantID value for this MFCResponseElement.
     * 
     * @return merchantID
     */
    public java.lang.String getMerchantID() {
        return merchantID;
    }


    /**
     * Sets the merchantID value for this MFCResponseElement.
     * 
     * @param merchantID
     */
    public void setMerchantID(java.lang.String merchantID) {
        this.merchantID = merchantID;
    }


    /**
     * Gets the terminalID value for this MFCResponseElement.
     * 
     * @return terminalID
     */
    public java.lang.String getTerminalID() {
        return terminalID;
    }


    /**
     * Sets the terminalID value for this MFCResponseElement.
     * 
     * @param terminalID
     */
    public void setTerminalID(java.lang.String terminalID) {
        this.terminalID = terminalID;
    }


    /**
     * Gets the orderID value for this MFCResponseElement.
     * 
     * @return orderID
     */
    public java.lang.String getOrderID() {
        return orderID;
    }


    /**
     * Sets the orderID value for this MFCResponseElement.
     * 
     * @param orderID
     */
    public void setOrderID(java.lang.String orderID) {
        this.orderID = orderID;
    }


    /**
     * Gets the txRefNum value for this MFCResponseElement.
     * 
     * @return txRefNum
     */
    public java.lang.String getTxRefNum() {
        return txRefNum;
    }


    /**
     * Sets the txRefNum value for this MFCResponseElement.
     * 
     * @param txRefNum
     */
    public void setTxRefNum(java.lang.String txRefNum) {
        this.txRefNum = txRefNum;
    }


    /**
     * Gets the txRefIdx value for this MFCResponseElement.
     * 
     * @return txRefIdx
     */
    public java.lang.String getTxRefIdx() {
        return txRefIdx;
    }


    /**
     * Sets the txRefIdx value for this MFCResponseElement.
     * 
     * @param txRefIdx
     */
    public void setTxRefIdx(java.lang.String txRefIdx) {
        this.txRefIdx = txRefIdx;
    }


    /**
     * Gets the splitTxRefIdx value for this MFCResponseElement.
     * 
     * @return splitTxRefIdx
     */
    public java.lang.String getSplitTxRefIdx() {
        return splitTxRefIdx;
    }


    /**
     * Sets the splitTxRefIdx value for this MFCResponseElement.
     * 
     * @param splitTxRefIdx
     */
    public void setSplitTxRefIdx(java.lang.String splitTxRefIdx) {
        this.splitTxRefIdx = splitTxRefIdx;
    }


    /**
     * Gets the amount value for this MFCResponseElement.
     * 
     * @return amount
     */
    public java.lang.String getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this MFCResponseElement.
     * 
     * @param amount
     */
    public void setAmount(java.lang.String amount) {
        this.amount = amount;
    }


    /**
     * Gets the respDateTime value for this MFCResponseElement.
     * 
     * @return respDateTime
     */
    public java.lang.String getRespDateTime() {
        return respDateTime;
    }


    /**
     * Sets the respDateTime value for this MFCResponseElement.
     * 
     * @param respDateTime
     */
    public void setRespDateTime(java.lang.String respDateTime) {
        this.respDateTime = respDateTime;
    }


    /**
     * Gets the procStatus value for this MFCResponseElement.
     * 
     * @return procStatus
     */
    public java.lang.String getProcStatus() {
        return procStatus;
    }


    /**
     * Sets the procStatus value for this MFCResponseElement.
     * 
     * @param procStatus
     */
    public void setProcStatus(java.lang.String procStatus) {
        this.procStatus = procStatus;
    }


    /**
     * Gets the procStatusMessage value for this MFCResponseElement.
     * 
     * @return procStatusMessage
     */
    public java.lang.String getProcStatusMessage() {
        return procStatusMessage;
    }


    /**
     * Sets the procStatusMessage value for this MFCResponseElement.
     * 
     * @param procStatusMessage
     */
    public void setProcStatusMessage(java.lang.String procStatusMessage) {
        this.procStatusMessage = procStatusMessage;
    }


    /**
     * Gets the approvalStatus value for this MFCResponseElement.
     * 
     * @return approvalStatus
     */
    public java.lang.String getApprovalStatus() {
        return approvalStatus;
    }


    /**
     * Sets the approvalStatus value for this MFCResponseElement.
     * 
     * @param approvalStatus
     */
    public void setApprovalStatus(java.lang.String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }


    /**
     * Gets the respCode value for this MFCResponseElement.
     * 
     * @return respCode
     */
    public java.lang.String getRespCode() {
        return respCode;
    }


    /**
     * Sets the respCode value for this MFCResponseElement.
     * 
     * @param respCode
     */
    public void setRespCode(java.lang.String respCode) {
        this.respCode = respCode;
    }


    /**
     * Gets the avsRespCode value for this MFCResponseElement.
     * 
     * @return avsRespCode
     */
    public java.lang.String getAvsRespCode() {
        return avsRespCode;
    }


    /**
     * Sets the avsRespCode value for this MFCResponseElement.
     * 
     * @param avsRespCode
     */
    public void setAvsRespCode(java.lang.String avsRespCode) {
        this.avsRespCode = avsRespCode;
    }


    /**
     * Gets the authorizationCode value for this MFCResponseElement.
     * 
     * @return authorizationCode
     */
    public java.lang.String getAuthorizationCode() {
        return authorizationCode;
    }


    /**
     * Sets the authorizationCode value for this MFCResponseElement.
     * 
     * @param authorizationCode
     */
    public void setAuthorizationCode(java.lang.String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }


    /**
     * Gets the respCodeMessage value for this MFCResponseElement.
     * 
     * @return respCodeMessage
     */
    public java.lang.String getRespCodeMessage() {
        return respCodeMessage;
    }


    /**
     * Sets the respCodeMessage value for this MFCResponseElement.
     * 
     * @param respCodeMessage
     */
    public void setRespCodeMessage(java.lang.String respCodeMessage) {
        this.respCodeMessage = respCodeMessage;
    }


    /**
     * Gets the hostRespCode value for this MFCResponseElement.
     * 
     * @return hostRespCode
     */
    public java.lang.String getHostRespCode() {
        return hostRespCode;
    }


    /**
     * Sets the hostRespCode value for this MFCResponseElement.
     * 
     * @param hostRespCode
     */
    public void setHostRespCode(java.lang.String hostRespCode) {
        this.hostRespCode = hostRespCode;
    }


    /**
     * Gets the hostAVSRespCode value for this MFCResponseElement.
     * 
     * @return hostAVSRespCode
     */
    public java.lang.String getHostAVSRespCode() {
        return hostAVSRespCode;
    }


    /**
     * Sets the hostAVSRespCode value for this MFCResponseElement.
     * 
     * @param hostAVSRespCode
     */
    public void setHostAVSRespCode(java.lang.String hostAVSRespCode) {
        this.hostAVSRespCode = hostAVSRespCode;
    }


    /**
     * Gets the retryTrace value for this MFCResponseElement.
     * 
     * @return retryTrace
     */
    public java.lang.String getRetryTrace() {
        return retryTrace;
    }


    /**
     * Sets the retryTrace value for this MFCResponseElement.
     * 
     * @param retryTrace
     */
    public void setRetryTrace(java.lang.String retryTrace) {
        this.retryTrace = retryTrace;
    }


    /**
     * Gets the retryAttempCount value for this MFCResponseElement.
     * 
     * @return retryAttempCount
     */
    public java.lang.String getRetryAttempCount() {
        return retryAttempCount;
    }


    /**
     * Sets the retryAttempCount value for this MFCResponseElement.
     * 
     * @param retryAttempCount
     */
    public void setRetryAttempCount(java.lang.String retryAttempCount) {
        this.retryAttempCount = retryAttempCount;
    }


    /**
     * Gets the lastRetryDate value for this MFCResponseElement.
     * 
     * @return lastRetryDate
     */
    public java.lang.String getLastRetryDate() {
        return lastRetryDate;
    }


    /**
     * Sets the lastRetryDate value for this MFCResponseElement.
     * 
     * @param lastRetryDate
     */
    public void setLastRetryDate(java.lang.String lastRetryDate) {
        this.lastRetryDate = lastRetryDate;
    }


    /**
     * Gets the txnSurchargeAmt value for this MFCResponseElement.
     * 
     * @return txnSurchargeAmt
     */
    public java.lang.String getTxnSurchargeAmt() {
        return txnSurchargeAmt;
    }


    /**
     * Sets the txnSurchargeAmt value for this MFCResponseElement.
     * 
     * @param txnSurchargeAmt
     */
    public void setTxnSurchargeAmt(java.lang.String txnSurchargeAmt) {
        this.txnSurchargeAmt = txnSurchargeAmt;
    }


    /**
     * Gets the tokenAssuranceLevel value for this MFCResponseElement.
     * 
     * @return tokenAssuranceLevel
     */
    public java.lang.String getTokenAssuranceLevel() {
        return tokenAssuranceLevel;
    }


    /**
     * Sets the tokenAssuranceLevel value for this MFCResponseElement.
     * 
     * @param tokenAssuranceLevel
     */
    public void setTokenAssuranceLevel(java.lang.String tokenAssuranceLevel) {
        this.tokenAssuranceLevel = tokenAssuranceLevel;
    }


    /**
     * Gets the dpanAccountStatus value for this MFCResponseElement.
     * 
     * @return dpanAccountStatus
     */
    public java.lang.String getDpanAccountStatus() {
        return dpanAccountStatus;
    }


    /**
     * Sets the dpanAccountStatus value for this MFCResponseElement.
     * 
     * @param dpanAccountStatus
     */
    public void setDpanAccountStatus(java.lang.String dpanAccountStatus) {
        this.dpanAccountStatus = dpanAccountStatus;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MFCResponseElement)) return false;
        MFCResponseElement other = (MFCResponseElement) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.version==null && other.getVersion()==null) || 
             (this.version!=null &&
              this.version.equals(other.getVersion()))) &&
            ((this.bin==null && other.getBin()==null) || 
             (this.bin!=null &&
              this.bin.equals(other.getBin()))) &&
            ((this.merchantID==null && other.getMerchantID()==null) || 
             (this.merchantID!=null &&
              this.merchantID.equals(other.getMerchantID()))) &&
            ((this.terminalID==null && other.getTerminalID()==null) || 
             (this.terminalID!=null &&
              this.terminalID.equals(other.getTerminalID()))) &&
            ((this.orderID==null && other.getOrderID()==null) || 
             (this.orderID!=null &&
              this.orderID.equals(other.getOrderID()))) &&
            ((this.txRefNum==null && other.getTxRefNum()==null) || 
             (this.txRefNum!=null &&
              this.txRefNum.equals(other.getTxRefNum()))) &&
            ((this.txRefIdx==null && other.getTxRefIdx()==null) || 
             (this.txRefIdx!=null &&
              this.txRefIdx.equals(other.getTxRefIdx()))) &&
            ((this.splitTxRefIdx==null && other.getSplitTxRefIdx()==null) || 
             (this.splitTxRefIdx!=null &&
              this.splitTxRefIdx.equals(other.getSplitTxRefIdx()))) &&
            ((this.amount==null && other.getAmount()==null) || 
             (this.amount!=null &&
              this.amount.equals(other.getAmount()))) &&
            ((this.respDateTime==null && other.getRespDateTime()==null) || 
             (this.respDateTime!=null &&
              this.respDateTime.equals(other.getRespDateTime()))) &&
            ((this.procStatus==null && other.getProcStatus()==null) || 
             (this.procStatus!=null &&
              this.procStatus.equals(other.getProcStatus()))) &&
            ((this.procStatusMessage==null && other.getProcStatusMessage()==null) || 
             (this.procStatusMessage!=null &&
              this.procStatusMessage.equals(other.getProcStatusMessage()))) &&
            ((this.approvalStatus==null && other.getApprovalStatus()==null) || 
             (this.approvalStatus!=null &&
              this.approvalStatus.equals(other.getApprovalStatus()))) &&
            ((this.respCode==null && other.getRespCode()==null) || 
             (this.respCode!=null &&
              this.respCode.equals(other.getRespCode()))) &&
            ((this.avsRespCode==null && other.getAvsRespCode()==null) || 
             (this.avsRespCode!=null &&
              this.avsRespCode.equals(other.getAvsRespCode()))) &&
            ((this.authorizationCode==null && other.getAuthorizationCode()==null) || 
             (this.authorizationCode!=null &&
              this.authorizationCode.equals(other.getAuthorizationCode()))) &&
            ((this.respCodeMessage==null && other.getRespCodeMessage()==null) || 
             (this.respCodeMessage!=null &&
              this.respCodeMessage.equals(other.getRespCodeMessage()))) &&
            ((this.hostRespCode==null && other.getHostRespCode()==null) || 
             (this.hostRespCode!=null &&
              this.hostRespCode.equals(other.getHostRespCode()))) &&
            ((this.hostAVSRespCode==null && other.getHostAVSRespCode()==null) || 
             (this.hostAVSRespCode!=null &&
              this.hostAVSRespCode.equals(other.getHostAVSRespCode()))) &&
            ((this.retryTrace==null && other.getRetryTrace()==null) || 
             (this.retryTrace!=null &&
              this.retryTrace.equals(other.getRetryTrace()))) &&
            ((this.retryAttempCount==null && other.getRetryAttempCount()==null) || 
             (this.retryAttempCount!=null &&
              this.retryAttempCount.equals(other.getRetryAttempCount()))) &&
            ((this.lastRetryDate==null && other.getLastRetryDate()==null) || 
             (this.lastRetryDate!=null &&
              this.lastRetryDate.equals(other.getLastRetryDate()))) &&
            ((this.txnSurchargeAmt==null && other.getTxnSurchargeAmt()==null) || 
             (this.txnSurchargeAmt!=null &&
              this.txnSurchargeAmt.equals(other.getTxnSurchargeAmt()))) &&
            ((this.tokenAssuranceLevel==null && other.getTokenAssuranceLevel()==null) || 
             (this.tokenAssuranceLevel!=null &&
              this.tokenAssuranceLevel.equals(other.getTokenAssuranceLevel()))) &&
            ((this.dpanAccountStatus==null && other.getDpanAccountStatus()==null) || 
             (this.dpanAccountStatus!=null &&
              this.dpanAccountStatus.equals(other.getDpanAccountStatus())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        if (getBin() != null) {
            _hashCode += getBin().hashCode();
        }
        if (getMerchantID() != null) {
            _hashCode += getMerchantID().hashCode();
        }
        if (getTerminalID() != null) {
            _hashCode += getTerminalID().hashCode();
        }
        if (getOrderID() != null) {
            _hashCode += getOrderID().hashCode();
        }
        if (getTxRefNum() != null) {
            _hashCode += getTxRefNum().hashCode();
        }
        if (getTxRefIdx() != null) {
            _hashCode += getTxRefIdx().hashCode();
        }
        if (getSplitTxRefIdx() != null) {
            _hashCode += getSplitTxRefIdx().hashCode();
        }
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getRespDateTime() != null) {
            _hashCode += getRespDateTime().hashCode();
        }
        if (getProcStatus() != null) {
            _hashCode += getProcStatus().hashCode();
        }
        if (getProcStatusMessage() != null) {
            _hashCode += getProcStatusMessage().hashCode();
        }
        if (getApprovalStatus() != null) {
            _hashCode += getApprovalStatus().hashCode();
        }
        if (getRespCode() != null) {
            _hashCode += getRespCode().hashCode();
        }
        if (getAvsRespCode() != null) {
            _hashCode += getAvsRespCode().hashCode();
        }
        if (getAuthorizationCode() != null) {
            _hashCode += getAuthorizationCode().hashCode();
        }
        if (getRespCodeMessage() != null) {
            _hashCode += getRespCodeMessage().hashCode();
        }
        if (getHostRespCode() != null) {
            _hashCode += getHostRespCode().hashCode();
        }
        if (getHostAVSRespCode() != null) {
            _hashCode += getHostAVSRespCode().hashCode();
        }
        if (getRetryTrace() != null) {
            _hashCode += getRetryTrace().hashCode();
        }
        if (getRetryAttempCount() != null) {
            _hashCode += getRetryAttempCount().hashCode();
        }
        if (getLastRetryDate() != null) {
            _hashCode += getLastRetryDate().hashCode();
        }
        if (getTxnSurchargeAmt() != null) {
            _hashCode += getTxnSurchargeAmt().hashCode();
        }
        if (getTokenAssuranceLevel() != null) {
            _hashCode += getTokenAssuranceLevel().hashCode();
        }
        if (getDpanAccountStatus() != null) {
            _hashCode += getDpanAccountStatus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MFCResponseElement.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "MFCResponseElement"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("version");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "version"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bin");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "bin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "merchantID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "terminalID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "orderID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txRefNum");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "txRefNum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txRefIdx");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "txRefIdx"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("splitTxRefIdx");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "splitTxRefIdx"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("respDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "respDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("procStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "procStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("procStatusMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "procStatusMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("approvalStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "approvalStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("respCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "respCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("avsRespCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "avsRespCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authorizationCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "authorizationCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("respCodeMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "respCodeMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostRespCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "hostRespCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostAVSRespCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "hostAVSRespCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("retryTrace");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "retryTrace"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("retryAttempCount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "retryAttempCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastRetryDate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "lastRetryDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txnSurchargeAmt");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "txnSurchargeAmt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tokenAssuranceLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "tokenAssuranceLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dpanAccountStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:ws.paymentech.net/PaymentechGateway", "dpanAccountStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
