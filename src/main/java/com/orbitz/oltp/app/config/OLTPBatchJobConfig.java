package com.orbitz.oltp.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "orbitz.oltp")
public  class OLTPBatchJobConfig {
    private String locatorQuery;
    private String classicTripDetailQuery;
    private String emailSubjectSuccessStart;
    private String emailSubjectSuccessEnd;
    private String emailSubjectFailure;
    private String emailBodySuccessStart;
    private String emailBodySuccessEnd;
    private String emailBodyFailure;

    private String smtpUsername;
    private String smtpPassword;
    private String smtpPort;
    private String smtpServer;
    private String smtpTls;
    private String smtpAuth;
    
    public String getSmtpAuth() {
        return smtpAuth;
    }
    public void setSmtpAuth(String smtpAuth) {
        this.smtpAuth = smtpAuth;
    }
    private String jdbcUsername;
    private String jdbcPassword;
    private String jdbcDriver;
    private String jdbcConnectionString;
    
    private String classicORBJdbcConnectionString;
    private String classicORBJdbcUsername;
    private String classicORBJdbcPassword;
    
    
    private String classicCTIXJdbcConnectionString;
    private String classicCTIXJdbcUsername;
    private String classicCTIXJdbcPassword;
    
    private String classicMemberEmailQuery;
    
    
    public String getClassicMemberEmailQuery() {
        return classicMemberEmailQuery;
    }
    public void setClassicMemberEmailQuery(String classicMemberEmailQuery) {
        this.classicMemberEmailQuery = classicMemberEmailQuery;
    }
    public String getClassicORBJdbcConnectionString() {
        return classicORBJdbcConnectionString;
    }
    public void setClassicORBJdbcConnectionString(
            String classicORBJdbcConnectionString) {
        this.classicORBJdbcConnectionString = classicORBJdbcConnectionString;
    }
    public String getClassicORBJdbcUsername() {
        return classicORBJdbcUsername;
    }
    public void setClassicORBJdbcUsername(String classicORBJdbcUsername) {
        this.classicORBJdbcUsername = classicORBJdbcUsername;
    }
    public String getClassicORBJdbcPassword() {
        return classicORBJdbcPassword;
    }
    public void setClassicORBJdbcPassword(String classicORBJdbcPassword) {
        this.classicORBJdbcPassword = classicORBJdbcPassword;
    }
    public String getClassicCTIXJdbcConnectionString() {
        return classicCTIXJdbcConnectionString;
    }
    public void setClassicCTIXJdbcConnectionString(
            String classicCTIXJdbcConnectionString) {
        this.classicCTIXJdbcConnectionString = classicCTIXJdbcConnectionString;
    }
    public String getClassicCTIXJdbcUsername() {
        return classicCTIXJdbcUsername;
    }
    public void setClassicCTIXJdbcUsername(String classicCTIXJdbcUsername) {
        this.classicCTIXJdbcUsername = classicCTIXJdbcUsername;
    }
    public String getClassicCTIXJdbcPassword() {
        return classicCTIXJdbcPassword;
    }
    public void setClassicCTIXJdbcPassword(String classicCTIXJdbcPassword) {
        this.classicCTIXJdbcPassword = classicCTIXJdbcPassword;
    }
    public String getJdbcUsername() {
        return jdbcUsername;
    }
    public void setJdbcUsername(String jdbcUsername) {
        this.jdbcUsername = jdbcUsername;
    }
    public String getJdbcPassword() {
        return jdbcPassword;
    }
    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }
    public String getJdbcDriver() {
        return jdbcDriver;
    }
    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }
    public String getJdbcConnectionString() {
        return jdbcConnectionString;
    }
    public void setJdbcConnectionString(String jdbcConnectionString) {
        this.jdbcConnectionString = jdbcConnectionString;
    }
    public String getLocatorQuery() {
        return locatorQuery;
    }
    public void setLocatorQuery(String locatorQuery) {
        this.locatorQuery = locatorQuery;
    }
    public String getClassicTripDetailQuery() {
        return classicTripDetailQuery;
    }
    public void setClassicTripDetailQuery(String classicTripDetailQuery) {
        this.classicTripDetailQuery = classicTripDetailQuery;
    }
    public String getEmailSubjectSuccessStart() {
        return emailSubjectSuccessStart;
    }
    public void setEmailSubjectSuccessStart(String emailSubjectSuccessStart) {
        this.emailSubjectSuccessStart = emailSubjectSuccessStart;
    }
    public String getEmailSubjectSuccessEnd() {
        return emailSubjectSuccessEnd;
    }
    public void setEmailSubjectSuccessEnd(String emailSubjectSuccessEnd) {
        this.emailSubjectSuccessEnd = emailSubjectSuccessEnd;
    }
    public String getEmailSubjectFailure() {
        return emailSubjectFailure;
    }
    public void setEmailSubjectFailure(String emailSubjectFailure) {
        this.emailSubjectFailure = emailSubjectFailure;
    }
    public String getEmailBodySuccessStart() {
        return emailBodySuccessStart;
    }
    public void setEmailBodySuccessStart(String emailBodySuccessStart) {
        this.emailBodySuccessStart = emailBodySuccessStart;
    }
    public String getEmailBodySuccessEnd() {
        return emailBodySuccessEnd;
    }
    public void setEmailBodySuccessEnd(String emailBodySuccessEnd) {
        this.emailBodySuccessEnd = emailBodySuccessEnd;
    }
    public String getEmailBodyFailure() {
        return emailBodyFailure;
    }
    public void setEmailBodyFailure(String emailBodyFailure) {
        this.emailBodyFailure = emailBodyFailure;
    }
    public String getSmtpUsername() {
        return smtpUsername;
    }
    public void setSmtpUsername(String smtpUsername) {
        this.smtpUsername = smtpUsername;
    }
    public String getSmtpPassword() {
        return smtpPassword;
    }
    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }
    public String getSmtpPort() {
        return smtpPort;
    }
    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }
    public String getSmtpServer() {
        return smtpServer;
    }
    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }
    public String getSmtpTls() {
        return smtpTls;
    }
    public void setSmtpTls(String smtpTls) {
        this.smtpTls = smtpTls;
    }
    public String getSmptAuth() {
        return smptAuth;
    }
    public void setSmptAuth(String smptAuth) {
        this.smptAuth = smptAuth;
    }
    private String smptAuth;
}