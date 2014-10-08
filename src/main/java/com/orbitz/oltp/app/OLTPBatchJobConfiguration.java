package com.orbitz.oltp.app;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.CompositeItemWriteListener;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.SimplePartitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import com.orbitz.oltp.app.config.OLTPBatchJobConfig;
import com.orbitz.oltp.db.MemberRowMapper;
import com.orbitz.oltp.db.model.MemberTrip;
import com.orbitz.oltp.processor.MemberTripProcessor;
import com.orbitz.oltp.util.NovoHTMLGrabber;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class OLTPBatchJobConfiguration {

     @Autowired
     @Resource(name = "baseDatasource")
     protected DataSource dataSource;
     
     @Value("${oltp.batch.totalThreads}")
     protected Integer gridSize;
     
     @Value("${oltp.batch.totalRecords}")
     protected Integer recordsPerThread;
     
     @Value("${oltp.batch.processName}")
     protected String batchProcessName;
     
     @Value("${oltp.batch.posId}")
     protected String posId;
     
     
     
     protected Logger log = Logger.getLogger(OLTPBatchJobConfiguration.class);

//    @Autowired
//    OLTPBatchJobConfig batchJobConfig;

    // @Autowired
    // PlatformTransactionManager transactionManager;
    
    
    @PostConstruct
    public void init(){
//        posId = System.getProperty("oltp.batch.posId");
//        batchProcessName = System.getProperty("oltp.batch.name");
//        recordsPerThread = Integer.valueOf(System.getProperty("oltp.batch.recordsPerThread"));
//        gridSize = Integer.valueOf(System.getProperty("oltp.batch.gridSize"));
        
        batchProcessName += System.currentTimeMillis();
    }
    
    
    @Primary
    @Bean(name = "baseDatasource")
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        // dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        dataSource.setDriverClassName(batchJobConfig().getJdbcDriver());

        // FQA1
        // dataSource.setUrl("jdbc:oracle:thin:@//ex12-scan.dev.orbitz.net:1521/qa1_cus");
        // dataSource.setUsername("customer_user");
        // dataSource.setPassword("agoBhc02");

        // DEV
        // dataSource.setUrl("jdbc:oracle:thin:@ex12-scan.dev.orbitz.net:1521/dev_cus");
        // dataSource.setUsername("customer_user");
        // dataSource.setPassword("hsiT#cw01");

        dataSource.setUrl(batchJobConfig().getJdbcConnectionString());
        dataSource.setUsername(batchJobConfig().getJdbcUsername());
        dataSource.setPassword(batchJobConfig().getJdbcPassword());

        return dataSource;

        // BasicDataSource dataSource = new BasicDataSource();
        // dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        // dataSource.setUrl("jdbc:mysql://localhost/oltp_export");
        // dataSource.setUsername("root");
        // return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @StepScope
    @Bean
    public JdbcCursorItemReader<MemberTrip> reader(@Value("#{stepExecutionContext[fromId]}") final Integer fromId,
            @Value("#{stepExecutionContext[toId]}") final Integer toId) {
        JdbcCursorItemReader<MemberTrip> reader = new JdbcCursorItemReader<MemberTrip>();
        reader.setDataSource(dataSource);
        // reader.setSql("select cm.CUSTOMER_MEMBER_ID, cm.PII_EMAIL, tp.ORBITZLOCATORCODE, tp.REF_POINT_OF_SALE_ID, tp.CUSTOMER_CLASSIC_TRAVELPLAN_ID "
        // +
        // "from CUSTOMER_CLASSIC_TRAVELPLAN tp " +
        // "join CUSTOMER_MEMBER cm on cm.CUSTOMER_MEMBER_ID = tp.MEMBERID " +
        // "join CUSTOMER_CLASSIC_TRIP_DETAIL cctd on cctd.CUSTOMER_CLASSIC_TRAVELPLAN_ID is NULL");
        reader.setPreparedStatementSetter(new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, Integer.valueOf(posId));
                ps.setInt(2, fromId);
                ps.setInt(3, toId);
            }
        });
        reader.setSql(batchJobConfig().getLocatorQuery());
        // //reader.setSql("Select MEMBER_ID, MEMBER_EMAIL, TRIP_LOCATOR, PROCESSED from travelplan where processed = 1");
        // reader.setSql("Select MEMBERID, ORBITZLOCATORCODE from CUSTOMER_CLASSIC_TRAVELPLAN");
        reader.setRowMapper(new MemberRowMapper());
        return reader;
    }
    
