package com.orbitz.oltp.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages="com.orbitz.oltp")
@EnableAutoConfiguration
public class Application {
	public static void main(String[] args){
	    //SpringApplication.run(Application.class, args);
	    // arg[0] - Repeat Count
	    // arg[1] - Total Threads
	    // args[2] - Records to process
	    // args[3] - Sleep time between batches
	    // args[4] - Process name
	    //for(int i = 0; i < Integer.valueOf(args[0]); i ++){
	        System.exit(SpringApplication.exit(SpringApplication.run(Application.class, args)));
	        try{
	            //Thread.sleep(60000);
	        }
	        catch(Exception e){
	            System.err.println(e.getMessage());
	        }
	    //}
	}
}
