package com.auditbucket.audit.bean;

import com.auditbucket.audit.model.AuditLog;
import com.auditbucket.audit.model.AuditWhat;

/**
 * User: Mike Holdsworth
 * Since: 4/09/13
 */
public class AuditLogDetailBean {

    private AuditLogDetailBean() {
    }

    public AuditLogDetailBean(AuditLog log, AuditWhat what) {
        this();
        this.log = log;
        this.what = what;

    }

    private AuditLog log;
    private AuditWhat what;

    public AuditLog getLog() {
        return this.log;
    }

    public AuditWhat getWhat() {
        return this.what;
    }

}