//    @StepScope
//    @Bean
//    public JdbcPagingItemReader<MemberTrip> reader2(){
//        JdbcPagingItemReader<MemberTrip> reader = new JdbcPagingItemReader<MemberTrip>();
//        reader.setDataSource(dataSource);
//        reader.setPageSize(100);
//        reader.setRowMapper(new MemberRowMapper());
//        reader.setQueryProvider(
//                   new SqlPagingQueryProviderFactoryBean()
//                   .set
//                   .setSelectClause("")
//                   
//        );
//        return reader;
//    }

    @Bean
    public ItemProcessor<MemberTrip, MemberTrip> processor() {
        return new MemberTripProcessor();
    }

    @Bean
    public ItemWriter<? super MemberTrip> writer() {
        JdbcBatchItemWriter<MemberTrip> writer = new JdbcBatchItemWriter<MemberTrip>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<MemberTrip>());
        // writer.setSql("INSERT INTO trip_details_json(TRIP_LOCATOR, MEMBER_ID, JSON_STRING) values(:tripLocator, :memberId, :jsonString)");
        // writer.setSql("INSERT INTO CUSTOMER_CLASSIC_TRIP_DETAIL(ORBITZLOCATORCODE, CUSTOMER_CLASSIC_TRAVELPLAN_ID, PII_TRIP_DETAIL, REF_POINT_OF_SALE_ID) "
        // +
        // "values(:tripLocator, :travelPlanId, :jsonString, :posId)");
        writer.setSql(batchJobConfig().getClassicTripDetailQuery());
        writer.setDataSource(dataSource);
        return writer;
    }

    // @Bean
    // public ItemWriter<MemberTrip> writer2(){
    // JdbcBatchItemWriter<MemberTrip> writer = new
    // JdbcBatchItemWriter<MemberTrip>();
    // writer.setItemSqlParameterSourceProvider(new
    // BeanPropertyItemSqlParameterSourceProvider<MemberTrip>());
    // writer.setSql("UPDATE travelplan set processed = 1 where MEMBER_ID = :memberId and TRIP_LOCATOR = :tripLocator");
    // writer.setDataSource(dataSource);
    // return writer;
    // }

    @Bean
    public CompositeItemWriter<MemberTrip> compositeWriter(DataSource dataSource) {

        List<ItemWriter<? super MemberTrip>> writers = new ArrayList<ItemWriter<? super MemberTrip>>();
        writers.add(writer());

        CompositeItemWriter<MemberTrip> compositeWriter = new CompositeItemWriter<MemberTrip>();
        compositeWriter.setDelegates(writers);

        return compositeWriter;
    }

    @Bean
    public Job importUserJob(JobBuilderFactory jobs, Step partitionStep) {
        return jobs.get(batchProcessName)
                .incrementer(new RunIdIncrementer())
                //.flow(step).end()
                .start(partitionStep)
                .listener(jobStatusExecutionListener())
                .build();
    }

    @Bean
    public JobExecutionListener jobStatusExecutionListener() {
        return new JobExecutionListener() {

            public void beforeJob(JobExecution jobExecution) {
                // send mail before starting
                sendMail(batchJobConfig().getEmailSubjectSuccessStart(),
                        "ahmed.bhaila@orbitzworldwide.com",
                        batchJobConfig().getEmailBodySuccessStart(), null);
            }

            public void afterJob(JobExecution jobExecution) {
                // send mail after job execution completion
                String exitCode = jobExecution.getExitStatus().getExitCode();
                if (exitCode.equals(ExitStatus.COMPLETED.getExitCode())) {
                    sendMail(batchJobConfig().getEmailSubjectSuccessEnd(),
                            "ahmed.bhaila@orbitzworldwide.com",
                            batchJobConfig().getEmailBodySuccessEnd(), null);
                } else {
                    sendMail(batchJobConfig().getEmailSubjectFailure(),
                            "ahmed.bhaila@orbitzworldwide.com",
                            batchJobConfig().getEmailBodyFailure(),
                            jobExecution.getExecutionContext());
                }

            }
        };
    }

    private void sendMail(String subject, String toEmail, String messageBody,
            ExecutionContext context) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("mojo_jo_jo@mobilejourney");
        message.setTo(toEmail);
        message.setSubject(subject);
        if (context != null) {
            Map<String, String> values = (Map<String, String>) context
                    .get("values");
            if (values  != null && values.containsKey("error")) {
                messageBody += context.getString("error");
            }
        }
        message.setText(messageBody);
        mailSender().send(message);

    }

    @Bean
    public MailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(batchJobConfig().getSmtpServer());
        mailSender.setPort(Integer.parseInt(batchJobConfig().getSmtpPort()));
        mailSender.setUsername(batchJobConfig().getSmtpUsername());
        mailSender.setPassword(batchJobConfig().getSmtpPassword());

        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", batchJobConfig().getSmtpAuth());
        props.setProperty("mail.smtp.starttls.enable",
                batchJobConfig().getSmtpTls());

        // mailSender.setHost("smtp.gmail.com");
        // mailSender.setPort(587);
        // mailSender.setUsername("ahmed.bhaila@gmail.com");
        // mailSender.setPassword("edvethygzysrtkhe");
        //
        // Properties props = new Properties();
        // props.setProperty("mail.smtp.auth", "true");
        // props.setProperty("mail.smtp.starttls.enable", "true");

        mailSender.setJavaMailProperties(props);
        return mailSender;
    }
    
    @Bean
    public Step partitionStep(StepBuilderFactory stepBuilderFactory,
            ItemReader<MemberTrip> reader,
            CompositeItemWriter<MemberTrip> writer,
            ItemProcessor<MemberTrip, MemberTrip> processor){
        return stepBuilderFactory
                .get("partitionStep")
                .partitioner(step(stepBuilderFactory, reader, writer, processor))
                .partitioner("step", rangePartitioner())
                .gridSize(gridSize == null ? 11 : gridSize)
                .taskExecutor(taskExecutor())
                .build();
    }
    
    @Bean
    public Partitioner simplePartitioner(){
        return new SimplePartitioner();
    }
    
    @Bean
    public Partitioner rangePartitioner(){
        return new Partitioner() {
            
                        
            public Map<String, ExecutionContext> partition(int gridSize) {
                Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
                int range = recordsPerThread == null ? 500 : recordsPerThread;
                int fromId = 1;
                int toId = range;
                
                for(int i = 0; i <= gridSize; i ++){
                    ExecutionContext value = new ExecutionContext();
                    
                    System.out.println("\n Starting Thread " + i);
                    System.out.println("fromId: " + fromId);
                    System.out.println("toId: " + toId);
                    
                    value.putInt("fromId", fromId);
                    value.putInt("toId", toId);
                    
                    value.putString("name", "Thread" + i);
                    result.put("partition" + i, value);
                    
                    fromId = toId + 1;
                    toId += range;
                }

                return result;
            }
        };
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory,
            ItemReader<MemberTrip> reader,
            CompositeItemWriter<MemberTrip> writer,
            ItemProcessor<MemberTrip, MemberTrip> processor) {
        return stepBuilderFactory
                .get("step")
                .<MemberTrip, MemberTrip> chunk(1000)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(org.springframework.dao.DataIntegrityViolationException.class)
                .skipLimit(1000)
                //.transactionManager(transactionManager)
                .transactionAttribute(
                        new DefaultTransactionAttribute(
                                TransactionDefinition.ISOLATION_SERIALIZABLE))
                .transactionAttribute(
                        new DefaultTransactionAttribute(
                                TransactionDefinition.ISOLATION_READ_COMMITTED))
                .transactionAttribute(
                        new DefaultTransactionAttribute(
                                TransactionDefinition.PROPAGATION_NOT_SUPPORTED))
                .build();
    }
    
    @Bean
    public TaskExecutor taskExecutor(){
        //ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        AsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        
        //taskExecutor.setMaxPoolSize(10);
        //taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @Bean
    public NovoHTMLGrabber htmlGrabber() {
        return new NovoHTMLGrabber();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public OLTPBatchJobConfig batchJobConfig(){
        return new OLTPBatchJobConfig();
    }
//    @Bean(name = "orbitzDatasource")
//    public DataSource orbitzDataSource() {
//        BasicDataSource ds = new BasicDataSource();
//        ds.setDriverClassName(batchJobConfig().getJdbcDriver());
//
//        ds.setUrl(batchJobConfig().getClassicORBJdbcConnectionString());
//        ds.setUsername(batchJobConfig().getClassicORBJdbcUsername());
//        ds.setPassword(batchJobConfig().getClassicORBJdbcPassword());
//
//        return ds;
//    }
//  
//    @Bean(name = "ctixDatasource")
//    public DataSource ctixDataSource() {
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
