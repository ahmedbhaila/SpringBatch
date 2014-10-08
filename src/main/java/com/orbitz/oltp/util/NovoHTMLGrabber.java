package com.orbitz.oltp.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.orbitz.oltp.app.config.OLTPBatchJobConfig;

//@Configuration("novoHTMLGrabber")
//@Component
public class NovoHTMLGrabber {
	
	
	private static String ADTURL = "http://adt.qa1.orbztest.com/App/LoginOSR?&username=adtfqa1&osrpw=5OWs5ycd3iJJM/%2bh%2bzjygA%3D%3D&customerid=";
	private static String MEMBERTRIPURL = "https://qa1.orbztest.com/Secure/PerformDisplayActiveOTPDetails?selectedTravelPlanLocatorCode=";
	
	
    // PROD URLs
//	private static String ADTURL = "http://adt.orbitz.com/App/LoginOSR?&username=AGENT1&osrpw=PRC&customerid=";
//    private static String MEMBERTRIPURL = "https://www.orbitz.com/Secure/PerformDisplayActiveOTPDetails?selectedTravelPlanLocatorCode=";
//	
	protected RequestConfig globalConfig;
	protected SSLContextBuilder builder;
	protected SSLConnectionSocketFactory sslsf;
	protected CookieStore cookieStore;
	protected CloseableHttpClient client;
	
	protected Integer currentMemberId = 0;
	
	@Value("${oltp.batch.posId}")
    protected String posId;
	
	@Autowired
	protected OLTPBatchJobConfig batchJobConfig;
	
	BasicDataSource orbitzDatasource;
	
	BasicDataSource ctixDatasource;
	
	@PostConstruct
	public void init(){
		try{
			globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();
			builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			sslsf = new SSLConnectionSocketFactory(builder.build());
			cookieStore = new BasicCookieStore();
			client = HttpClients.custom()
					.setDefaultCookieStore(cookieStore)
					.setDefaultRequestConfig(globalConfig)
					.setSSLSocketFactory(sslsf)
					.build();
			
			System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");

			System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");

			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");

			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
			
			//posId = System.getProperty("oltp.batch.posId");
			
			
            orbitzDatasource = new BasicDataSource();
            orbitzDatasource.setDriverClassName(batchJobConfig.getJdbcDriver());

            orbitzDatasource.setUrl(batchJobConfig
                    .getClassicORBJdbcConnectionString());
            orbitzDatasource.setUsername(batchJobConfig
                    .getClassicORBJdbcUsername());
            orbitzDatasource.setPassword(batchJobConfig
                    .getClassicORBJdbcPassword());

            ctixDatasource = new BasicDataSource();
            ctixDatasource.setDriverClassName(batchJobConfig.getJdbcDriver());

            ctixDatasource.setUrl(batchJobConfig
                    .getClassicCTIXJdbcConnectionString());
            ctixDatasource.setUsername(batchJobConfig
                    .getClassicCTIXJdbcUsername());
            ctixDatasource.setPassword(batchJobConfig
                    .getClassicCTIXJdbcPassword());
			        
			
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	}
	
	public String getHTML(Integer memberId, String recordLocator){
		
		HttpResponse response = null;
		HttpGet httpGet = null;
		String content = null;
		try{
			// only make an ADT call if a call was not made previously
			if(!(memberId.equals(currentMemberId))){			    
				try {
				    cookieStore = new BasicCookieStore();
				    client = HttpClients.custom()
		                    .setDefaultCookieStore(cookieStore)
		                    .setDefaultRequestConfig(globalConfig)
		                    .setSSLSocketFactory(sslsf)
		                    .build();
					currentMemberId = memberId;
//					System.out.println("Member ADT URL is " + ADTURL
//							+ memberId);
					String memberEmail = getMemberEmail(memberId);
					System.out.println("Member Email is " + memberEmail);
					
					httpGet = new HttpGet(ADTURL + memberEmail);
					// http://adt.qa1.orbztest.com/App/LoginOSR?&username=adtfqa1&osrpw=5OWs5ycd3iJJM/%2bh%2bzjygA%3D%3D&customerid=000173@orbitz.com

					// httpGet = new
					// HttpGet("http://adt.qa1.orbztest.com/App/LoginOSR?&username=adtfqa1&osrpw=5OWs5ycd3iJJM/%2bh%2bzjygA%3D%3D&customerid=000173@orbitz.com");
					response = client.execute(httpGet);
					//if(response.getStatusLine().getStatusCode() != HttpStatus.SC_NOT_FOUND){
					    
			            //System.out.println("Response line is " + response.getStatusLine().getStatusCode());
			            
					//}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			
			System.out.println("Member Trip URL is " + MEMBERTRIPURL + recordLocator);
            httpGet = new HttpGet(MEMBERTRIPURL + recordLocator);
            response = client.execute(httpGet);
			if(response.getStatusLine().getStatusCode() != HttpStatus.SC_NOT_FOUND){
                content = new String(EntityUtils.toByteArray(response.getEntity()));
            }
			//Thread.sleep(10);
			//System.out.println("Content is " + content);
		}
		catch(Exception e){
			System.out.println("MEMBERTRIPURL Error " + e.getMessage());
		}
		finally{
			
		}
		return content;
	}
	
	private String getMemberEmail(Integer memberId){
	    String memberEmail = "";
	    Connection conn = null;
        try {
            conn = (posId.equals("1") ? orbitzDatasource.getConnection()
                    : ctixDatasource.getConnection());
            PreparedStatement ps = conn.prepareStatement(batchJobConfig
                    .getClassicMemberEmailQuery());
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                memberEmail = rs.getString(1);
            }
            ps.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally{
            if(conn != null){
                try{
                    conn.close();
                }
                catch(Exception e){
                    System.err.println(e.getMessage());
                }
            }
        }
        return memberEmail;
	}
//	@Bean(name = "novoConfig")
//    public OLTPBatchJobConfig batchJobConfig(){
//        return new OLTPBatchJobConfig();
//    }
    
//    @Bean(name = "orbitzDatasource")
//    public DataSource orbitzDataSource(){
//        BasicDataSource ds = new BasicDataSource();
//        ds.setDriverClassName(batchJobConfig().getJdbcDriver());
//        
//        ds
//                .setUrl(batchJobConfig().getClassicORBJdbcConnectionString());
//        ds.setUsername(batchJobConfig().getClassicORBJdbcUsername());
//        ds.setPassword(batchJobConfig().getClassicORBJdbcPassword());
//        
//        return ds;
//    }
    
//    @Bean(name = "ctixDatasource")
//    public DataSource ctixDataSource(){
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setDriverClassName(batchJobConfig().getJdbcDriver());
//        
//        dataSource
//                .setUrl(batchJobConfig().getClassicCTIXJdbcConnectionString());
//        dataSource.setUsername(batchJobConfig().getClassicCTIXJdbcUsername());
//        dataSource.setPassword(batchJobConfig().getClassicCTIXJdbcPassword());
//        
//        return dataSource;
//    }
}
