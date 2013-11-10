package com.auditbucket.spring;

import com.auditbucket.bean.AuditHeaderInputBean;
import com.auditbucket.bean.AuditLogInputBean;
import com.auditbucket.bean.AuditResultBean;
import com.auditbucket.helper.AuditException;
import com.auditbucket.spring.utils.PojoToAbTransformer;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * @deprecated use the one in the hospital importer
 */
public class AbClient {
    private String serverName;
    private String apiKey;
    private String userName;
    private String password;
    private String forteressName;
    private String NEW_LOG;
    private String NEW_HEADER;
    private Random rnd = new Random();

    private Logger logger = LoggerFactory.getLogger(AbClient.class);
    private String user;

    @Override
    public String toString() {
        return "AbClient{" +
                "serverName='" + serverName + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", userName='" + userName + '\'' +
                ", forteressName='" + forteressName + '\'' +
                '}';
    }

    public AbClient(String serverName, String apiKey, String userName, String password, String fortressName) {
        this.serverName = serverName;
        this.apiKey = apiKey;
        this.userName = userName;
        this.password = password;
        this.forteressName = fortressName;
        this.NEW_LOG = this.serverName + "/v1/audit/log";
        this.NEW_HEADER = this.serverName + "/v1/audit/";
        logger.info(this.toString());

    }

    // ToDo: Should only be one method - createAudit(). The creation of a Log happens if there is pojo data to
    //       transmit beyond the AuditHeader Metadata - i.e. un-annotated fields. A header can have an optional Log associated
    //       with it.

    public AuditResultBean createAuditHeader(Object pojo) throws IllegalAccessException, IOException, AuditException {
        AuditHeaderInputBean auditHeaderInputBean = PojoToAbTransformer.transformToAbFormat(pojo);
        auditHeaderInputBean.setFortress(forteressName);
        auditHeaderInputBean.setFortressUser(getUser());
        // ToDo: Remove this - mike debug
        //auditHeaderInputBean.setCallerRef(null);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        HttpHeaders httpHeaders = setHeaders(userName, password);
        HttpEntity<AuditHeaderInputBean> requestEntity = new HttpEntity<AuditHeaderInputBean>(auditHeaderInputBean, httpHeaders);

        // ToDo: audit/header/new is only called if @AuditKey is null, otherwise /audit/log/new is called
        logger.debug("template {}", restTemplate.toString());
        ResponseEntity response = restTemplate.exchange(NEW_HEADER, HttpMethod.POST, requestEntity, AuditResultBean.class);
        // TODO dependeing on  response.getStatusCode() we must throw or not a specific AB exception
        return (AuditResultBean) response.getBody();
    }

    public AuditLogInputBean createAuditLog(Object pojo) throws IllegalAccessException, IOException, AuditException {
        AuditLogInputBean auditLogInputBean = PojoToAbTransformer.transformToAbLogFormat(pojo);
        assert (auditLogInputBean.getAuditKey() != null);
        auditLogInputBean.setFortressUser(getUser());
        HttpHeaders httpHeaders = setHeaders(userName, password);
        HttpEntity<AuditLogInputBean> requestEntity = new HttpEntity<AuditLogInputBean>(auditLogInputBean, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        ResponseEntity response = restTemplate.exchange(NEW_LOG, HttpMethod.POST, requestEntity, AuditLogInputBean.class);

        // TODO depending on  response.getStatusCode() we must throw or not a specific AB exception
        return (AuditLogInputBean) response.getBody();
    }

    private HttpHeaders setHeaders(final String username, final String password) {
        HttpHeaders headers = new HttpHeaders() {
            {
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.encodeBase64(
                        auth.getBytes(Charset.forName("US-ASCII")));
                String authHeader = "Basic " + new String(encodedAuth);
                set("Authorization", authHeader);
            }
        };
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");
        headers.add("Accept-Encoding", "gzip");

        return headers;
    }

    String getUser() {
        int i = rnd.nextInt((100 - 1) + 1);
        return userName + i;
    }
}
