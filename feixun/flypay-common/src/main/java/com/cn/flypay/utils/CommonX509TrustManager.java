package com.cn.flypay.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class CommonX509TrustManager implements X509TrustManager{
	public void checkClientTrusted(X509Certificate[] x509Certificates, String s)throws CertificateException{
	}

	public void checkServerTrusted(X509Certificate[] x509Certificates, String s)throws CertificateException{
	}

	public X509Certificate[] getAcceptedIssuers(){
	    return null;
	}
}